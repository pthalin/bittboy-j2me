/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * JEsd - Copyright (C) 1999 JCraft Inc.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WaveFile {
	private WAVEHeader wheader;
	InputStream is = null;

	public WaveFile(InputStream fis) throws IOException {
		is = fis;
		wheader = WAVEHeader.parse(is);
	}

	WaveFile(int channels, int bitsPerSample, int rate, int datasize) throws IOException {
		wheader = new WAVEHeader(channels, bitsPerSample, rate, datasize);
	}

	public byte[] getHeader() {
		return wheader.getHeader();
	}

	public int readFrame(byte[] buf, int fcount) throws IOException {
		int i;
		i = is.read(buf, 0, fcount * getFrameSize());
		if (i < 0) {
			return i;
		}
		return i / getFrameSize();
	}

	public int getFrameCount() {
		return wheader.datasize / getFrameSize();
	}

	public int getFrameSize() {
		return wheader.channels * (wheader.bits / 8);
	}

	public int getChannels() {
		return wheader.channels;
	}

	public int getRate() {
		return wheader.rate;
	}

	public int getBitsPerSample() {
		return wheader.bits;
	}

	public int getTrackBytes() {
		return wheader.datasize;
	}

	public void close() throws IOException {
		is.close();
	}
}

class WAVEHeader {

	static final int HEADERSIZE = 44;
	private static byte[] RIFF = "RIFF".getBytes();
	private static byte[] WAVE = "WAVE".getBytes();
	private static byte[] fmt_ = "fmt ".getBytes();
	private static byte[] data = "data".getBytes();

	int rate, datasize;
	int channels = 1;
	int bits = 8;

	byte[] header = null;

	WAVEHeader(int channels, int bitsPerSample, int rate, int datasize) {
		this.channels = channels;
		this.bits = bitsPerSample;
		this.rate = rate;
		this.datasize = datasize;
	}

	static WAVEHeader parse(InputStream fis) throws IOException {

		int rate = 0;
		int datasize = 0;
		int channels = 0;
		int bps = 0; // averate byte per second
		int balign = 0; // block align
		int bits = 0; // 8/16

		/* Wave File Header - RIFF Type Chunk */

		// Chunk ID
		byte[] tmp = new byte[4];
		fis.read(tmp, 0, 4);
		String riffField = new String(tmp);
		if (!riffField.equalsIgnoreCase("RIFF")) {
			throw new IOException("Bad file format. Expected: \"RIFF\" Found: " + riffField);
		}
		
		//    if(riff[0]=='R') io=new IOLSB(); else io=new IOMSB();
		IOLSB io = new IOLSB();
		io.setInputStream(fis);

		// Chunk Data Size ( = filesize - 8 )
		int header_data_size = io.readInt();
		int len = header_data_size;

		// RIFF Type: WAVE
		io.readByte(tmp, 0, 4);
		len -= 4; // WAVE
		String waveField = new String(tmp);
		if (!waveField.equalsIgnoreCase("WAVE")) {
			throw new IOException("Bad file format. Expected: \"WAVE\" Found: " + waveField);
		}

		/* Format Chunk - "fmt " */

		//while (len > 0) {
		
		// Format chunk id
		io.readByte(tmp, 0, 4);
		len -= 4; 
		String chunkID = new String(tmp);
		//	Chunk Data Size ( = 16 )
		int size = io.readInt();
		len -= 4;
		if (chunkID.equals("fmt ")) {
			// Compression code : PCM/uncompressed ( = 1 )
			int compression = io.readShort(); // format tag
			size -= 2;
			len -= 2;
			// Number of channels
			channels = io.readShort();
			// Sample rate
			rate = io.readInt(); 
			// verage bytes per second ( = sampleRate * blockAlign )
			bps = io.readInt(); // averate byte per second
			// Block align
			balign = io.readShort();
			size -= 12;
			len -= 12;
			// Significant bits per sample
			if (compression == 1) { // WAVE_FORMAT_PCM 
				bits = io.readShort();
				size -= 2;
				len -= 2;
			}
			//continue;
		}
		/* Data Chunk - "data" */
		else if (chunkID.equals("data")) {
			datasize = size; // size
			len -= datasize;
			//continue;
		} else {
			throw new IOException("Bad file format. Unexpected WAVE chunk: " + waveField);
		}
		//System.out.println("unknown chunk: "+s);
		io.readPad(size);
		len -= size;
		//}

		//fis.close();

		WAVEHeader wh = new WAVEHeader(channels, bits, rate, datasize);

		//		fis = WAVEHeader.class.getResourceAsStream(name);
		//		wh.header = new byte[header_data_size - datasize];
		//		fis.read(wh.header, 0, wh.header.length);
		//		fis.close();

		return wh;

	}

