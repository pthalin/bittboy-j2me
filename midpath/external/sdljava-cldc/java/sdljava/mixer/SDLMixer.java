package sdljava.mixer;

/**
 *  sdljava - a java binding to the SDL API
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 */
import java.io.IOException;
import java.io.InputStream;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.SDLVersion;
import sdljava.audio.SDLAudio;
import sdljava.util.BufferUtil;
import sdljava.x.swig.Mix_Chunk;
import sdljava.x.swig.Mix_Fading;
import sdljava.x.swig.Mix_MusicType;
import sdljava.x.swig.SDL_version;
import sdljava.x.swig.SWIGTYPE_p__Mix_Music;
import sdljava.x.swig.SWIG_SDLMixer;

/**
 * Binding to the SDL_mixer library.
 * <p>
 * Please see the documentation <a href="http://jcatki.no-ip.org/SDL_mixer/SDL_mixer_frame.html">here</a>.
 * <p>
 * This library wraps all the functionality provided, however, <b>no support for callbacks</b> is currently
 * implemented.
 * <p>
 * All audio data currently lives at the C layer and is never copied to the java side (it
 * is accessed via NIO buffers)
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLMixer.java,v 1.20 2005/09/18 15:53:42 ivan_ganza Exp $
 */
public class SDLMixer {

	// constants from SDL_audio.h
	public final static int AUDIO_U8 = SDLAudio.AUDIO_U8;
	public final static int AUDIO_S8 = SDLAudio.AUDIO_S8;
	public final static int AUDIO_U16LSB = SDLAudio.AUDIO_U16LSB;
	public final static int AUDIO_S16LSB = SDLAudio.AUDIO_S16LSB;
	public final static int AUDIO_U16MSB = SDLAudio.AUDIO_U16MSB;
	public final static int AUDIO_S16MSB = SDLAudio.AUDIO_S16MSB;
	public final static int AUDIO_U16 = SDLAudio.AUDIO_U16;
	public final static int AUDIO_S16 = SDLAudio.AUDIO_S16;
	public final static int AUDIO_S16SYS = SDLAudio.AUDIO_S16SYS;
	public final static int AUDIO_U16SYS = SDLAudio.AUDIO_U16SYS;

	////////////////////////////////////////////////////////////////////////////////
	// GENERAL

