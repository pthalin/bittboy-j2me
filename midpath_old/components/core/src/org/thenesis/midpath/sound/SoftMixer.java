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
package org.thenesis.midpath.sound;

import java.util.Vector;

/**
 * <p>A software mixer with embedded resampler and audio format converter.</p>
 * <p>References: http://www.pascalgamedevelopment.com/library/oxygen_damt/index.html</p>
 * @author Guillaume Legris
 * @author Mathieu legris
 */
public class SoftMixer extends Mixer {
	
	private static final int MIXER_VOLUME_MAX = 1 << 7; // 128

	private int mixbufferSize;
	private byte[] mixBuffer;
	private Object mutex = new Object();
	private SoundBackend soundBackend;
	private AudioFormat mixerAudioFormat;
	private Vector aliveLines = new Vector();
	private int mixerVolume = MIXER_VOLUME_MAX; // 128

	public SoftMixer(SoundBackend backend) {
		this.soundBackend = backend;
		this.mixbufferSize = soundBackend.getBufferSize();
		this.mixBuffer = new byte[mixbufferSize];
		this.mixerAudioFormat = soundBackend.getAudioFormat();
	}

	public Line createLine(AudioFormat format) {
		return new SoftMixerLine(format, mixerAudioFormat, mixbufferSize);
	}

	//	 Mix if needed (i.e. when all line buffers are filled)
	private void mix() {

		//System.out.println("[DEBUG] SoftMixer.mix() : start " + Thread.currentThread());

		//		try {
		//			Thread.sleep(20);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		// Prevent operations while mixing (other lines acquiring the lock, line added to the pool)
		//synchronized (mutex) {

		aliveLines.removeAllElements();

		// Check if we have to mix the buffers.
		for (int i = 0; i < lineList.size(); i++) {
			SoftMixerLine line = (SoftMixerLine) lineList.elementAt(i);
			if (line.isRunning()) {

				// Do not mix yet if line is not filled 
				if (line.available() > 0) {
					return;
				}
				// Add a line to the mix 
				aliveLines.addElement(line);

			}

		}

		//System.out.println("[DEBUG] SoftMixer.mix() : have to mix " + Thread.currentThread());

		// Clean mixBuffer
		for (int i = 0; i < mixBuffer.length; i++) {
			mixBuffer[i] = 0;
		}

		// Mix the line buffers
		for (int i = 0; i < aliveLines.size(); i++) {
			SoftMixerLine line = (SoftMixerLine) aliveLines.elementAt(i);
			mixLine(mixBuffer, line, aliveLines.size());
			// Clear the line buffer
			line.reset();
		}

		soundBackend.write(mixBuffer, 0, mixBuffer.length);
		//}

		//System.out.println("[DEBUG] SoftMixer.mix() : end " + Thread.currentThread());

	}

	private void mixLine(byte[] destBuf, SoftMixerLine line, int lines) {

		//System.out.println("[DEBUG] SoftMixer.mix2()");

		byte[] srcBuf = line.getData();

		// TODO Add line volume and compressor
		if ((lines <= 1) && (mixerVolume == MIXER_VOLUME_MAX)) {
			System.arraycopy(srcBuf, 0, destBuf, 0, srcBuf.length);
//			for (int i = 0; i < srcBuf.length; i += 1) {
//				destBuf[i] = srcBuf[i];
//			}
		} else {

			int mixFactor = ((256 / lines) * mixerVolume) >> 7;

			for (int i = 0; i < srcBuf.length; i += 2) {

				int srcVal = (srcBuf[i + 1] << 8) + srcBuf[i];
				int dstVal = ((destBuf[i + 1] << 8) + destBuf[i]);
				int mixVal = ((srcVal * mixFactor) >> 8) + dstVal;
				//int mixVal = srcVal / lines + dstVal;
				//int mixVal = srcVal + dstVal;
				
				destBuf[i] = (byte) (mixVal & 0xFF);
				destBuf[i + 1] = (byte) ((mixVal >> 8) & 0xFF);
			}
		}

	}

