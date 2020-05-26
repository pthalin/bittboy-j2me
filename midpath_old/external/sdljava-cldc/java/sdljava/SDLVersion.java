package sdljava;

import sdljava.x.swig.SDL_version;

/**
 * SDL Version information
 *
 * @author  Ivan Z. Ganza
 * @version $Id: SDLVersion.java,v 1.1 2005/02/15 03:10:55 ivan_ganza Exp $
 */
public class SDLVersion {

	int major;
	int minor;
	int patch;

	public SDLVersion(SDL_version v) {
		this.major = v.getMajor();
		this.minor = v.getMinor();
		this.patch = v.getPatch();
	}

	/**
	 * Gets the value of major
	 *
	 * @return the value of major
	 */
	public int getMajor() {
		return this.major;
	}

	/**
	 * Gets the value of minor
	 *
	 * @return the value of minor
	 */
	public int getMinor() {
		return this.minor;
	}

	/**
	 * Gets the value of patch
	 *
	 * @return the value of patch
	 */
	public int getPatch() {
		return this.patch;
	}
}