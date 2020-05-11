/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * JOrbis - Copyright (C) 2000 ymnk, JCraft,Inc.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.sound.codec;

import java.io.IOException;
import java.io.InputStream;

import org.thenesis.midpath.sound.AudioFormat;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

/**
 * Takes a vorbis bitstream from an input stream and writes raw stereo PCM to
 * an output stream. Decodes simple and chained OggVorbis files from beginning
 * to end.
 * 
 * @author ymnk <ymnk@jcraft.com>
 * @author Guillaume Legris
 */
public class OggVorbisDecoder implements AudioDecoder {

	public static final int STOPPED = 0;
	public static final int PREPARED = 1;
	public static final int DECODING = 2;
	public static final int CLOSED = 3;

	private static int convsize = 4096 * 2;
	private static byte[] convbuffer = new byte[convsize]; // take 8k out of the data segment, not the stack

	private InputStream input;

	//  Sync and verify incoming physical bitstream
	private SyncState syncState;
	// Take physical pages, weld into a logical stream of packets
	private StreamState streamState;
	// One Ogg bitstream page. Vorbis packets are inside.
	private Page page;
	// One raw packet of data for decode.
	private Packet packet;

	// Struct that stores all the static vorbis bitstream settings
	private Info info;
	// Struct that stores all the bitstream user comments
	private Comment comment;
	// Central working state for the packet->PCM decoder
	private DspState dspState;
	// Local working space for packet->PCM decode
	private Block block;

	private byte[] buffer;
	private int bytes;
	private int eos;
	private int index;

	float[][][] _pcm;
	int[] _index;

	private boolean paused = false;

	private AudioFormat format;

	public OggVorbisDecoder() {
		//System.out.println("[DEBUG] OggVorbisDecoder.<init>()");
	}