	/**
	 * An audio line with embedded resampler and audio format converter.<br>
	 * When line is started, the line must be regulary filled with data. Otherwhise it could block underlying mixing process (if any).<br>
	 * A correct code is: 
	 * <pre>
	 * line.start();
	 * while(running) {
	 *   line.write(buf, 0, buf.length);
	 * }
	 * // No more data to write.
	 * // Push data inside the buffer to the hardware and stop the line.
	 * line.drain();
	 * line.stop();
	 * </pre>
	 */
	public class SoftMixerLine implements Line {

		public int state = STOPPED;
		private int lineBufferSize;
		private int lineOffset;
		private byte[] lineBuffer;
		private AudioFormat lineAudioFormat;
		private AudioFormat mixerAudioFormat;
		private int convertingBufferSize;
		private byte[] convertingBuffer;
		private int resamplingBufferSize;
		private byte[] resamplingBuffer;
		private int maxChunkSize;

		SoftMixerLine(AudioFormat format, AudioFormat dstformat, int size) {
			this.lineAudioFormat = format;
			this.mixerAudioFormat = dstformat;
			lineBufferSize = size;
			maxChunkSize = lineBufferSize >> 2;
			convertingBufferSize = AudioTools.getFrameConversionBufferSize(maxChunkSize, lineAudioFormat,
					mixerAudioFormat);
			convertingBuffer = new byte[convertingBufferSize];
			resamplingBufferSize = AudioTools.getFormatConversionBufferSize(maxChunkSize, lineAudioFormat,
					mixerAudioFormat);
			resamplingBuffer = new byte[resamplingBufferSize];
			lineBuffer = new byte[size];
		}

		public int available() {
			return lineBufferSize - lineOffset;
		}

		public int write(byte[] b, int offset, int length) {

			//System.out.println("[DEBUG] SoftMixer.write(): b.length=" + b.length + " offset="  + offset + " length="+ length + " lineBufferSize=" + lineBufferSize);

			int bytesWritten = 0;

			while ((state == STARTED) && (length > 0)) {
				if (length < maxChunkSize) {
					writeChunk(b, offset, length);
					bytesWritten += length;
					break;
				} else {
					writeChunk(b, offset, maxChunkSize);
					length -= maxChunkSize;
					offset += maxChunkSize;
					bytesWritten += maxChunkSize;
				}
			}

			//System.out.println("[DEBUG] SoftMixer.write(): end");

			return bytesWritten;

		}

		/**
		 * Writes a chunk of audio data. Chunk size must be smaller or equal to the line buffer size.
		 * @param b
		 * @param offset
		 * @param length
		 */
		private void writeChunk(byte[] b, int offset, int length) {

			//System.out.println("[DEBUG] SoftMixer.writeChunk(): line audio format : " + lineAudioFormat);
			//System.out.println("[DEBUG] SoftMixer.writeChunk(): mixer audio format : " + mixerAudioFormat);

			// Convert audio data only if the line and mixer audio formats don't match
			if (lineAudioFormat.matches(mixerAudioFormat)) {
				// Block/loop until all data are consumed
				while (state == STARTED && length > 0) {

					// Prevent other operations while writing and requesting mix
					synchronized (mutex) {

						int available = available();

						if (available > 0) {
							int size = Math.min(length, available);
							System.arraycopy(b, offset, lineBuffer, lineOffset, size);
							length -= size;
							offset += size;
							lineOffset += size;
						}

						// Mix if needed (i.e. when all line buffers are filled)
						mix();

					}

					Thread.yield();
				}
			} else {

				if (!lineAudioFormat.is16bitsStereoSignedLittleEndian()) {
					//System.out.println("[DEBUG] SoftMixer.writeChunk(): convert format");
					length = AudioTools.convertTo16BitsStereo(b, lineAudioFormat, offset, convertingBuffer,
							mixerAudioFormat, 0, length);
					offset = 0;
					b = convertingBuffer;
				}

				// Resample
				//System.out.println("[DEBUG] SoftMixer.writeChunk(): " + b.length + " " + length + "  " + resamplingBuffer.length + " " + offset);
				int resamplingSize = AudioTools.resample(b, lineAudioFormat, offset, resamplingBuffer,
						mixerAudioFormat, 0, length);
				int resamplingBufferOffset = 0;
				// System.out.println("[DEBUG] SoftMixer.writeChunk(): resamplingSize=" + resamplingSize);

				// Block/loop until all (resampled) data are consumed
				while (state == STARTED && resamplingSize > 0) {

					// Prevent other operations while writing and requesting mix
					synchronized (mutex) {

						int available = available();

						if (available > 0) {
							int size = Math.min(resamplingSize, available);

							System.arraycopy(resamplingBuffer, resamplingBufferOffset, lineBuffer, lineOffset, size);

							resamplingSize -= size;
							resamplingBufferOffset += size;
							lineOffset += size;
						}

						// Mix if needed (i.e. when all line buffers are filled)
						mix();

					}

					Thread.yield();
				}
			} // if

		}

