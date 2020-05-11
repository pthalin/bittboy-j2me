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
package org.thenesis.midpath.sound.codec;

import java.io.IOException;
import java.io.InputStream;

import javazoom.jlme.decoder.BitStream;
import javazoom.jlme.decoder.Decoder;
import javazoom.jlme.decoder.Header;
import javazoom.jlme.decoder.SampleBuffer;

import org.thenesis.midpath.sound.AudioFormat;

public class MP3Decoder implements AudioDecoder {

	private Decoder decoder;
	private BitStream bitstream;
	private AudioFormat format;

	public MP3Decoder()  {
	}
	
	public void initialize(InputStream stream) throws IOException {
		bitstream = new BitStream(stream);
		// Read first frame and get audio format
		Header header = bitstream.readFrame();
		decoder = new Decoder(header, bitstream);
		SampleBuffer output = (SampleBuffer) decoder.decodeFrame();
		format = new AudioFormat(decoder.getOutputFrequency(), AudioFormat.BITS_16, decoder.getOutputChannels(), true, false);
		bitstream.closeFrame();
	}


	public int decodeStep(DecoderCallback callback) throws IOException {
		Header header = bitstream.readFrame();
		SampleBuffer output = (SampleBuffer) decoder.decodeFrame();
		int length = output.size();
		if (length == 0)
			return -1;
		callback.write(output.getBuffer(), 0, length);
		bitstream.closeFrame();
		return length;
	}

	public AudioFormat getOutputAudioFormat() {
		return format;
	}

	
}