	/**
	 * Initialize decoder
	 * 
	 * @throws IOException
	 * @throws IllegalDecoderStateException
	 */
	public void initialize(InputStream is) throws IOException {
		
		/* Decode setup */
		
		// Sync and verify incoming physical bitstream
		syncState = new SyncState();
		// Take physical pages, weld into a logical stream of packets
		streamState = new StreamState();
		// One Ogg bitstream page. Vorbis packets are inside.
		page = new Page();
		// One raw packet of data for decode.
		packet = new Packet();

		// Struct that stores all the static vorbis bitstream settings
		info = new Info();
		// Struct that stores all the bitstream user comments
		comment = new Comment();
		// Central working state for the packet->PCM decoder
		dspState = new DspState();
		// Local working space for packet->PCM decode
		block = new Block(dspState);
		//Now we can read pages
		syncState.init();
		
		/* Init variables */
		
		input = is;
		bytes = 0;
		eos = 0;

		/* Prepare */

		// while (true) { // we repeat if the bitstream is chained
		/* Grab some data at the head of the stream.  We want the first page
		 * (which is guaranteed to be small and only contain the Vorbis
		 * stream initial header). We need the first page to get the stream
		 * serialno. */

		// Submit a 4k block to libvorbis' Ogg layer
		int index = syncState.buffer(4096);
		buffer = syncState.data;
		bytes = input.read(buffer, index, 4096);
		syncState.wrote(bytes);

		// Get the first page.
		if (syncState.pageout(page) != 1) {

			close();

			// Have we simply run out of data?  If so, we're done.
			if (bytes < 4096) {
				// Clean up the framer
				syncState.clear();
				return;
			}

			// Error case. Must not be Vorbis data
			throw new IOException("Input does not appear to be an Ogg bitstream.");
		}

		// Get the serial number and set up the rest of decode.
		// serialno first; use it to set up a logical stream
		streamState.init(page.serialno());

		/* Extract the initial header from the first page and verify that the
		 * Ogg bitstream is in fact Vorbis data
		 *
		 * I handle the initial header first instead of just having the code
		 * read all three Vorbis headers at once because reading the initial
		 * header is an easy way to identify a Vorbis bitstream and it's
		 * useful to see that functionality seperated out. */

		info.init();
		comment.init();
		if (streamState.pagein(page) < 0) {
			// error; stream version mismatch perhaps
			throw new IOException("Error reading first page of Ogg bitstream data.");
		}

		if (streamState.packetout(packet) != 1) {
			// no page? must not be vorbis
			throw new IOException("Error reading initial header packet.");
		}

		if (info.synthesis_headerin(comment, packet) < 0) {
			// error case; not a vorbis header
			throw new IOException("This Ogg bitstream does not contain Vorbis audio data.");
		}

		/* At this point, we're sure we're Vorbis.  We've set up the logical
		 * (Ogg) bitstream decoder.  Get the comment and codebook headers and
		 * set up the Vorbis decoder
		 *
		 * The next two packets in order are the comment and codebook headers.
		 * They're likely large and may span multiple pages.  Thus we reead
		 * and submit data until we get our two pacakets, watching that no
		 * pages are missing.  If a page is missing, error out; losing a
		 * header page is the only place where missing data is fatal. */

		int i = 0;
		while (i < 2) {
			while (i < 2) {

				int result = syncState.pageout(page);
				if (result == 0)
					break; // Need more data
				// Don't complain about missing or corrupt data yet.  We'll
				// catch it at the packet output phase

				if (result == 1) {
					streamState.pagein(page); // we can ignore any errors here
					// as they'll also become apparent
					// at packetout
					while (i < 2) {
						result = streamState.packetout(packet);
						if (result == 0)
							break;
						if (result == -1) {
							// Uh oh; data at some point was corrupted or missing!
							// We can't tolerate that in a header.  Die.
							throw new IOException("Corrupt secondary header.  Exiting.");
						}
						info.synthesis_headerin(comment, packet);
						i++;
					}
				}
			}
			// no harm in not checking before adding more
			index = syncState.buffer(4096);
			buffer = syncState.data;
			try {
				bytes = input.read(buffer, index, 4096);
			} catch (Exception e) {
				throw new IOException("Error when reading data: " + e.getMessage());
			}
			if (bytes == 0 && i < 2) {
				throw new IOException("End of file before finding all Vorbis headers!");
			}
			syncState.wrote(bytes);

		} // while

		// Throw the comments plus a few lines about the bitstream we're
		// decoding
		/*{
		 
		 for (int j = 0; j < comment.comments; j++) {
		 System.err.println(comment.getComment(j));
		 }
		 
		 
		 byte[][] ptr = comment.user_comments;
		 for (int j = 0; j < ptr.length; j++) {
		 if (ptr[j] == null)
		 break;
		 System.err.println(new String(ptr[j], 0, ptr[j].length - 1));
		 }
		 System.err.println("\nBitstream is " + info.channels + " channel, "
		 + info.rate + "Hz");
		 System.err.println("Encoded by: "
		 + new String(comment.vendor, 0, comment.vendor.length - 1) + "\n");
		 }*/

		convsize = 4096 / info.channels;

		// OK, got and parsed all three headers. Initialize the Vorbis
		// packet->PCM decoder.
		dspState.synthesis_init(info); // central decode state
		block.init(dspState); // local state for most of the decode
		// so multiple block decodes can
		// proceed in parallel.  We could init
		// multiple vorbis_block structures
		// for dspState here

		_pcm = new float[1][][];
		_index = new int[info.channels];

		format = new AudioFormat(getSampleRate(), AudioFormat.BITS_16, getChannels(), true, false);

		//System.out.println("[DEBUG] OggVorbisDecoder.initialize(): end");

	}

	//	/**
	//	 * Decodes stream and write resulting PCM audio data to the given output stream.
	//	 * 
	//	 * @throws IOException
	//	 * @throws IllegalDecoderStateException
	//	 */
	//	public void decode(OutputStream output) throws IOException, IllegalDecoderStateException {
	//
	//		if (decoderState != PREPARED) {
	//			throw new IllegalDecoderStateException("Decoder must be prepared before decoding !");
	//		}
	//
	//		decoderState = DECODING;
	//
	//		convsize = 4096 / info.channels;
	//
	//		// OK, got and parsed all three headers. Initialize the Vorbis
	//		// packet->PCM decoder.
	//		dspState.synthesis_init(info); // central decode state
	//		block.init(dspState); // local state for most of the decode
	//		// so multiple block decodes can
	//		// proceed in parallel.  We could init
	//		// multiple vorbis_block structures
	//		// for dspState here
	//
	//		_pcm = new float[1][][];
	//		_index = new int[info.channels];
	//	}

