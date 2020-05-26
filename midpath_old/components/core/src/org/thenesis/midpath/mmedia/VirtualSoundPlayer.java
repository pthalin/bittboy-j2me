/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
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
package org.thenesis.midpath.mmedia;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.SourceStream;

import org.thenesis.midpath.sound.Line;
import org.thenesis.midpath.sound.Mixer;
import org.thenesis.midpath.sound.SoundBackend;
import org.thenesis.midpath.sound.SoundToolkit;
import org.thenesis.midpath.sound.codec.AudioDecoder;
import org.thenesis.midpath.sound.codec.DecoderCallback;
import org.thenesis.midpath.sound.codec.MP3Decoder;
import org.thenesis.midpath.sound.codec.OggVorbisDecoder;
import org.thenesis.midpath.sound.codec.WaveDecoder;

import com.sun.midp.log.LogChannels;
import com.sun.midp.log.Logging;

public class VirtualSoundPlayer extends BasicPlayer {

	private static SoundBackend backend;

	static {
		backend = SoundToolkit.getBackend();
		try {
			backend.open();
		} catch (IOException e) {
			if (Logging.REPORT_LEVEL <= Logging.WARNING) {
				Logging.report(Logging.WARNING, LogChannels.LC_CORE, e.getMessage());
			}
		}
	}

	private volatile AudioDecoder decoder;
	private Mixer mixer;
	private Line line;
	private volatile DecodingThread decodingThread;

	public VirtualSoundPlayer() {
	}

	protected void doClose() {
		decodingThread.close();
		mixer.removeLine(line);
		line.close();
	}

	protected void doDeallocate() {
		mixer.removeLine(line);
		line.close();
	}

	protected Control doGetControl(String type) {
		if (type.equals(pkgName + "VolumeControl")) {
			return new VirtualVolumeControl();
		}
		return null;
	}

	protected long doGetDuration() {
		return TIME_UNKNOWN;
	}

	protected long doGetMediaTime() {
		return TIME_UNKNOWN;
	}

	protected void doPrefetch() throws MediaException {

	}

	protected void doRealize() throws MediaException {

		// Ensure audio backend is ok 
		SoundBackend backend = SoundToolkit.getBackend();
		if (!backend.isOpen()) {
			throw new MediaException("Failed to open audio backend.");
		}

		String type = source.getContentType();
		if (type.equalsIgnoreCase("audio/x-wav")) {
			decoder = new WaveDecoder();
		} else if (type.equalsIgnoreCase("audio/mp3")) {
			decoder = new MP3Decoder();
		} else if (type.equalsIgnoreCase("audio/ogg")) {
			decoder = new OggVorbisDecoder();
		} 

		// Create an audio decoder 
		try {
			resetDecoder();
		} catch (IOException e) {
			e.printStackTrace();
			throw new MediaException(e.getMessage());
			
		}

		//System.out.println("[DEBUG] VirtualSoundPlayer.realize(): audio format : " + decoder.getOutputAudioFormat());

		// Create a line
		mixer = backend.getMixer();
		line = mixer.createLine(decoder.getOutputAudioFormat());
		mixer.addLine(line);

	}

	private void resetDecoder() throws IOException {
		
		if (stream.tell() > 0) {
			stream.seek(0);
		}
		DecoderInputStream dis = new DecoderInputStream(stream);
		decoder.initialize(dis);
	}

	protected long doSetMediaTime(long now) throws MediaException {

		
		try {
			if (now == 0) {
				resetDecoder();
				return 0;
			}
		} catch (IOException e) {
		}
		
		throw new MediaException("Can't set position on this media");
		
	}

	protected boolean doStart() {

		//System.out.println("[DEBUG] VirtualPlayer.doStart(): 1");

		if ((decodingThread == null) || decodingThread.isClosed()) {
			decodingThread = new DecodingThread(decoder, line);
		}

		if (decodingThread.isPaused()) {
			decodingThread.resume();
		} else {
			decodingThread.start();
		}

		//System.out.println("[DEBUG] VirtualPlayer.doStart(): 2");

		return true;

	}

