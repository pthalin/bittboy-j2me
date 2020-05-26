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

public class AudioTools {

	/* Resampling */

	/**
	 * 
	 * @param srcBufferSize the size of the input buffer 
	 * @param srcFreq the frequency of the input data
	 * @param dstFreq the frequency of the resampled data
	 * @return the max size of the target buffer for resampling
	 */
	public static int getResamplingBufferSize(int srcBufferSize, AudioFormat srcFormat, AudioFormat dstFormat) {
		
		int resamplingSize = srcBufferSize * dstFormat.sampleRate / srcFormat.sampleRate;
		int bytesPerFrame = dstFormat.getBytesPerFrame();
		
		// Align audio data (size must be a multiple of the frame size)
		int remainder = resamplingSize % bytesPerFrame;
		resamplingSize -= remainder;
		
		return resamplingSize;
	}

	/**
	 * Resample the input audio data. The number of bytes per frame must be same in the input and output buffers 
	 * (value returned by dstFormat.getBytesPerFrame() is used)
	 * @param srcBuffer the audio data to resample
	 * @param srcFreq the frequency of the input data
	 * @param count the length of the data to resample in bytes
	 * @param dstBuffer the output buffer which contains resampled data. Its length must be at least the size given by 
	 * 					ResamplingBufferSize(srcBuffer, AudioFormat srcFormat, AudioFormat dstFormat)
	 * @param dstFreq the frequency of the resampled data
	 * @param bytePerFrame the number of byte per frame of audio data
	 * @return the size of the resampled data in bytes
	 */
	public static int resample(byte[] srcBuffer, AudioFormat srcFormat, int srcOffset, byte[] dstBuffer, AudioFormat dstFormat,
			int destOffset, int count) {

		int srcFreq = srcFormat.sampleRate;
		int dstFreq = dstFormat.sampleRate;
		int bytesPerFrame = dstFormat.getBytesPerFrame();
		
		int destSamples = getResamplingBufferSize(count, srcFormat, dstFormat);

		//		if (dstBuffer.length < destSamples) {
		//			throw new IllegalArgumentException("Destination buffer is too small");
		//		}

		//System.out.println("bytePerFrame=" + bytePerFrame);

		// Downsampling
		if (srcFreq > dstFreq) {

			for (int i = 0; i < destSamples; i += bytesPerFrame) {
				int srcPos = srcOffset + i * srcFreq / dstFreq;
				int destPos = destOffset + i;

				// Align to the first byte of the frame
				int remainder = srcPos % bytesPerFrame;
				srcPos -= remainder;

				// Process all bytes of the frame
				for (int j = 0; j < bytesPerFrame; j++) {
					dstBuffer[destPos + j] = srcBuffer[srcPos + j];
				}
			}

		}
		// Upsampling
		else if (srcFreq < dstFreq) {
			
			//System.out.println("[DEBUG] AudioTools.resample()");
			for (int i = 0; i < destSamples; i += bytesPerFrame) {
				int srcPos = srcOffset + i * srcFreq / dstFreq;
				int destPos = destOffset + i;

				// Align to the first byte of the frame
				int remainder = srcPos % bytesPerFrame;
				srcPos -= remainder;

				// Process all bytes of the frame
				for (int j = 0; j < bytesPerFrame; j++) {
					dstBuffer[destPos + j] = srcBuffer[srcPos + j];
				}
			}

		} else {
			System.arraycopy(srcBuffer, srcOffset, dstBuffer, destOffset, destSamples);
		}

		return destSamples;

	}

	/* Format conversion */

	/**
	 * 
	 * @param srcBufferSize the size of the input buffer 
	 * @param srcFreq the frequency of the input data
	 * @param dstFreq the frequency of the resampled data
	 * @return the max size of the target buffer for resampling
	 */
	public static int getFrameConversionBufferSize(int srcBufferSize, AudioFormat srcFormat, AudioFormat dstFormat) {

		int factor = srcBufferSize * dstFormat.bytePerSample * dstFormat.channels
				/ (srcFormat.bytePerSample * srcFormat.channels);

		return factor;

	}
	
