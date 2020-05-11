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

public abstract class AbstractLine implements Line {

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
	

	public AbstractLine(AudioFormat format, AudioFormat dstformat, int size) {
		this.lineAudioFormat = format;
		this.mixerAudioFormat = dstformat;
		lineBufferSize = size;
		maxChunkSize = lineBufferSize >> 2;
		convertingBufferSize = AudioTools.getFrameConversionBufferSize(maxChunkSize, lineAudioFormat, mixerAudioFormat);
		convertingBuffer = new byte[convertingBufferSize];
		resamplingBufferSize = AudioTools.getFormatConversionBufferSize(maxChunkSize, lineAudioFormat, mixerAudioFormat);
		resamplingBuffer = new byte[resamplingBufferSize];
		lineBuffer = new byte[size];
	}
	
	public abstract void notifyLineUpdated();
	

	public int available() {
		return lineBufferSize - lineOffset;
	}

	public int write(byte[] b, int offset, int length) {

		//System.out.println("[DEBUG] AbstractLine.write(): b.length=" + b.length + " offset="  + offset + " length="+ length + " lineBufferSize=" + lineBufferSize);
		
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
		
		//System.out.println("[DEBUG] AbstractLine.write(): end");
		
		return bytesWritten;

	}

	/**
	 * Writes a chunk of audio data. Chunk size must be smaller or equal to the line buffer size.
	 * @param b
	 * @param offset
	 * @param length
	 */
	private void writeChunk(byte[] b, int offset, int length) {
		
		//System.out.println("[DEBUG] AbstractLine.writeChunk(): line audio format : " + lineAudioFormat);
		//System.out.println("[DEBUG] AbstractLine.writeChunk(): mixer audio format : " + mixerAudioFormat);

		// Convert audio data only if the line and mixer audio formats don't match
		if (lineAudioFormat.matches(mixerAudioFormat)) {
			// Block/loop until all data are consumed
			while (state == STARTED && length > 0) {

				int available = available();

				if (available > 0) {
					int size = Math.min(length, available);
					System.arraycopy(b, offset, lineBuffer, lineOffset, size);
					length -= size;
					offset += size;
					lineOffset += size;
				}

				// Notify the line was written (for mixing purpose for example)
				notifyLineUpdated();

				Thread.yield();
			}
		} else {

			if (!lineAudioFormat.is16bitsStereoSignedLittleEndian()) {
				//System.out.println("[DEBUG] AbstractLine.writeChunk(): convert format");
				length = AudioTools.convertTo16BitsStereo(b, lineAudioFormat, offset, convertingBuffer,
						mixerAudioFormat, 0, length);
				offset = 0;
				b = convertingBuffer;
			}

			// Resample
			//System.out.println("[DEBUG] AbstractLine.writeChunk(): " + b.length + " " + length + "  " + resamplingBuffer.length + " " + offset);
			int resamplingSize = AudioTools.resample(b, lineAudioFormat, offset, resamplingBuffer,
					mixerAudioFormat, 0, length);
			int resamplingBufferOffset = 0;
			// System.out.println("[DEBUG] AbstractLine.writeChunk(): resamplingSize=" + resamplingSize);

			// Block/loop until all (resampled) data are consumed
			while (state == STARTED && resamplingSize > 0) {

				int available = available();

				if (available > 0) {
					int size = Math.min(resamplingSize, available);

					System.arraycopy(resamplingBuffer, resamplingBufferOffset, lineBuffer, lineOffset, size);

					resamplingSize -= size;
					resamplingBufferOffset += size;
					lineOffset += size;
				}

				// Notify the line was written (for mixing purpose for example)	
				notifyLineUpdated();

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
		notifyLineUpdated();
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