	/**
	 * Decodes stream and write resulting PCM audio data to the given output stream.
	 * 
	 * @throws IOException
	 * @throws IllegalDecoderStateException
	 */
	public int decodeStep(DecoderCallback callback) throws IOException {

		int bytesWritten = 0;

		// The rest is just a straight decode loop until end of stream
		if (eos == 0) {

			while (eos == 0) {

				int result = syncState.pageout(page);
				if (result == 0)
					break; // need more data
				if (result == -1) { // missing or corrupt data at this page position
					System.err.println("Corrupt or missing data in bitstream; continuing...");
				} else {
					streamState.pagein(page); // can safely ignore errors at
					// this point
					while (true) {
						result = streamState.packetout(packet);

						if (result == 0)
							break; // need more data
						if (result == -1) { // missing or corrupt data at this page position
							// no reason to complain; already complained above
						} else {
							// we have a packet.  Decode it
							int samples;
							if (block.synthesis(packet) == 0) { // test for success!
								dspState.synthesis_blockin(block);
							}

							// **pcm is a multichannel float vector.  In stereo, for
							// example, pcm[0] is left, and pcm[1] is right.  samples is
							// the size of each channel.  Convert the float values
							// (-1.<=range<=1.) to whatever PCM format and write it out

							while ((samples = dspState.synthesis_pcmout(_pcm, _index)) > 0) {
								float[][] pcm = _pcm[0];
								boolean clipflag = false;
								int bout = (samples < convsize ? samples : convsize);

								// convert floats to 16 bit signed ints (host order) and
								// interleave
								for (int i = 0; i < info.channels; i++) {
									int ptr = i * 2;
									//int ptr=i;
									int mono = _index[i];
									for (int j = 0; j < bout; j++) {
										int val = (int) (pcm[i][mono + j] * 32767.);
										//		      short val=(short)(pcm[i][mono+j]*32767.);
										//		      int val=(int)Math.round(pcm[i][mono+j]*32767.);
										// might as well guard against clipping
										if (val > 32767) {
											val = 32767;
											clipflag = true;
										}
										if (val < -32768) {
											val = -32768;
											clipflag = true;
										}
										if (val < 0)
											val = val | 0x8000;
										convbuffer[ptr] = (byte) (val);
										convbuffer[ptr + 1] = (byte) (val >>> 8);
										ptr += 2 * (info.channels);
									}
								}

								//if(clipflag)
								//System.err.println("Clipping in frame "+dspState.sequence);

								//System.out.write(convbuffer, 0, 2
								//        * info.channels * bout);

								//System.out.println("write");

								//System.out.println("write");
								callback.write(convbuffer, 0, 2 * info.channels * bout);
								bytesWritten += 2 * info.channels * bout;

								// Tell libvorbis how many samples we actually consumed
								dspState.synthesis_read(bout);

								// Needed to operate in a multi-threaded way 

								if (paused) {

									// Synchronize to own the object's monitor
									synchronized (this) {
										try {
											wait();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										paused = false;
									}

								}

							}
						}
					}
					if (page.eos() != 0)
						eos = 1;
				}
			} // while
			if (eos == 0) {
				index = syncState.buffer(4096);
				buffer = syncState.data;
				bytes = input.read(buffer, index, 4096);
				syncState.wrote(bytes);
				if (bytes == 0)
					eos = 1;
			}
		} else {
			// clean up this logical bitstream; before exit we see if we're
			// followed by another [chained]

			streamState.clear();

			// ogg_page and ogg_packet structs always point to storage in
			// libvorbis.  They're never freed or manipulated directly

			block.clear();
			dspState.clear();
			//info.clear(); // must be called last

			bytesWritten = -1;

		}

		return bytesWritten;

	}

	public synchronized void close() {
		//System.out.println("\n\nDone.");
	}

	public synchronized void pause() {
		paused = true;
	}

	public synchronized void resume() {
		notify();
	}

	public String[] getComments() {

		if (comment == null) {
			return null;
		}

		String[] comments = new String[comment.comments];

		for (int i = 0; i < comment.comments; i++) {
			comments[i] = comment.getComment(i);
		}

		return comments;
	}

	public String[] getUserComments() {

		if (comment == null) {
			return null;
		}

		byte[][] ptr = comment.user_comments;

		String[] comments = new String[ptr.length];

		for (int i = 0; i < ptr.length; i++) {
			if (ptr[i] == null)
				break;
			comments[i] = new String(ptr[i], 0, ptr[i].length - 1);
		}

		return comments;

	}

	public String getVendor() {

		if (comment == null) {
			return null;
		}

		return new String(comment.vendor, 0, comment.vendor.length - 1);

	}

	public int getChannels() {

		if (info == null) {
			return 0;
		}

		return info.channels;
	}

	public int getSampleRate() {

		if (info == null) {
			return 0;
		}

		return info.rate;
	}

	public AudioFormat getOutputAudioFormat() {
		return format;
	}

}
