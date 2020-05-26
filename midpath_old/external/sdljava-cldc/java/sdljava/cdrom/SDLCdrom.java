package sdljava.cdrom;

/**
 *  sdljava - a java binding to the SDL API
 *
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
import java.util.Vector;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.x.swig.SDL_CD;
import sdljava.x.swig.SDL_CDtrack;
import sdljava.x.swig.SWIG_SDLCdrom;

/**
 * Binding to the SDL Cdrom routines.
 *
 * 
 * @version $Id: SDLCdrom.java,v 1.5 2005/01/19 03:09:11 ivan_ganza Exp $
 */
public class SDLCdrom {

	SDL_CD swigCdrom;
	int driveIndex;

	private SDLCdrom(SDL_CD swigCdrom, int driveIndex) {
		this.swigCdrom = swigCdrom;
		this.driveIndex = driveIndex;
	}

	public SDL_CD getSwigCdrom() {
		return swigCdrom;
	}

	/**
	 * Returns the number of CD-ROM drives on the system.
	 *
	 * @return Returns the number of CD-ROM drives on the system.
	 */
	public static int numDrives() {
		return SWIG_SDLCdrom.SDL_CDNumDrives();
	}

	/**
	 * Returns a human-readable, system-dependent identifier for the CD-ROM.
	 *
	 * @param drive drive is the index of the drive
	 * @return a human-readable, system-dependent identifier for the CD-ROM
	 */
	public static String cdName(int drive) {
		return SWIG_SDLCdrom.SDL_CDName(drive);
	}

	/**
	 * Describe <code>cdOpen</code> method here.
	 * <P>
	 * Opens a CD-ROM drive for access. It returns a SDL_CD structure
	 * on success, or NULL if the drive was invalid or busy. This
	 * newly opened CD-ROM becomes the default CD used when other CD
	 * functions are passed a NULL CD-ROM handle.
	 * <P>
	 * Drives are numbered starting with 0. Drive 0 is the system default CD-ROM.
	 *
	 * @param drive an <code>int</code> value
	 * @return a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLCdrom cdOpen(int drive) throws SDLException {
		SDL_CD cd = SWIG_SDLCdrom.SDL_CDOpen(drive);
		if (cd == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLCdrom(cd, drive);
	}

	/**
	 * Returns the current status of the given drive.
	 * <P>
	 * If the drive has a CD in it, the table of contents of the CD
	 * and current play position of the CD will be stored in the cdrom
	 * object.
	 * 
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @return a <code>CDstatus</code> value
	 */
	public static CDstatus cdStatus(SDLCdrom cdrom) {
		return CDstatus.swigToEnum(SWIG_SDLCdrom.SDL_CDStatus(cdrom.getSwigCdrom()));
		//return SWIG_SDLCdrom.SDL_CDStatus(cdrom.getSwigCdrom());
	}

	/**
	 * Play a CD
	 * <P>
	 * Plays the given cdrom, starting a frame start for length frames.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @param start an <code>int</code> value
	 * @param length an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void cdPlay(SDLCdrom cdrom, int start, int length) throws SDLException {
		int result = SWIG_SDLCdrom.SDL_CDPlay(cdrom.getSwigCdrom(), start, length);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Play the given CD track(s)
	 * <P>
	 * SDL_CDPlayTracks plays the given CD starting at track start_track, for ntracks tracks.
	 * <P>
	 *      start_frame is the frame offset, from the beginning of the
	 *      start_track, at which to start. nframes is the frame offset, from the
	 *      beginning of the last track (start_track+ntracks), at which to end
	 *      playing.
	 * <P>
	 *      SDL_CDPlayTracks should only be called after calling SDL_CDStatus to
	 *      get track information about the CD.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @param start_track an <code>int</code> value
	 * @param start_frame an <code>int</code> value
	 * @param ntrakcs an <code>int</code> value
	 * @param nframes an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void cdPlayTracks(SDLCdrom cdrom, int start_track, int start_frame, int ntracks, int nframes)
			throws SDLException {
		int result = SWIG_SDLCdrom.SDL_CDPlayTracks(cdrom.getSwigCdrom(), start_track, start_frame, ntracks, nframes);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Pauses a CDROM
	 * <P>
	 * Pauses play on the given cdrom.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void cdPause(SDLCdrom cdrom) throws SDLException {
		int result = SWIG_SDLCdrom.SDL_CDPause(cdrom.getSwigCdrom());
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Resumes a CDROM
	 * <P>
	 * Resumes play on the given cdrom.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void cdResume(SDLCdrom cdrom) throws SDLException {
		int result = SWIG_SDLCdrom.SDL_CDResume(cdrom.getSwigCdrom());
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Stops a CDROM
	 * <P>
	 * Stops play on the given cdrom.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void cdStop(SDLCdrom cdrom) throws SDLException {
		int result = SWIG_SDLCdrom.SDL_CDStop(cdrom.getSwigCdrom());
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Ejects a CDROM
	 * <P>
	 * Ejects the given cdrom.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void cdEject(SDLCdrom cdrom) throws SDLException {
		int result = SWIG_SDLCdrom.SDL_CDEject(cdrom.getSwigCdrom());
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Closes a cdrom
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 */
	public static void cdClose(SDLCdrom cdrom) {
		SWIG_SDLCdrom.SDL_CDClose(cdrom.getSwigCdrom());
	}