	/**
	 * 
	 * @param srcBufferSize the size of the input buffer 
	 * @param srcFreq the frequency of the input data
	 * @param dstFreq the frequency of the resampled data
	 * @return the max size of the target buffer for resampling
	 */
	public static int getFormatConversionBufferSize(int srcBufferSize, AudioFormat srcFormat, AudioFormat dstFormat) {

		int factor = srcBufferSize * dstFormat.bytePerSample * dstFormat.channels * dstFormat.sampleRate
				/ (srcFormat.bytePerSample * srcFormat.channels * srcFormat.sampleRate);

		return factor;

	}

	/**
	 * Convert input audio data to 16 bits stereo format. 
	 * @param srcBuffer
	 * @param srcFormat
	 * @param dstBuffer the target buffer. Its length must be at least the size given by 
	 * 					getFrameConversionBufferSize(srcBuffer, AudioFormat srcFormat, AudioFormat dstFormat)
	 * @param dstFormat
	 * @param length
	 */
	public static int convertTo16BitsStereo(byte[] srcBuffer, AudioFormat srcFormat, int srcOffset, byte[] dstBuffer,
			AudioFormat dstFormat, int dstOffset, int length) {
		
		int destSamples = getFrameConversionBufferSize(length, srcFormat, dstFormat);

		if ((srcFormat.channels == AudioFormat.STEREO) && (dstFormat.channels == AudioFormat.STEREO)) {

			if (srcFormat.bytePerSample == dstFormat.bytePerSample) {

				if ((srcFormat.signed == false) && (dstFormat.signed == true)) {
					for (int i = 0; i < destSamples; i += 4) {
						int srcPos = srcOffset + i;
						int dstPos = dstOffset + i;
						dstBuffer[dstPos] = (byte) (srcBuffer[srcPos] + 128);
						dstBuffer[dstPos + 1] = (byte) (srcBuffer[srcPos + 1] + 128);
						dstBuffer[dstPos + 2] = (byte) (srcBuffer[srcPos + 2] + 128);
						dstBuffer[dstPos + 3] = (byte) (srcBuffer[srcPos + 3] + 128);
					}
				} else if ((srcFormat.signed == true) && (dstFormat.signed == true)) {
					System.arraycopy(srcBuffer, srcOffset, dstBuffer, dstOffset, destSamples);
				}

			} else if ((srcFormat.bytePerSample == AudioFormat.BITS_8)
					&& (dstFormat.bytePerSample == AudioFormat.BITS_16)) {

				if ((srcFormat.signed == false) && (dstFormat.signed == true)) {
					for (int i = 0; i < length; i += 2) {
						int srcPos = srcOffset + i;
						int dstPos = dstOffset + i << 1;
						dstBuffer[dstPos] = 0;
						dstBuffer[dstPos + 1] = (byte) (srcBuffer[srcPos] + 128);
						dstBuffer[dstPos + 2] = 0;
						dstBuffer[dstPos + 3] = (byte) (srcBuffer[srcPos + 1] + 128);
					}
				} else if ((srcFormat.signed == true) && (dstFormat.signed == true)) {
					for (int i = 0; i < length; i += 2) {
						int srcPos = srcOffset + i;
						int dstPos = dstOffset + i << 1;
						dstBuffer[dstPos] = 0;
						dstBuffer[dstPos + 1] = (byte) (srcBuffer[srcPos]);
						dstBuffer[dstPos + 2] = 0;
						dstBuffer[dstPos + 3] = (byte) (srcBuffer[srcPos + 1]);
					}
				}
			}

		} else if ((srcFormat.channels == AudioFormat.MONO) && (dstFormat.channels == AudioFormat.STEREO)) {
			
			if ((srcFormat.bytePerSample == AudioFormat.BITS_16) && (dstFormat.bytePerSample == AudioFormat.BITS_16)) {
				
				if ((srcFormat.signed == false) && (dstFormat.signed == true)) {
					
					for (int i = 0; i < length; i += 2) {
						int srcPos = srcOffset + i;
						int dstPos = dstOffset + i << 1;
						dstBuffer[dstPos] = (byte) (srcBuffer[srcPos] + 128);
						dstBuffer[dstPos + 1] = (byte) (srcBuffer[srcPos + 1] + 128);
						dstBuffer[dstPos + 2] = (byte) (srcBuffer[srcPos] + 128);
						dstBuffer[dstPos + 3] = (byte) (srcBuffer[srcPos + 1] + 128);
					}
				} else if ((srcFormat.signed == true) && (dstFormat.signed == true)) {
					//System.out.println("[DEBUG] AudioTools.convert()");
					for (int i = 0; i < length; i += 2) {
						int srcPos = srcOffset + i;
						int dstPos = dstOffset + i << 1;
						dstBuffer[dstPos] = (byte) (srcBuffer[srcPos]);
						dstBuffer[dstPos + 1] = (byte) (srcBuffer[srcPos + 1] );
						dstBuffer[dstPos + 2] = (byte) (srcBuffer[srcPos]);
						dstBuffer[dstPos + 3] = (byte) (srcBuffer[srcPos + 1]);
					}
				}

			} else if ((srcFormat.bytePerSample == AudioFormat.BITS_8)
					&& (dstFormat.bytePerSample == AudioFormat.BITS_16)) {

				if ((srcFormat.signed == false) && (dstFormat.signed == true)) {
					for (int i = 0; i < length; i++) {
						int srcPos = srcOffset + i;
						int dstPos = dstOffset + i << 2;
						dstBuffer[dstPos] = 0;
						dstBuffer[dstPos + 1] = (byte) (srcBuffer[srcPos] + 128);
						dstBuffer[dstPos + 2] = 0;
						dstBuffer[dstPos + 3] = (byte) (srcBuffer[srcPos] + 128);
					}
				} else if ((srcFormat.signed == true) && (dstFormat.signed == true)) {
					for (int i = 0; i < length; i++) {
						int srcPos = srcOffset + i;
						int dstPos = dstOffset + i << 2;
						dstBuffer[dstPos] = 0;
						dstBuffer[dstPos + 1] = (byte) (srcBuffer[srcPos]);
						dstBuffer[dstPos + 2] = 0;
						dstBuffer[dstPos + 3] = (byte) (srcBuffer[srcPos]);
					}
				}
			}

		}
		
		return destSamples;

	}