	byte[] getHeader() {
		if (header != null)
			return header;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(HEADERSIZE);
			IOLSB io = new IOLSB();
			io.setOutputStream(baos);

			io.writeByte(RIFF);
			io.writeInt(datasize + (HEADERSIZE - 8));
			io.writeByte(WAVE);

			io.writeByte(fmt_); // format chunk
			io.writeInt(16); // size
			io.writeShort(1); // format tag
			io.writeShort(channels); // channel count
			io.writeInt(rate); // sample rate
			io.writeInt(rate * (bits / 8)); // averate byte per second
			io.writeShort(1); // block align
			io.writeShort(bits); // bits per sample

			io.writeByte(data); // data chunk
			io.writeInt(datasize); // size

			header = baos.toByteArray();
			return header;
		} catch (Exception e) {
			//System.out.println(e);
		}
		return null;
	}
	
}

final class IOLSB { 

	private java.io.InputStream in = null;
	private java.io.OutputStream out = null;

	byte[] ba;
	byte[] sa;
	byte[] ia;

	IOLSB() {
		ba = new byte[1];
		sa = new byte[2];
		ia = new byte[8];
	}

	void setInputStream(java.io.InputStream in) {
		this.in = in;
	}

	void setOutputStream(java.io.OutputStream out) {
		this.out = out;
	}

	void readByte(byte[] array, int begin, int length) throws java.io.IOException {
		int i = 0;
		while (true) {
			i = in.read(array, begin, length);
			if (i == -1) {
				throw new java.io.IOException();
			}
			length -= i;
			begin += i;
			if (length <= 0)
				return;
		}
	}

	void readPad(int n) throws java.io.IOException {
		int i;
		while (n > 0) {
			i = in.read(ba, 0, 1);
			if (i == -1) {
				throw new java.io.IOException();
			}
			n--;
		}
	}

	void writeByte(byte val) throws java.io.IOException {
		ba[0] = val;
		out.write(ba, 0, 1);
	}

	void writeByte(int val) throws java.io.IOException {
		writeByte((byte) val);
	}

	void writeByte(byte[] array) throws java.io.IOException {
		writeByte(array, 0, array.length);
	}

	void writeByte(byte[] array, int begin, int length) throws java.io.IOException {
		out.write(array, begin, length);
	}

	void writePad(int n) throws java.io.IOException {

		ba[0] = 0;
		while (0 < n) {
			out.write(ba, 0, 1);
			n--;
		}
	}

	//void flush() throws java.io.IOException{ }
	void close() throws java.io.IOException {
		in.close();
		out.close();
	}

	int readInt() throws java.io.IOException {
		int i = in.read(ia, 0, 4);
		if (i == -1) {
			throw new java.io.IOException();
		}
		i = ia[0] & 0xff;
		i |= ((ia[1] & 0xff) << 8);
		i |= ((ia[2] & 0xff) << 16);
		i |= ((ia[3] & 0xff) << 24);
		return i;
	}

	int readShort() throws java.io.IOException {
		int i = in.read(sa, 0, 2);
		if (i == -1) {
			throw new java.io.IOException();
		}
		i = sa[0] & 0xff;
		i |= ((sa[1] & 0xff) << 8);
		return i;
	}

	void writeInt(int val) throws java.io.IOException {
		ia[0] = (byte) ((val) & 0xff);
		ia[1] = (byte) ((val >> 8) & 0xff);
		ia[2] = (byte) ((val >> 16) & 0xff);
		ia[3] = (byte) ((val >> 24) & 0xff);
		out.write(ia, 0, 4);
	}

	void writeShort(int val) throws java.io.IOException {
		sa[0] = (byte) ((val) & 0xff);
		sa[1] = (byte) ((val >> 8) & 0xff);
		out.write(sa, 0, 2);
	}

}