	/**
	 * Returns the current status of this drive.
	 * <P>
	 * If the drive has a CD in it, the table of contents of the CD
	 * and current play position of the CD will be stored in the cdrom
	 * object.
	 * 
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @return a <code>CDstatus</code> value
	 */
	public CDstatus cdStatus() {
		return cdStatus(this);
	}

	/**
	 * Play a CD
	 * <P>
	 * Plays starting a frame start for length frames.
	 *
	 * @param start an <code>int</code> value
	 * @param length an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public void cdPlay(int start, int length) throws SDLException {
		cdPlay(this, start, length);
	}

	/**
	 * Play the given CD track(s)
	 * <P>
	 * SDL_CDPlayTracks plays the given CD starting at track start_track, for ntracks tracks.
	 * <P>
	 *      start_frame is the frame offset, from the beginning of the
	 *      start_track, at which to start. nframes is the frame offset, from the
	 *      beginning of the last track (start_track+ntracks), at which to end
	 *      playing.
	 * <P>
	 *      SDL_CDPlayTracks should only be called after calling SDL_CDStatus to
	 *      get track information about the CD.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @param start_track an <code>int</code> value
	 * @param start_frame an <code>int</code> value
	 * @param ntrakcs an <code>int</code> value
	 * @param nframes an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public void cdPlayTracks(int start_track, int start_frame, int ntracks, int nframes) throws SDLException {
		cdPlayTracks(this, start_track, start_frame, ntracks, nframes);
	}

	/**
	 * Pauses a CDROM
	 * <P>
	 * Pauses play
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public void cdPause() throws SDLException {
		cdPause(this);
	}

	/**
	 * Resumes a CDROM
	 * <P>
	 * Resumes play
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public void cdResume() throws SDLException {
		cdResume(this);
	}

	/**
	 * Stops a CDROM
	 * <P>
	 * Stops play
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public void cdStop() throws SDLException {
		cdStop(this);
	}

	/**
	 * Ejects a CDROM
	 * <P>
	 * Ejects the cdrom.
	 *
	 * @param cdrom a <code>SDLCdrom</code> value
	 * @exception SDLException if an error occurs
	 */
	public void cdEject() throws SDLException {
		cdEject(this);
	}

	/**
	 * Close the cdrom
	 *
	 */
	public void cdClose() {
		cdClose(this);
	}

	/**
	 * Get the private drive identifier
	 *
	 * @return the private drive identifier
	 */
	public int getId() {
		return swigCdrom.getId();
	}

	/**
	 * Get the number of tracks on the CD
	 *
	 * @return the number of tracks on the CD
	 */
	public int getNumTracks() {
		return swigCdrom.getNumtracks();
	}

	/**
	 * Get the current track
	 *
	 * @return the current track
	 */
	public int getCurrentTrack() {
		return swigCdrom.getCur_track();
	}

	/**
	 * Get the current frame offset within the track
	 *
	 * @return the current frame offset within the track
	 */
	public int getCurrentFrame() {
		return swigCdrom.getCur_frame();
	}

	/**
	 * Get the list of tracks
	 *
	 * @return the list of tracks
	 */
	public Vector getTracks() {
		Vector l = new Vector();

		int count = getNumTracks();
		for (int i = 0; i < count; i++) {
			SDL_CDtrack track = SWIG_SDLCdrom.SWIG_SDL_getTrack(this.getSwigCdrom(), i);
			l.addElement(new SDLCdTrack(track));
		}

		return l;
	}

	public int getDriveIndex() {
		return this.driveIndex;
	}

	public FrameInfo getCurrentFrameInfo() {
		int[] m = { 0 };
		int[] s = { 0 };
		int[] f = { 0 };

		int frame = getCurrentFrame();

		SWIG_SDLCdrom.SWIG_framesToMSF(frame, m, s, f);

		return new FrameInfo(frame, m[0], s[0], f[0]);
	}

	// FIXME 
	protected void finalize() {
		if (swigCdrom != null)
			cdClose();
	}
}