	/**
	 * Initialize the mixer API.
	 * <p> This must be called before using
	 * other functions in this library.  SDL must be initialized with
	 * <B>SDL_INIT_AUDIO</B> before this call. frequency would be 44100 for
	 * 44.1KHz, which is CD audio rate. chunksize
	 * is the size of each mixed sample. The smaller this is the more
	 * your hooks will be called. If make this too small on a slow
	 * system, sound may skip. If made to large, sound effects will
	 * lag behind the action more. You want a happy medium for your
	 * target computer. You also may make this 4096, or larger, if you
	 * are just playing music. <B>MIX_CHANNELS(8)</B> mixing channels will be
	 * allocated by default. You may call this function multiple
	 * times, however you will have to call Mix_CloseAudio just as
	 * many times for the device to actually close. The format will
	 * not changed on subsequent calls. So you will have to close all
	 * the way before trying to open with different format parameters.
	 *
	 * <I>format</I> is based on SDL audio support, see SDL_audio.h. Here are the values listed there:
	 *
	 *  <P>
	 *  <UL>
	 *  <LI>
	 *  <B>AUDIO_U8</B><br> &nbsp;&nbsp;&nbsp;&nbsp;Unsigned 8-bit samples
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_S8</B><br>&nbsp;&nbsp;&nbsp;&nbsp;Signed 8-bit samples
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_U16LSB</B><br>&nbsp;&nbsp;&nbsp;&nbsp;Unsigned 16-bit samples, in little-endian byte order
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_S16LSB</B> <br>&nbsp;&nbsp;&nbsp;&nbsp;Signed 16-bit samples, in little-endian byte order
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_U16MSB</B> <br>&nbsp;&nbsp;&nbsp;&nbsp;Unsigned 16-bit samples, in big-endian byte order
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_S16MSB</B> <br>&nbsp;&nbsp;&nbsp;&nbsp;Signed 16-bit samples, in big-endian byte order
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_U16</B> <br>&nbsp;&nbsp;&nbsp;&nbsp;same as AUDIO_U16LSB (for backwards compatability probably)
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_S16</B> <br>&nbsp;&nbsp;&nbsp;&nbsp;same as AUDIO_S16LSB (for backwards compatability probably)
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_U16SYS</B> <br>&nbsp;&nbsp;&nbsp;&nbsp;Unsigned 16-bit samples, in system byte order
	 *  </LI>
	 *  <LI>
	 *  <B>AUDIO_S16SYS</B> <br>&nbsp;&nbsp;&nbsp;&nbsp;Signed 16-bit samples, in system byte order
	 *  </LI>
	 * </UL>
	 * <P>
	 * MIX_DEFAULT_FORMAT is the same as AUDIO_S16SYS.
	 *
	 * @param frequency Output sampling frequency in samples per second (Hz).
	 * @param format Output sample format.
	 * @param channels Number of sound channels in output.
	 *                 Set to 2 for stereo, 1 for mono. This has nothing to do with mixing channels.
	 * @param chunksize Bytes used per output sample.
	 * @exception SDLException if an error occurs
	 */
	public static void openAudio(int frequency, int format, int channels, int chunksize) throws SDLException {
		int result = SWIG_SDLMixer.Mix_OpenAudio(frequency, format, channels, chunksize);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Shutdown and cleanup the mixer API.  After calling this all
	 * audio is stopped, the device is closed, and the SDL_mixer
	 * functions should not be used. You may, of course, use
	 * Mix_OpenAudio to start the functionality again.  Note: This
	 * function doesn't do anything until you have called it the same
	 * number of times that you called Mix_OpenAudio. You may use
	 * Mix_QuerySpec to find out how many times Mix_CloseAudio needs
	 * to be called before the device is actually closed.
	 *
	 * @exception SDLException if an error occurs
	 */
	public static void close() throws SDLException {
		SWIG_SDLMixer.Mix_CloseAudio();
	}

	/**
	 * Get the actual audio format in use by the opened audio
	 * device. This may or may not match the parameters you passed to
	 * Mix_OpenAudio.
	 * 
	 * @return The current audio settings
	 * @exception SDLException if an error occurs
	 */
	public static MixerSpec querySpec() throws SDLException {
		int[] frequency = { -1 };
		int[] format = { -1 };
		int[] channels = { -1 };

		int result = SWIG_SDLMixer.Mix_QuerySpec(frequency, format, channels);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
		return new MixerSpec(frequency[0], format[0], channels[0]);
	}

	////////////////////////////////////////////////////////////////////////////////
	// SAMPLES

	/**
	 * Load file for use as a sample. This can load
	 * WAVE, AIFF, RIFF, OGG, and VOC files.  Note: You must call
	 * SDL_OpenAudio before this. It must know the output
	 * characteristics so it can convert the sample for playback, it
	 * does this conversion at load time.
	 *
	 * @param The path to the file to load
	 * @return The MixChunk
	 * @exception SDLException if an error occurs
	 */
	public static MixChunk loadWAV(String path) throws SDLException {
		Mix_Chunk chunk = SWIG_SDLMixer.SWIG_Mix_LoadWAV(path);
		if (chunk == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new MixChunk(chunk);
	}

	/**
	 * Load file for use as a sample. This can load
	 * WAVE, AIFF, RIFF, OGG, and VOC files.  Note: You must call
	 * SDL_OpenAudio before this. It must know the output
	 * characteristics so it can convert the sample for playback, it
	 * does this conversion at load time.
	 * <P>
	 *
	 * @param data a <code>byte[]</code> value
	 * @return The MixChunk
	 * @exception SDLException if an error occurs
	 */
	public static MixChunk loadWAV(byte[] data) throws SDLException {
		Mix_Chunk chunk = SWIG_SDLMixer.SWIG_Mix_LoadWAV_Buffer(data, data.length);
		if (chunk == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new MixChunk(chunk);
	}

	/**
	 * Load file for use as a sample. This can load
	 * WAVE, AIFF, RIFF, OGG, and VOC files.  Note: You must call
	 * SDL_OpenAudio before this. It must know the output
	 * characteristics so it can convert the sample for playback, it
	 * does this conversion at load time.
	 * <P>
	 *
	 *
	 * @param in the audio data given as an input stream
	 * @return The MixChunk
	 * @exception SDLException if an error occurs
	 */
	public static MixChunk loadWAV(InputStream in) throws SDLException, IOException {
		return loadWAV(BufferUtil.readInputStream(in));
	}


	//    /**
	//     * Load file for use as a sample. This can load
	//     * WAVE, AIFF, RIFF, OGG, and VOC files.  Note: You must call
	//     * SDL_OpenAudio before this. It must know the output
	//     * characteristics so it can convert the sample for playback, it
	//     * does this conversion at load time.
	//     *
	//     * @param buf a <I>DIRECT</I> <code>Buffer</code> with the Sample data
	//     * @return The MixChunk
	//     * @exception SDLException if an error occurs
	//     */
	//    public static MixChunk loadWAV(Buffer buf) throws SDLException {
	//	Mix_Chunk chunk = SWIG_SDLMixer.SWIG_Mix_LoadWAV_Buffer(buf, buf.capacity());
	//	if (chunk == null) {
	//	    throw new SDLException(SDLMain.getError());
	//	}
	//	return new MixChunk(chunk);
	//    }
	//
	//    /**
	//     * Load file for use as a sample. This can load
	//     * WAVE, AIFF, RIFF, OGG, and VOC files.  Note: You must call
	//     * SDL_OpenAudio before this. It must know the output
	//     * characteristics so it can convert the sample for playback, it
	//     * does this conversion at load time.
	//     * <P>
	//     * This method automatically creates the NIO Direct Buffer from the given byte array
	//     *
	//     * @param data a <code>byte[]</code> value
	//     * @return The MixChunk
	//     * @exception SDLException if an error occurs
	//     */
	//    public static MixChunk loadWAV(byte[] data) throws SDLException {
	//	ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
	//	buf.put(data);
	//	
	//	return loadWAV(buf);
	//    }
	//    /**
	//     * Load file for use as a sample. This can load
	//     * WAVE, AIFF, RIFF, OGG, and VOC files.  Note: You must call
	//     * SDL_OpenAudio before this. It must know the output
	//     * characteristics so it can convert the sample for playback, it
	//     * does this conversion at load time.
	//     * <P>
	//     * This method automatically creates the NIO Direct Buffer from the given byte array
	//     *
	//     * @param url The URL to fetch the data from
	//     * @return The MixChunk
	//     * @exception SDLException if an error occurs
	//     */
	//    public static MixChunk loadWAV(URL url) throws SDLException, IOException, MalformedURLException {
	//	return loadWAV( BufferUtil.readURL(url));
	//    }
	//
	//    /**
	//     * Load file for use as a sample. This can load
	//     * WAVE, AIFF, RIFF, OGG, and VOC files.  Note: You must call
	//     * SDL_OpenAudio before this. It must know the output
	//     * characteristics so it can convert the sample for playback, it
	//     * does this conversion at load time.
	//     * <P>
	//     * This method automatically creates the NIO Direct Buffer
	//     *
	//     * @param url The URL to fetch the data from
	//     * @return The MixChunk
	//     * @exception SDLException if an error occurs
	//     */
	//    public static MixChunk loadWAV(InputStream in) throws SDLException, IOException{
	//	return loadWAV( BufferUtil.readInputStream(in));
	//    }
	/**
	 * Set chunk->volume to volume.  The volume setting will take
	 * effect when the chunk is used on a channel, being mixed into
	 * the output.
	 *
	 * @param The chunk to set the volume in
	 * @param The volume to use from 0 to MIX_MAX_VOLUME(128).  If
	 * greater than MIX_MAX_VOLUME, then it will be set to
	 * MIX_MAX_VOLUME.  If less than 0 then chunk->volume will not be
	 * set.
	 * @return previous chunk->volume setting. if you passed a
	 * negative value for volume then this volume is still the current
	 * volume for the chunk
	 * @exception SDLException if an error occurs
	 */
	public static int volumeChunk(MixChunk chunk, int volume) throws SDLException {
		return SWIG_SDLMixer.Mix_VolumeChunk(chunk.getSwigMixChunk(), volume);
	}

	/**
	 * Free the memory used in chunk, and free chunk itself as
	 * well. Do not use chunk after this without loading a new sample
	 * to it. Note: It's a bad idea to free a chunk that is still
	 * being played...
	 *
	 * @param chunk a <code>MixChunk</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void freeChunk(MixChunk chunk) throws SDLException {
		SWIG_SDLMixer.Mix_FreeChunk(chunk.getSwigMixChunk());
	}

	////////////////////////////////////////////////////////////////////////////////
	// CHANNELS

	/**
	 * Set the number of channels being mixed. This can be called
	 * multiple times, even with sounds playing. If numchans is less
	 * than the current number of channels, then the higher channels
	 * will be stopped, freed, and therefore not mixed any
	 * longer. It's probably not a good idea to change the size 1000
	 * times a second though.  If any channels are deallocated, any
	 * callback set by Mix_ChannelFinished will be called when each
	 * channel is halted to be freed. Note: passing in zero WILL free
	 * all mixing channels, however music will still play.
	 * <p>
	 * Never fails...but a
	 * high number of channels can segfault if you run out of
	 * memory. We're talking REALLY high!
	 *
	 * @param count Number of channels to allocate for mixing.  A
	 * negative number will not do anything, it will tell you how many
	 * channels are currently allocated.
	 * @return The number of channels allocated.
	 * @exception SDLException if an error occurs
	 */
	public static int allocateChannels(int count) throws SDLException {
		return SWIG_SDLMixer.Mix_AllocateChannels(count);
	}

	/**
	 * Set the volume for any allocated channel. If channel is -1 then
	 * all channels at are set at once. The volume is applied during
	 * the final mix, along with the sample volume. So setting this
	 * volume to 64 will halve the output of all samples played on the
	 * specified channel. All channels default to a volume of 128,
	 * which is the max. Newly allocated channels will have the max
	 * volume set, so setting all channels volumes does not affect
	 * subsequent channel allocations.
	 *
	 * @param Channel to set mix volume for. -1 will set the volume for all allocated channels.
	 * @param volume The volume to use from 0 to MIX_MAX_VOLUME(128).
	 *               If greater than MIX_MAX_VOLUME, then it will be set to MIX_MAX_VOLUME.
	 *               If less than 0 then the volume will not be set.
	 * @return current volume of the channel. If channel is -1, the average volume is returned.
	 * @exception SDLException if an error occurs
	 */
	public static int volume(int channel, int volume) throws SDLException {
		return SWIG_SDLMixer.Mix_Volume(channel, volume);
	}

	/**
	 * Play chunk on channel, or if channel is -1, pick the first free
	 * unreserved channel. The sample will play for loops+1 number of
	 * times, unless stopped by halt, or fade out, or setting a new
	 * expiration time of less time than it would have originally
	 * taken to play the loops, or closing the mixer.
	 * <P>
	 * Note: this just calls Mix_PlayChannelTimed() with ticks set to -1.
	 *
	 * @param channel Channel to play on, or -1 for the first free unreserved channel.
	 * @param chunk Sample to play
	 * @param loops Number of loops, -1 is infinite loops.
	 *              Passing one here plays the sample twice (1 loop).
	 * @return    the channel the sample is played on
	 * @exception SDLException if an error occurs
	 */
	public static int playChannel(int channel, MixChunk chunk, int loops) throws SDLException {
		int result = SWIG_SDLMixer.SWIG_Mix_PlayChannel(channel, chunk.getSwigMixChunk(), loops);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
		return result;
	}

	/**
	 * If the sample is long enough and has enough loops then the
	 * sample will stop after ticks milliseconds. Otherwise this
	 * function is the same as playChannel.
	 *
	 * @param channel Channel to play on, or -1 for the first free unreserved channel.
	 * @param chunk Sample to play
	 * @param loops Number of loops, -1 is infinite loops.
	 *              Passing one here plays the sample twice (1 loop).
	 * @param ticks Millisecond limit to play sample, at most.
	 *              If not enough loops or the sample chunk is not long enough,
	 *              then the sample may stop before this timeout occurs.
	 *              -1 means play forever.
	 * @return    the channel the sample is played on
	 * @exception SDLException if an error occurs
	 */
	public static int playChannelTimed(int channel, MixChunk chunk, int loops, int ticks) throws SDLException {
		int result = SWIG_SDLMixer.Mix_PlayChannelTimed(channel, chunk.getSwigMixChunk(), loops, ticks);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
		return result;
	}

	/**
	 * Play chunk on channel, or if channel is -1, pick the first free
	 * unreserved channel.  The channel volume starts at 0 and fades
	 * up to full volume over ms milliseconds of time. The sample may
	 * end before the fade-in is complete if it is too short or
	 * doesn't have enough loops. The sample will play for loops+1
	 * number of times, unless stopped by halt, or fade out, or
	 * setting a new expiration time of less time than it would have
	 * originally taken to play the loops, or closing the mixer.
	 * <P>
	 * Note: this just calls Mix_FadeInChannelTimed() with ticks set to -1.
	 *
	 * @param channel Channel to play on, or -1 for the first free unreserved channel.
	 * @param chunk   Sample to play
	 * @param loops   Number of loops, -1 is infinite loops.
	 *                Passing one here plays the sample twice (1 loop).
	 * @param ms      Milliseconds of time that the fade-in effect should take to go from silence to full volume.
	 * @return    the channel the sample is played on
	 * @exception SDLException if an error occurs
	 */
	public static int fadeInChannel(int channel, MixChunk chunk, int loops, int ms) throws SDLException {
		int result = SWIG_SDLMixer.SWIG_Mix_FadeInChannel(channel, chunk.getSwigMixChunk(), loops, ms);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
		return result;
	}

	/**
	 * f the sample is long enough and has enough loops then the
	 * sample will stop after ticks milliseconds. Otherwise this
	 * function is the same as Mix_FadeInChannel.
	 *
	 * @param channel Channel to play on, or -1 for the first free unreserved channel.
	 * @param chunk   Sample to play
	 * @param loops   Number of loops, -1 is infinite loops.
	 *                Passing one here plays the sample twice (1 loop).
	 * @param ms      Milliseconds of time that the fade-in effect should take to go from silence to full volume.
	 * @param ticks   Millisecond limit to play sample, at most.
	 *                If not enough loops or the sample chunk is not long enough,
	 *                then the sample may stop before this timeout occurs.
	 *                -1 means play forever.
	 * @return    the channel the sample is played on
	 * @exception SDLException if an error occurs
	 */
	public static int fadeInChannelTimed(int channel, MixChunk chunk, int loops, int ms, int ticks) throws SDLException {
		int result = SWIG_SDLMixer.Mix_FadeInChannelTimed(channel, chunk.getSwigMixChunk(), loops, ms, ticks);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
		return result;
	}

	/**
	 * Pause channel, or all playing channels if -1 is passed in. You
	 * may still halt a paused channel.
	 * <P>
	 * Note: Only channels which are actively playing will be paused.
	 *
	 * @param channel Channel to pause playing, or -1 for all channels.
	 * @exception SDLException if an error occurs
	 */
	public static void pause(int channel) throws SDLException {
		SWIG_SDLMixer.Mix_Pause(channel);
	}

	/**
	 * Unpause channel, or all playing and paused channels if -1 is passed in.
	 *
	 * @param channel Channel to resume playing, or -1 for all channels.
	 * @exception SDLException if an error occurs
	 */
	public static void resume(int channel) throws SDLException {
		SWIG_SDLMixer.Mix_Resume(channel);
	}

	/**
	 * Halt channel playback, or all channels if -1 is passed in.
	 * Any callback set by Mix_ChannelFinished will be called.
	 *
	 * @param channel Channel to stop playing, or -1 for all channels.
	 * @exception SDLException if an error occurs
	 */
	public static void haltChannel(int channel) throws SDLException {
		SWIG_SDLMixer.Mix_HaltChannel(channel);
	}

	/**
	 * Halt channel playback, or all channels if -1 is passed in,
	 * after ticks milliseconds. Any callback set by Mix_ChannelFinished will be called when the channel expires.
	 *
	 * @param channel Channel to stop playing, or -1 for all channels.
	 * @param ticks Millisecons until channel(s) halt playback.
	 * @return Number of channels set to expire. Whether or not they are active.
	 * @exception SDLException if an error occurs
	 */
	public static int expireChannel(int channel, int ticks) throws SDLException {
		return SWIG_SDLMixer.Mix_ExpireChannel(channel, ticks);
	}

	/**
	 * Gradually fade out which channel over ms milliseconds starting
	 * from now. The channel will be halted after the fade out is
	 * completed. Only channels that are playing are set to fade out,
	 * including paused channels.
	 * <p>
	 * Any callback set by Mix_ChannelFinished will be called when the channel finishes
	 * fading out.
	 *
	 * @param channel Channel to fade out, or -1 to fade all channels out.
	 * @param ms Milliseconds of time that the fade-out effect should take to go to silence, starting now.
	 * @return  The number of channels set to fade out.
	 * @exception SDLException if an error occurs
	 */
	public static int fadeOutChannel(int channel, int ms) throws SDLException {
		return SWIG_SDLMixer.Mix_FadeOutChannel(channel, ms);
	}

	// channelFinishedCallback goes here

	/**
	 * Tells you if channel is playing, or not. Note: Does not check if the channel has been paused.
	 *
	 * @param channel Channel to test whether it is playing or not.
	 *                -1 will tell you how many channels are playing.
	 * @return Zero if the channel is not playing.
	 *         Otherwise if you passed in -1, the number of channels playing is returned.
	 *         If you passed in a specific channel, then 1 is returned if it is playing
	 * @exception SDLException if an error occurs
	 */
	public static int playing(int channel) throws SDLException {
		return SWIG_SDLMixer.Mix_Playing(channel);
	}

	/**
	 * Tells you if channel is paused, or not.
	 * <P>
	 * Note: Does not check if the channel has been halted after it was paused, which may seem a little weird.
	 *
	 * @param channel Channel to test whether it is paused or not.
	 *                -1 will tell you how many channels are paused.
	 * @return Zero if the channel is not paused.
	 *        Otherwise if you passed in -1, the number of paused channels is returned.
	 *        If you passed in a specific channel, then 1 is returned if it is paused.
	 * @exception SDLException if an error occurs
	 */
	public static int paused(int channel) throws SDLException {
		return SWIG_SDLMixer.Mix_Paused(channel);
	}

	/**
	 * Tells you if which channel is fading in, out, or not. Does not
	 * tell you if the channel is playing anything, or paused, so
	 * you'd need to test that separately.
	 *
	 * @param which Channel to get the fade activity status from.
	 * @return the fading status
	 * @exception SDLException if an error occurs
	 */
	public static Mix_Fading fadingChannel(int which) throws SDLException {
		if (which == -1)
			throw new IllegalArgumentException("value of -1 for which is not valid!");

		return SWIG_SDLMixer.Mix_FadingChannel(which);
	}

	/**
	 * Get the most recent sample chunk pointer played on
	 * channel. This pointer may be currently playing, or just the
	 * last used.
	 * <P>
	 * Note: The actual chunk may have been freed, so this may not be valid anymore.
	 *
	 * @param channel Channel to get the current Mix_Chunk playing.
	 *                -1 is not valid
	 * @return a <code>MixChunk</code> value
	 * @exception SDLException if an error occurs
	 */
	public static MixChunk getChunk(int channel) throws SDLException {
		if (channel == -1)
			throw new IllegalArgumentException("value of -1 for which is not valid!");

		Mix_Chunk chunk = SWIG_SDLMixer.Mix_GetChunk(channel);

		return chunk == null ? null : new MixChunk(chunk);
	}

	////////////////////////////////////////////////////////////////////////////////
	// GROUPS
	/**
	 * Reserve num channels from being used when playing samples when
	 * passing in -1 as a channel number to playback functions. The
	 * channels are reserved starting from channel 0 to num-1. Passing
	 * in zero will unreserve all channels. Normally SDL_mixer starts
	 * without any channels reserved.
	 * <P>
	 * The following functions are affected by this setting:
	 * <P>
	 * Mix_PlayChannel
	 * <P>
	 * Mix_PlayChannelTimed
	 * <P>
	 * Mix_FadeInChannel
	 * <P>
	 * Mix_FadeInChannelTimed
	 *
	 * @param num Number of channels to reserve from default mixing.
	 *            Zero removes all reservations.
	 * @return The number of channels reserved. Never fails, but may
	 * return less channels than you ask for, depending on the number
	 * of channels previously allocated.
	 * @exception SDLException if an error occurs
	 */
	public static int reserveChannels(int num) throws SDLException {
		return SWIG_SDLMixer.Mix_ReserveChannels(num);
	}

	/**
	 * Add which channel to group tag, or reset it's group to the default group tag (-1).
	 *
	 * @param which Channel number of channels to assign tag to.
	 * @param tag A group number Any positive numbers (including zero).
	 *            -1 is the default group. Use -1 to remove a group tag essentially.
	 * @exception SDLException if an error occurs
	 */
	public static void groupChannel(int which, int tag) throws SDLException {
		int result = SWIG_SDLMixer.Mix_GroupChannel(which, tag);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Add channels starting at from up through to to group tag, or reset it's group to the default group tag (-1).
	 *
	 * @param from  First Channel number of channels to assign tag to. Must be less or equal to to.
	 * @param to Last Channel number of channels to assign tag to. Must be greater or equal to from.
	 * @param tag A group number. Any positive numbers (including zero).
	 *            -1 is the default group. Use -1 to remove a group tag essentially.
	 * @return The number of tagged channels on success. If that
	 * number is less than to-from+1 then some channels were no tagged
	 * because they didn't exist.
	 */
	public static int groupChannels(int from, int to, int tag) {
		return SWIG_SDLMixer.Mix_GroupChannels(from, to, tag);
	}

	/**
	 * Count the number of channels in group tag.
	 *
	 * @param tag A group number Any positive numbers (including zero).
	 *            -1 will count ALL channels.
	 * @return The number of channels found in the group. This function never fails.
	 * @exception SDLException if an error occurs
	 */
	public static int groupCount(int tag) throws SDLException {
		return SWIG_SDLMixer.Mix_GroupCount(tag);
	}

	/**
	 * Find the first available (not playing) channel in group tag.
	 *
	 * @param tag A group number Any positive numbers (including zero).
	 *            -1 will search ALL channels.
	 * @return The channel found on success. -1 is returned when no channels in the group are available.
	 * @exception SDLException if an error occurs
	 */
	public static int groupAvailable(int tag) throws SDLException {
		return SWIG_SDLMixer.Mix_GroupAvailable(tag);
	}

	/**
	 * Find the oldest actively playing channel in group tag.
	 *
	 * @param tag A group number Any positive numbers (including zero).
	 -1 will search ALL channels.
	 * @return The channel found on success.
	 *         -1 is returned when no channels in the group are playing or the group is empty
	 * @exception SDLException if an error occurs
	 */
	public static int groupOldest(int tag) throws SDLException {
		return SWIG_SDLMixer.Mix_GroupOldest(tag);
	}

	/**
	 * Find the newest, most recently started, actively playing channel in group tag.
	 *
	 * @param tag A group number Any positive numbers (including zero).
	 *            -1 will search ALL channels.
	 * @return The channel found on success.
	 *         -1 is returned when no channels in the group are playing or the group is empty.
	 * @exception SDLException if an error occurs
	 */
	public static int groupNewer(int tag) throws SDLException {
		return SWIG_SDLMixer.Mix_GroupNewer(tag);
	}

	/**
	 * Gradually fade out channels in group tag over ms milliseconds
	 * starting from now. The channels will be halted after the fade
	 * out is completed. Only channels that are playing are set to
	 * fade out, including paused channels. Any callback set by
	 * Mix_ChannelFinished will be called when each channel finishes
	 * fading out.
	 *
	 * @param tag Group to fade out.
	 *            NOTE: -1 will NOT fade all channels out. Use Mix_FadeOutChannel(-1) for that instead.
	 * @param ms Milliseconds of time that the fade-out effect should take to go to silence, starting now.
	 * @return The number of channels set to fade out.
	 * @exception SDLException if an error occurs
	 */
	public static int fadeOutGroup(int tag, int ms) throws SDLException {
		return SWIG_SDLMixer.Mix_FadeOutGroup(tag, ms);
	}

	/**
	 * Halt playback on all channels in group tag.
	 * Any callback set by Mix_ChannelFinished will be called once for each channel that stops.
	 *
	 * @param tag Group to fade out.
	 *        NOTE: -1 will NOT halt all channels. Use Mix_HaltChannel(-1) for that instead.
	 * @exception SDLException if an error occurs
	 */
	public static void haltGroup(int tag) throws SDLException {
		SWIG_SDLMixer.Mix_HaltGroup(tag);
	}

	////////////////////////////////////////////////////////////////////////////////
	// MUSIC

	/**
	 * Load music file to use. This can load WAVE, MOD, MIDI, OGG,
	 * MP3, and any file that you use a command to play with.  If you
	 * are using an external command to play the music, you must call
	 * Mix_SetMusicCMD before this, otherwise the internal players
	 * will be used. Alternatively, if you have set an external
	 * command up and don't want to use it, you must call
	 * Mix_SetMusicCMD(NULL) to use the built-in players again.
	 *
	 * @param path The path to the music file
	 * @return A MixMusic instance
	 * @exception SDLException if an error occurs
	 */
	public static MixMusic loadMUS(String path) throws SDLException {
		SWIGTYPE_p__Mix_Music mixMusic = SWIG_SDLMixer.Mix_LoadMUS(path);
		if (mixMusic == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new MixMusic(mixMusic);
	}
	
	    /**
	     * Load music file to use. This can load WAVE, MOD, MIDI, OGG,
	     * MP3, and any file that you use a command to play with.  If you
	     * are using an external command to play the music, you must call
	     * Mix_SetMusicCMD before this, otherwise the internal players
	     * will be used. Alternatively, if you have set an external
	     * command up and don't want to use it, you must call
	     * Mix_SetMusicCMD(NULL) to use the built-in players again.
	     *
	     * @param data a <code>byte[]</code> value
	     * @return A MixMusic instance
	     * @exception SDLException if an error occurs
	     * <P>
	     */
	    public static MixMusic loadMUS(byte[] data) throws SDLException, IOException {
	    	SWIGTYPE_p__Mix_Music mixMusic = SWIG_SDLMixer.Mix_LoadMUS(data, data.length);
	    	
	    	if (mixMusic == null) {
				throw new SDLException(SDLMain.getError());
			}
			return new MixMusic(mixMusic);
	    }
	
	
	    /**
	     * Load music file to use. This can load WAVE, MOD, MIDI, OGG,
	     * MP3, and any file that you use a command to play with.  If you
	     * are using an external command to play the music, you must call
	     * Mix_SetMusicCMD before this, otherwise the internal players
	     * will be used. Alternatively, if you have set an external
	     * command up and don't want to use it, you must call
	     * Mix_SetMusicCMD(NULL) to use the built-in players again.
	     *
	     * @param in an <code>InputStream</code> value
	     * @return A MixMusic instance
	     * @exception SDLException if an error occurs
	     * @exception IOException if an error occurs
	     * <P>
	     * This method automatically creates the NIO Direct Buffer
	     */
	    public static MixMusic loadMUS(InputStream in) throws SDLException, IOException {
	    	return loadMUS(BufferUtil.readInputStream(in));
	    }
	
	

	//    /**
	//     * Load music file to use. This can load WAVE, MOD, MIDI, OGG,
	//     * MP3, and any file that you use a command to play with.  If you
	//     * are using an external command to play the music, you must call
	//     * Mix_SetMusicCMD before this, otherwise the internal players
	//     * will be used. Alternatively, if you have set an external
	//     * command up and don't want to use it, you must call
	//     * Mix_SetMusicCMD(NULL) to use the built-in players again.
	//     *
	//     * @param buf a <I>DIRECT</I> <code>Buffer</code> value
	//     * @return A MixMusic instance
	//     * @exception SDLException if an error occurs
	//     */
	//    public static MixMusic loadMUS(ByteBuffer buf) throws SDLException, IOException {
	//	//SWIGTYPE_p__Mix_Music mixMusic = SWIG_SDLMixer.SWIG_Mix_LoadMUS_Buffer(buf, buf.capacity());
	//	//if (mixMusic == null) {
	//	//    throw new SDLException(SDLMain.getError());
	//	//}
	//	//return new MixMusic(mixMusic);
	//
	//	byte[] data = new byte[buf.capacity()];
	//	buf.get(data);
	//
	//	return loadMUS(data);
	//    }
	//
	//    /**
	//     * Load music file to use. This can load WAVE, MOD, MIDI, OGG,
	//     * MP3, and any file that you use a command to play with.  If you
	//     * are using an external command to play the music, you must call
	//     * Mix_SetMusicCMD before this, otherwise the internal players
	//     * will be used. Alternatively, if you have set an external
	//     * command up and don't want to use it, you must call
	//     * Mix_SetMusicCMD(NULL) to use the built-in players again.
	//     *
	//     * @param data a <code>byte[]</code> value
	//     * @return A MixMusic instance
	//     * @exception SDLException if an error occurs
	//     * <P>
	//     * This method automatically creates the NIO Direct Buffer from the given byte array
	//     */
	//    public static MixMusic loadMUS(byte[] data) throws SDLException, IOException {
	////	ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
	////	buf.put(data);
	////	
	////	return loadMUS(buf);
	//
	//	synchronized (SDLMixer.class) {
	//	    // open temp file and write data to it
	//	    String tmpDir = System.getProperty("java.io.tmpdir");
	//
	//	    BufferedOutputStream out = null;
	//	    try {
	//		out = new BufferedOutputStream(new FileOutputStream(tmpDir + java.io.File. separator + ".SDLMixer-loadMUS.tmp"));
	//
	//		out.write(data, 0, data.length);
	//		out.flush();
	//		out.close();
	//
	//		MixMusic music = loadMUS(tmpDir + java.io.File.separator + ".SDLMixer-loadMUS.tmp");
	//		
	//		return music;
	//	    } catch (IOException e) {
	//		throw new SDLException(e);
	//	    }
	//	    finally {
	//		if (out != null) out.close();
	//
	//		File f = new File(tmpDir + java.io.File.separator + ".SDLMixer-loadMUS.tmp");
	//		if (f.exists()) f.delete();
	//	    }
	//	}
	//    }
	//
	//    /**
	//     * Load music file to use. This can load WAVE, MOD, MIDI, OGG,
	//     * MP3, and any file that you use a command to play with.  If you
	//     * are using an external command to play the music, you must call
	//     * Mix_SetMusicCMD before this, otherwise the internal players
	//     * will be used. Alternatively, if you have set an external
	//     * command up and don't want to use it, you must call
	//     * Mix_SetMusicCMD(NULL) to use the built-in players again.
	//     *
	//     * @param url an <code>URL</code> value
	//     * @return A MixMusic instance
	//     * @exception SDLException if an error occurs
	//     * @exception IOException if an error occurs
	//     * @exception MalformedURLException if an error occurs
	//     * <P>
	//     * This method automatically creates the NIO Direct Buffer from the given byte array
	//     * <P>
	//     * NOTE:  for now this method downloads the url and saves the data in a temporary file
	//     *        then invokes the standard loadMUS(String path) method to load it, deleting
	//     *        the file afterwards.  Once we move to Mixer 1.2.6 this will not longer be necessary.
	//     */
	//    public static MixMusic loadMUS(URL url) throws SDLException, IOException, MalformedURLException {
	//	// read into buffer
	//	ByteBuffer buffer = BufferUtil.readURL(url);
	//	
	//	return loadMUS(buffer);
	//    }
	//
	//    /**
	//     * Load music file to use. This can load WAVE, MOD, MIDI, OGG,
	//     * MP3, and any file that you use a command to play with.  If you
	//     * are using an external command to play the music, you must call
	//     * Mix_SetMusicCMD before this, otherwise the internal players
	//     * will be used. Alternatively, if you have set an external
	//     * command up and don't want to use it, you must call
	//     * Mix_SetMusicCMD(NULL) to use the built-in players again.
	//     *
	//     * @param in an <code>InputStream</code> value
	//     * @return A MixMusic instance
	//     * @exception SDLException if an error occurs
	//     * @exception IOException if an error occurs
	//     * <P>
	//     * This method automatically creates the NIO Direct Buffer
	//     */
	//    public static MixMusic loadMUS(InputStream in) throws SDLException, IOException {
	//	return loadMUS( BufferUtil.readInputStream(in));
	//    }

	/**
	 * Free the loaded music. If music is playing it will be
	 * halted. If music is fading out, then this function will wait
	 * (blocking) until the fade out is complete.
	 *
	 * @param music a <code>MixMusic</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void freeMusic(MixMusic music) throws SDLException {
		SWIG_SDLMixer.Mix_FreeMusic(music.getSwigMixMusic());
	}

	/**
	 * Play the loaded music loop times through from start to
	 * finish. The previous music will be halted, or if fading out it
	 * waits (blocking) for that to finish.
	 *
	 * @param music Music to play
	 * @param loops number of times to play through the music.
	 *              0 plays the music zero times...
	 *              -1 plays the music forever (or as close as it can get to that)
	 * @exception SDLException if an error occurs
	 */
	public static void playMusic(MixMusic music, int loops) throws SDLException {
		int result = SWIG_SDLMixer.Mix_PlayMusic(music.getSwigMixMusic(), loops);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Fade in over ms milliseconds of time, the loaded music, playing
	 * it loop times through from start to finish.  The fade in effect
	 * only applies to the first loop.  Any previous music will be
	 * halted, or if it is fading out it will wait (blocking) for the
	 * fade to complete.  This function is the same as
	 * Mix_FadeInMusicPos(music, loops, ms, 0).
	 *
	 * @param music Music to play
	 * @param loops number of times to play through the music.
	 *              0 plays the music zero times...
	 *              -1 plays the music forever (or as close as it can get to that)
	 * @param ms Milliseconds for the fade-in effect to complete.
	 * @exception SDLException if an error occurs
	 */
	public static void fadeInMusic(MixMusic music, int loops, int ms) throws SDLException {
		int result = SWIG_SDLMixer.Mix_FadeInMusic(music.getSwigMixMusic(), loops, ms);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Fade in over ms milliseconds of time, the loaded music, playing
	 * it loop times through from start to finish.  The fade in effect
	 * only applies to the first loop.  The first time the music is
	 * played, it posistion will be set to posistion, which means
	 * different things for different types of music files, see
	 * Mix_SetMusicPosition for more info on that.  Any previous music
	 * will be halted, or if it is fading out it will wait (blocking)
	 * for the fade to complete
	 *
	 * @param music Music to play
	 * @param loops number of times to play through the music.
	 *              0 plays the music zero times...
	 *              -1 plays the music forever (or as close as it can get to that)
	 * @param ms Milliseconds for the fade-in effect to complete.
	 * @param position Posistion to play from, see setMusicPosition for meaning.
	 * @exception SDLException if an error occurs
	 */
	public static void fadeInMusicPos(MixMusic music, int loops, int ms, double position) throws SDLException {
		int result = SWIG_SDLMixer.Mix_FadeInMusicPos(music.getSwigMixMusic(), loops, ms, position);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	// Mix_HookMusic goes here

	/**
	 * Set the volume to volume, if it is 0 or greater, and return the
	 * previous volume setting. Setting the volume during a fade will
	 * not work, the faders use this function to perform their effect!
	 * Setting volume while using an external music player set by
	 * Mix_SetMusicCMD will have no effect, and Mix_GetError will show
	 * the reason why not.
	 *
	 * @param volume     Music volume, from 0 to MIX_MAX_VOLUME(128).
	 *                   Values greater than MIX_MAX_VOLUME will use MIX_MAX_VOLUME.
	 *                   -1 does not set the volume, but does return the current volume setting. 
	 * @return The previous volume setting.
	 * @exception SDLException if an error occurs
	 */
	public static int volumeMusic(int volume) throws SDLException {
		return SWIG_SDLMixer.Mix_VolumeMusic(volume);
	}

	/**
	 * Pause the music playback. You may halt paused music.
	 * <P>
	 * Note: Music can only be paused if it is actively playing.
	 *
	 */
	public static void pauseMusic() {
		SWIG_SDLMixer.Mix_PauseMusic();
	}

	/**
	 * Unpause the music. This is safe to use on halted, paused, and already playing music.
	 *
	 */
	public static void resumeMusic() {
		SWIG_SDLMixer.Mix_ResumeMusic();
	}

	/**
	 * Rewind the music to the start. This is safe to use on halted,
	 * paused, and already playing music. It is not useful to rewind
	 * the music immediately after starting playback, because it
	 * starts at the beginning by default.
	 * <P>
	 * This function only works for these streams: MOD, OGG, MP3, Native MIDI. 
	 *
	 */
	public static void rewindMusic() {
		SWIG_SDLMixer.Mix_RewindMusic();
	}

	/**
	 * Set the position of the currently playing music. The position
	 * takes different meanings for different music sources. It only
	 * works on the music sources listed below.
	 * <P>
	 * <UL>
	 * <LI>MOD<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;The double is cast to Uint16 and used for a pattern number in the module.
	 * Passing zero is similar to rewinding the song.
	 * </LI>
	 * <LI>
	 * OGG<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;Jumps to position seconds from the beginning of the song.
	 * </LI>
	 * <LI>
	 * MP3<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;Jumps to position seconds from the current position in the stream.
	 * So you may want to call Mix_RewindMusic before this.
	 * Does not go in reverse...negative values do nothing.
	 * </LI>
	 * </UL>
	 *
	 * @param position Position to play from 
	 * @return true on success, -1 if the codec doesn't support this function
	 * @exception SDLException if an error occurs
	 */
	public static boolean setMusicPosition(double position) throws SDLException {
		int result = SWIG_SDLMixer.Mix_SetMusicPosition(position);
		return result == 0 ? true : false;
	}

	//    /**
	//     * 
	//     *
	//     * @param command a <code>String</code> value
	//     * @exception SDLException if an error occurs
	//     */
	//    public static void setMusicCMD(String command) throws SDLException {
	//    }

	/**
	 * Halt playback of music. This interrupts music fader
	 * effects. Any callback set by Mix_HookMusicFinished will be
	 * called when the music stops.
	 *
	 * @exception SDLException if an error occurs
	 */
	public static void haltMusic() throws SDLException {
		int result = SWIG_SDLMixer.Mix_HaltMusic();
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Gradually fade out the music over ms milliseconds starting from
	 * now. The music will be halted after the fade out is
	 * completed. Only when music is playing and not fading already
	 * are set to fade out, including paused channels. Any callback
	 * set by Mix_HookMusicFinished will be called when the music
	 * finishes fading out.
	 *
	 * @param ms Milliseconds of time that the fade-out effect should take to go to silence, starting now.
	 * @exception SDLException if an error occurs
	 */
	public static void fadeOutMusic(int ms) throws SDLException {
		int result = SWIG_SDLMixer.Mix_FadeOutMusic(ms);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	// Mix_HookMusicFinished goes here

	/**
	 * Tells you the file format encoding of the music. This may be
	 * handy when used with Mix_SetMusicPosition, and other music
	 * functions that vary based on the type of music being played. If
	 * you want to know the type of music currently being played, pass
	 * in NULL to music.
	 *
	 * @param music The music to get the type of.
	 *              NULL will get the currently playing music type.
	 * @return The type of music or if music is NULL then the
	 * currently playing music type, otherwise MUS_NONE if no music is
	 * playing
	 * @exception SDLException if an error occurs
	 */
	public static Mix_MusicType getMusicType(MixMusic music) throws SDLException {
		return SWIG_SDLMixer.Mix_GetMusicType(music.getSwigMixMusic());
	}

	/**
	 * Tells you if music is actively playing, or not.
	 * <P>
	 * Note: Does not check if the channel has been paused.
	 *
	 * @return if music is actively playing
	 */
	public static boolean playingMusic() {
		int result = SWIG_SDLMixer.Mix_PlayingMusic();
		return result == 1 ? true : false;
	}

	/**
	 * Tells you if music is paused, or not.
	 * <P>
	 * Note: Does not check if the music was been halted after it was paused, which may seem a little weird.
	 *
	 * @return if music is paused
	 */
	public static boolean pausedMusic() {
		int result = SWIG_SDLMixer.Mix_PausedMusic();
		return result == 1 ? true : false;
	}

	/**
	 * Tells you if music is fading in, out, or not at all. Does not
	 * tell you if the channel is playing anything, or paused, so
	 * you'd need to test that separately.
	 *
	 * @return a <code>Mix_Fading</code> value
	 */
	public static Mix_Fading fadingMusic() {
		return SWIG_SDLMixer.Mix_FadingMusic();
	}

	// Mix_GetMusicHookData goes here

	////////////////////////////////////////////////////////////////////////////////
	// Effects

	/**
	 * This effect will only work on stereo audio. Meaning you called
	 * Mix_OpenAudio with 2 channels (MIX_DEFAULT_CHANNELS). The
	 * easiest way to do true panning is to call
	 * Mix_SetPanning(channel, left, 254 - left); so that the total
	 * volume is correct, if you consider the maximum volume to be 127
	 * per channel for center, or 254 max for left, this works, but
	 * about halves the effective volume.  This Function registers the
	 * effect for you, so don't try to Mix_RegisterEffect it yourself.
	 * <P>
	 * NOTE: Setting both left and right to 255 will unregister the
	 * effect from channel. You cannot unregister it any other way,
	 * unless you use Mix_UnregisterAllEffects on the channel.  NOTE:
	 * Using this function on a mono audio device will not register
	 * the effect, nor will it return an error status.
	 *
	 * @param channel Channel number to register this effect on.
	 *                Use MIX_CHANNEL_POST to process the postmix stream.
	 * @param left Volume for the left channel, range is 0(silence) to 255(loud)
	 * @param right Volume for the left channel, range is 0(silence) to 255(loud)
	 * @exception SDLException if an error occurs
	 */
	public static void setPanning(int channel, int left, int right) throws SDLException {
		int result = SWIG_SDLMixer.Mix_SetPanning(channel, (short) left, (short) right);
		if (result == 0) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * This effect simulates a simple attenuation of volume due to
	 * distance. The volume never quite reaches silence, even at max
	 * distance.
	 * <P>
	 * NOTE: Using a distance of 0 will cause the effect to
	 * unregister itself from channel. You cannot unregister it any
	 * other way, unless you use Mix_UnregisterAllEffects on the
	 * channel.
	 *
	 * @param channel Channel number to register this effect on.
	 *                Use MIX_CHANNEL_POST to process the postmix stream.
	 * @param distance  Specify the distance from the listener, from 0(close/loud) to 255(far/quiet).
	 * @exception SDLException if an error occurs
	 */
	public static void setDistance(int channel, int distance) throws SDLException {
		int result = SWIG_SDLMixer.Mix_SetDistance(channel, (short) distance);
		if (result == 0) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * This effect emulates a simple 3D audio effect. It's not all
	 * that realistic, but it can help improve some level of
	 * realism. By giving it the angle and distance from the camera's
	 * point of view, the effect pans and attenuates volumes. If you
	 * are looking for better positional audio, using OpenAL is
	 * suggested.
	 * <P>
	 * NOTE: Using angle and distance of 0, will cause the
	 * effect to unregister itself from channel. You cannot unregister
	 * it any other way, unless you use Mix_UnregisterAllEffects on
	 * the channel.
	 *
	 * @param channel Channel number to register this effect on.
	 *                Use MIX_CHANNEL_POST to process the postmix stream.
	 * @param angle Direction in relation to forward from 0 to 360 degrees.
	 *              Larger angles will be reduced to this range using angles % 360.
	 *      <br>0 = directly in front.
	 *      <br>90 = directly to the right.
	 *      <br>180 = directly behind.
	 *      <br>270 = directly to the left.
	 *      <P>
	 *      So you can see it goes clockwise starting at directly in front.
	 *      This ends up being similar in effect to Mix_SetPanning.
	 * 
	 * @param distance     The distance from the listener, from 0(near/loud) to 255(far/quiet).
	 *                      This is the same as the Mix_SetDistance effect.
	 * @exception SDLException if an error occurs
	 */
	public static void setPosition(int channel, int angle, int distance) throws SDLException {
		int result = SWIG_SDLMixer.Mix_SetPosition(channel, (short) angle, (short) distance);
		if (result == 0) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * get a version structure with the compile-time version of the SDL_mixer library.
	 *
	 *
	 * @return the compile-time version of the SDL_mixer library.
	 */
	public static SDLVersion getMixVersion() {
		SDL_version v = SWIG_SDLMixer.SWIG_MIX_VERSION();
		return new SDLVersion(v);
	}

} // class SDLMixer