	protected void doStop() throws MediaException {
		decodingThread.pause();
	}

	class VirtualToneControl implements ToneControl {

		public void setSequence(byte[] sequence) {

			if (sequence == null)
				throw new IllegalArgumentException("null sequence");

			if ((state == Player.PREFETCHED) || (state == Player.STARTED))
				throw new IllegalStateException("Prefetched or Started");

		}

	}

	class VirtualVolumeControl implements VolumeControl {

		public int oldLevel = 0;
		boolean muted = false;

		public int getLevel() {

			if (muted) {
				return oldLevel;
			}

			return 0;
		}

		public boolean isMuted() {
			return (getLevel() == 0) ? true : false;
		}

		public int setLevel(int level) {

			if (muted) {
				oldLevel = level;
				return level;
			}

			return 0;
		}

		public void setMute(boolean mute) {

			if (mute) {
				oldLevel = getLevel();
				setLevel(0);
				muted = true;
			} else {
				setLevel(oldLevel);
				muted = false;
			}

		}

	}

	public void endOfMediaReached() {
		//System.out.println("[DEBUG] VirtualPlayer.endOfMediaReached(): start");
		sendEvent(PlayerListener.END_OF_MEDIA, new Long(-1));
		//System.out.println("[DEBUG] VirtualPlayer.endOfMediaReached(): end");
	}

	public class DecodingThread implements Runnable {

		private volatile Thread thread;
		private volatile boolean paused = false;
		private volatile boolean closed = false;
		private AudioDecoder decoder;
		private Line line;

		private DecoderCallback decoderCallback = new DecoderCallback() {
			public void write(byte[] buf, int offset, int length) {
				//System.out.println("[DEBUG] DecoderCallback.write(): start");
				line.write(buf, offset, length);
				//System.out.println("[DEBUG] DecoderCallback.write(): end");
			}
		};

		public DecodingThread(AudioDecoder decoder, Line line) {
			this.decoder = decoder;
			this.line = line;
		}

		public synchronized void start() {
			if ((!closed) && (thread == null)) {
				line.start();
				thread = new Thread(this);
				thread.start();
			}
		}

		public synchronized void pause() {
			line.stop();
			paused = true;
		}

		public synchronized void resume() {
			line.start();
			paused = false;
			notify();
		}

		public synchronized void close() {
			line.stop();
			thread = null;
			closed = true;
		}

		public boolean isPaused() {
			return paused;
		}

		public boolean isClosed() {
			return closed;
		}

		public void run() {

			try {

				while (Thread.currentThread() == thread) {

					synchronized (DecodingThread.this) {
						if (paused) {
							try {
								wait();
							} catch (InterruptedException e) {
							}
						}
					}

					if (DecodingThread.this.decoder.decodeStep(decoderCallback) < 0) {
						close();
						break;
					}

					//System.out.println("[DEBUG] DecodingThread.run(): 2");
				}
			} catch (IOException e) {
				close();
			} finally {
				endOfMediaReached();
			}

		}

	}

}

class DecoderInputStream extends InputStream {

	private byte[] buf = new byte[1];
	private long markPosition = 0;
	private SourceStream stream;

	public DecoderInputStream(SourceStream stream) {
		this.stream = stream;
	}

	public int read() throws IOException {
		int len = stream.read(buf, 0, 1);
		if (len < 0) {
			return -1;
		} else {
			return (buf[0] & 0xFF);
		}
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	public int read(byte[] b) throws IOException {
		return stream.read(b, 0, b.length);
	}

	public void mark(int readlimit) {
		markPosition = stream.tell();
	}

	public boolean markSupported() {
		return (stream.getSeekType() == SourceStream.RANDOM_ACCESSIBLE);
	}

	public void reset() throws IOException {
		stream.seek(markPosition);
	}

};
