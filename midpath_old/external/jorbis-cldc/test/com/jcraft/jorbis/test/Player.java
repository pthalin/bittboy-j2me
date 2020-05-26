/* ========================================================================
 * MIDPath - Copyright (C) 2006 Guillaume Legris, Mathieu Legris
 * 
 * JOrbis - Copyright (C) 2000 ymnk, JCraft,Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * ========================================================================
 */
package com.jcraft.jorbis.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class Player {
	private static OggVorbisDecoder decoder;
	private static SourceDataLine line;

	private boolean playable = true;

	//Runtime rt = null;

	public Player(InputStream stream) throws Exception {

		// Create a decoder
		decoder = new OggVorbisDecoder(stream);

	}

	public static void startOutput(AudioFormat playFormat) throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, playFormat);

		if (!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException("sorry, the sound format cannot be played");
		}
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(playFormat);
		line.start();
	}

	public static void stopOutput() {
		if (line != null) {
			line.drain();
			line.stop();
			line.close();
			line = null;
		}
	}

	public static void main(String args[]) throws Exception {

		//		 Load Ogg file
		String oggFileName = "tr51-glegris.ogg";
		InputStream is = Player.class.getResourceAsStream(oggFileName);

		Player player = new Player(is);
		System.out.println("starting");
		player.play();
		System.out.println("ending");

		//		if (args.length > 0) {
		//			String file = args[0];
		//			try {
		//				
		//				        if (file.equalsIgnoreCase("-url"))
		//				        {
		//							if (args.length > 1)
		//							{
		//								URL u = new URL(args[1]);
		//				        		Player player = new Player(new BufferedInputStream(u.openStream(), 2048));
		//				        		System.out.println("starting");
		//				        		player.play();
		//				        		System.out.println("ending");
		//							}
		//							else
		//							{
		//								usage();
		//							}
		//						}
		//						else
		//						{
		//				        	//Player player = new Player(new BufferedInputStream(new FileInputStream(file), 2048));
		//							Player player = new Player(new BufferedInputStream(Player.class.getResourceAsStream("/res/Alert.mp3"), 2048));
		//				        	System.out.println("starting");
		//				        	player.play();
		//				        	System.out.println("ending");
		//						}
		//			} catch (Exception e) {
		//				System.err.println("couldn't locate the mp3 file");
		//			}
		//		} else {
		//			usage();
		//		}
		//		System.exit(0);
	}

	private static void usage() {
		System.out.println("Usage : ");
		System.out.println("       java javazoom.jlme.util.Player [mp3file] [-url mp3url]");
		System.out.println("");
		System.out.println("            mp3file : MP3 filename to play");
		System.out.println("            mp3url  : MP3 URL to play");
	}

	public void play() throws Exception {

		boolean first = true;

		// While the end of the stream has not be reached.
		while (true) {

			// Prepare the decoder
			decoder.prepare();

			// If ok, print infos about the ogg stream and start playback.
			if (decoder.getState() == OggVorbisDecoder.PREPARED) {

				// Print track infos
				String[] comments = decoder.getComments();
				for (int i = 0; i < comments.length; i++) {
					System.out.println(comments[i] + "\n");
				}

				System.out.println("Bitstream is " + decoder.getChannels() + " channel, " + decoder.getSampleRate()
						+ "Hz\n");
				System.out.println("Encoded by: " + decoder.getVendor() + "\n\n");

				// Open a line and configure it to accept audio data from the decoder.

				if (first) {
					first = false;
					// Sample rate, 16 bits per sample, channels, signed, little endian
					AudioFormat format = new AudioFormat(decoder.getSampleRate(), 16, decoder.getChannels(), true,
							false);
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

					if (!AudioSystem.isLineSupported(info)) {
						throw new LineUnavailableException("sorry, the sound format cannot be played");
					}
					line = (SourceDataLine) AudioSystem.getLine(info);
					line.open(format);
					line.start();
				}
				
				OutputStream os = new OutputStream() {

					private byte[] buf = new byte[1];
					int totalSize = 0;

					public void write(byte[] buffer, int offset, int len) throws IOException {
						line.write(buffer, offset, len);
						totalSize += len;
						System.out.println("totalSize=" + totalSize);
					}

					public void write(int b) throws IOException {
						buf[0] = (byte) b;
						write(buf, 0, 1);
					}

				};
				

				// Decode now
				decoder.decode(os);

				while (true) {
					System.out.println("step");
					
					//				 Decode now
					decoder.decodeStep(os);
				}

			}

			if (decoder.getState() == OggVorbisDecoder.CLOSED) {
				// No more data (no stream chained) or explicitly closed 
				break;
			}

		}

		//		boolean first = true;
		//		int length;
		//		Header header = bitstream.readFrame();
		//		decoder = new Decoder(header, bitstream);
		//		while (playable) {
		//			try {
		//				SampleBuffer output = (SampleBuffer) decoder.decodeFrame();
		//				length = output.size();
		//				if (length == 0)
		//					break;
		//				//{
		//				if (first) {
		//					first = false;
		//					System.out.println("frequency: " + decoder.getOutputFrequency() + ", channels: "
		//							+ decoder.getOutputChannels());
		//					startOutput(new AudioFormat(decoder.getOutputFrequency(), 16, decoder.getOutputChannels(), true,
		//							false));
		//				}
		//				line.write(output.getBuffer(), 0, length);
		//				bitstream.closeFrame();
		//				header = bitstream.readFrame();
		//				//System.out.println("Mem:"+(rt.totalMemory() - rt.freeMemory())+"/"+rt.totalMemory());
		//				//}
		//			} catch (Exception e) {
		//				//e.printStackTrace();
		//				break;
		//			}
		//		}
		//		playable = false;
		//		stopOutput();
		//		bitstream.close();
	}

	public void stop() {
		playable = false;
	}
}