	//	public static final int FP_SHIFT = 8;
	//  public static final int FP_ONE = 1 << FP_SHIFT;
	//  public static final int FP_MASK = FP_ONE - 1;
	//  
	//  public int resample(byte[] srcBuffer, int srcFreq, int count, byte[] dstBuffer, int dstFreq, int bytePerFrame) {
	//
	//
	//  	int destSamples = count * dstFreq / srcFreq;
	//  	int step = srcFreq * FP_ONE / dstFreq;
	//  	
	//  	System.out.println("destSamples=" + destSamples + "  " + (count * dstFreq / srcFreq));
	//
	//		//		if (dstBuffer.length < destSamples) {
	//		//			throw new IllegalArgumentException("Destination buffer is too small");
	//		//		}
	//
	//		//System.out.println("bytePerFrame=" + bytePerFrame);
	//
	//		// Downsampling
	//		if (srcFreq > dstFreq) {
	//
	//			int srcPos = 0;
	//			for (int i = 0; i < (destSamples - bytePerFrame); i += bytePerFrame) {
	//				srcPos = (i * step) >>  FP_SHIFT;
	//
	//				// Align to the first byte of the frame
	//				int remainder = srcPos % bytePerFrame;
	//				srcPos -= remainder;
	//				
	//				// Process all bytes of the frame
	//				for (int j = 0; j < bytePerFrame; j++) {
	//					dstBuffer[i + j] = srcBuffer[srcPos + j];
	//				}
	//				
	//			}
	//
	//		}
	//		// Upsampling
	//		else if (srcFreq < dstFreq) {
	//
	//			int srcPos = 0;
	//
	//			for (int i = 0; i < (destSamples - bytePerFrame); i += bytePerFrame) {
	//				srcPos = i * srcFreq / dstFreq;
	//
	//				// Align to the first byte of the frame
	//				int remainder = srcPos % bytePerFrame;
	//				srcPos -= remainder;
	//
	//				// Process all bytes of the frame
	//				for (int j = 0; j < bytePerFrame; j++) {
	//					dstBuffer[i + j] = srcBuffer[srcPos + j];
	//				}
	//			}
	//
	//		} else {
	//			System.arraycopy(srcBuffer, 0, dstBuffer, 0, destSamples);
	//		}
	//
	//		return destSamples;
	//
	//	}

}