		public void start() {
			state = STARTED;
		}

		public void stop() {
			state = STOPPED;
		}

		public void close() {
			state = CLOSED;
		}

		public void drain() {
			lineOffset = lineBufferSize;
			mix();
		}

		public boolean isRunning() {
			return (state == STARTED) ? true : false;
		}

		//		public boolean isFilled() {
		//			return (available() == 0);
		//		}

		public boolean isEmpty() {
			return (lineOffset == 0);
		}

		/**
		 * Reset the line (i.e clear the line buffer).
		 */
		void reset() {
			lineOffset = 0;
		}

		byte[] getData() {
			return lineBuffer;
		}

		/**
		 * Gets the format of the line audio data.
		 * 
		 * @return the format of the line audio data
		 */
		public AudioFormat getFormat() {
			return lineAudioFormat;
		}

	}

	//	public class SoftMixerLine extends AbstractLine {
	//
	//		public SoftMixerLine(AudioFormat format, AudioFormat dstformat, int size) {
	//			super(format, dstformat, size);
	//		}
	//
	//		public void notifyLineUpdated() {
	//			mix();
	//		}
	//		
	//	}

	//	public class SoftMixerLine implements Line {
	//
	//		public int state = STOPPED;
	//		private int lineBufferSize;
	//		private int lineOffset;
	//		private byte[] lineBuffer;
	//		private AudioFormat lineAudioFormat;
	//		private int convertingBufferSize;
	//		private byte[] convertingBuffer;
	//		private int resamplingBufferSize;
	//		private byte[] resamplingBuffer;
	//		private int maxChunkSize;
	//
	//		SoftMixerLine(AudioFormat format, int size) {
	//			this.lineAudioFormat = format;
	//			lineBufferSize = size;
	//			maxChunkSize = lineBufferSize >> 2;
	//			convertingBufferSize = AudioTools.getFrameConversionBufferSize(maxChunkSize, lineAudioFormat, mixerAudioFormat);
	//			convertingBuffer = new byte[convertingBufferSize];
	//			resamplingBufferSize = AudioTools.getFormatConversionBufferSize(maxChunkSize, lineAudioFormat, mixerAudioFormat);
	//			resamplingBuffer = new byte[resamplingBufferSize];
	//			lineBuffer = new byte[size];
	//		}
	//
	//		public synchronized int available() {
	//			return lineBufferSize - lineOffset;
	//		}
	//
	//		public synchronized int write(byte[] b, int offset, int length) {
	//
	//			//System.out.println("[DEBUG] SoftMixer.write(): b.length=" + b.length + " offset="  + offset + " length="+ length + " lineBufferSize=" + lineBufferSize);
	//			
	//			int bytesWritten = 0;
	//			
	//			while ((state == STARTED) && (length > 0)) {
	//				if (length < maxChunkSize) {
	//					writeChunk(b, offset, length);
	//					bytesWritten += length;
	//					break;
	//				} else {
	//					writeChunk(b, offset, maxChunkSize);
	//					length -= maxChunkSize;
	//					offset += maxChunkSize;
	//					bytesWritten += maxChunkSize;
	//				}
	//			}
	//			
	//			//System.out.println("[DEBUG] SoftMixer.write(): end");
	//			
	//			return bytesWritten;
	//
	//		}
	//
	//		/**
	//		 * Writes a chunk of audio data. Chunk size must be smaller or equal to the line buffer size.
	//		 * @param b
	//		 * @param offset
	//		 * @param length
	//		 */
	//		private void writeChunk(byte[] b, int offset, int length) {
	//			
	//			//System.out.println("[DEBUG] SoftMixer.writeChunk(): line audio format : " + lineAudioFormat);
	//			//System.out.println("[DEBUG] SoftMixer.writeChunk(): mixer audio format : " + mixerAudioFormat);
	//
	//			// Convert audio data only if the line and mixer audio formats don't match
	//			if (lineAudioFormat.matches(mixerAudioFormat)) {
	//				// Block/loop until all data are consumed
	//				while (state == STARTED && length > 0) {
	//
	//					int available = available();
	//
	//					if (available > 0) {
	//						int size = Math.min(length, available);
	//						System.arraycopy(b, offset, lineBuffer, lineOffset, size);
	//						length -= size;
	//						offset += size;
	//						lineOffset += size;
	//					}
	//
	//					// Mix if needed (i.e. when all line buffers are filled)
	//					mix();
	//
	//					Thread.yield();
	//				}
	//			} else {
	//
	//				if (!lineAudioFormat.is16bitsStereoSignedLittleEndian()) {
	//					System.out.println("[DEBUG] SoftMixer.writeChunk(): convert format");
	//					length = AudioTools.convertTo16BitsStereo(b, lineAudioFormat, offset, convertingBuffer,
	//							mixerAudioFormat, 0, length);
	//					offset = 0;
	//					b = convertingBuffer;
	//				}
	//
	//				// Resample
	//				//System.out.println("[DEBUG] SoftMixer.writeChunk(): " + b.length + " " + length + "  " + resamplingBuffer.length + " " + offset);
	//				int resamplingSize = AudioTools.resample(b, lineAudioFormat, offset, resamplingBuffer,
	//						mixerAudioFormat, 0, length);
	//				int resamplingBufferOffset = 0;
	//				// System.out.println("[DEBUG] SoftMixer.writeChunk(): resamplingSize=" + resamplingSize);
	//
	//				// Block/loop until all (resampled) data are consumed
	//				while (state == STARTED && resamplingSize > 0) {
	//
	//					int available = available();
	//
	//					if (available > 0) {
	//						int size = Math.min(resamplingSize, available);
	//
	//						System.arraycopy(resamplingBuffer, resamplingBufferOffset, lineBuffer, lineOffset, size);
	//
	//						resamplingSize -= size;
	//						resamplingBufferOffset += size;
	//						lineOffset += size;
	//					}
	//
	//					// Mix if needed (i.e. when all line buffers are filled)
	//					mix();
	//
	//					Thread.yield();
	//				}
	//			} // if
	//
	//		}
	//
	//		public synchronized void start() {
	//			state = STARTED;
	//		}
	//
	//		public synchronized void stop() {
	//			state = STOPPED;
	//		}
	//
	//		public synchronized void close() {
	//			state = CLOSED;
	//		}
	//		
	//		public synchronized void drain() {
	//			lineOffset = lineBufferSize;
	//			mix();
	//		}
	//
	//		public boolean isRunning() {
	//			return (state == STARTED) ? true : false;
	//		}
	//
	//		//		public boolean isFilled() {
	//		//			return (available() == 0);
	//		//		}
	//
	//		public boolean isEmpty() {
	//			return (lineOffset == 0);
	//		}
	//
	//		/**
	//		 * Reset the line (i.e clear the line buffer).
	//		 */
	//		void reset() {
	//			lineOffset = 0;
	//		}
	//
	//		byte[] getData() {
	//			return lineBuffer;
	//		}
	//
	//		/**
	//		 * Gets the format of the line audio data.
	//		 * 
	//		 * @return the format of the line audio data
	//		 */
	//		public AudioFormat getFormat() {
	//			return lineAudioFormat;
	//		}
	//
	//	}

}
