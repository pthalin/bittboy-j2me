package sdljava.event;

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
import java.util.Hashtable;

import sdljava.x.swig.SDLModValues;

/**
 * Set of possible key modifiers(mods)
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLMod.java,v 1.8 2005/01/19 03:09:12 ivan_ganza Exp $
 */
public class SDLMod {

	/**
	 * cache of SDLMod instances, one for each possible
	 * mods values
	 *
	 */
	static Hashtable modCache = new Hashtable();

	/**
	 * valid key mods (possibly OR'd together)
	 *
	 */
	int mods;

	/**
	 * Creates a new <code>SDLMod</code> instance.
	 *
	 * @param mods valid key mods (possibly OR'd together)
	 */
	public SDLMod(int mods) {
		this.mods = mods;
	}

	/**
	 * Get the SDLMod instance identified by mods.  This method
	 * creates the SDLMod instance and caches it if it didn't
	 * already exist.  Once created we won't need to create new
	 * SDLMod object instances each time a keyboard event occurs.
	 *
	 * @param mods valid key mods (possibly OR'd together)
	 * @return The singleton SDLMod instance
	 */
	public static SDLMod get(int mods) {
		SDLMod mod = (SDLMod) modCache.get(new Integer(mods));
		if (mod != null)
			return mod;

		mod = new SDLMod(mods);
		modCache.put(new Integer(mods), mod);
		return mod;
	}

	public boolean leftShift() {
		return ((mods & SDLModValues.KMOD_LSHIFT.swigValue()) == SDLModValues.KMOD_LSHIFT.swigValue());
	}

	public boolean rightShift() {
		return ((mods & SDLModValues.KMOD_RSHIFT.swigValue()) == SDLModValues.KMOD_RSHIFT.swigValue());
	}

	public boolean leftCtrl() {
		return ((mods & SDLModValues.KMOD_LCTRL.swigValue()) == SDLModValues.KMOD_LCTRL.swigValue());
	}

	public boolean rightCtrl() {
		return ((mods & SDLModValues.KMOD_RCTRL.swigValue()) == SDLModValues.KMOD_RCTRL.swigValue());
	}

	public boolean leftAlt() {
		return ((mods & SDLModValues.KMOD_LALT.swigValue()) == SDLModValues.KMOD_LALT.swigValue());
	}

	public boolean rightAlt() {
		return ((mods & SDLModValues.KMOD_RALT.swigValue()) == SDLModValues.KMOD_RALT.swigValue());
	}

	public boolean leftMeta() {
		return ((mods & SDLModValues.KMOD_LMETA.swigValue()) == SDLModValues.KMOD_LMETA.swigValue());
	}

	public boolean rightMeta() {
		return ((mods & SDLModValues.KMOD_RMETA.swigValue()) == SDLModValues.KMOD_RMETA.swigValue());
	}

	public boolean num() {
		return ((mods & SDLModValues.KMOD_NUM.swigValue()) == SDLModValues.KMOD_NUM.swigValue());
	}

	public boolean caps() {
		return ((mods & SDLModValues.KMOD_CAPS.swigValue()) == SDLModValues.KMOD_CAPS.swigValue());
	}

	public boolean mode() {
		return ((mods & SDLModValues.KMOD_MODE.swigValue()) == SDLModValues.KMOD_MODE.swigValue());
	}

	public boolean ctrl() {
		return ((mods & (SDLModValues.KMOD_RCTRL.swigValue() | SDLModValues.KMOD_LCTRL.swigValue())) != 0);
	}

	public boolean shift() {
		return ((mods & (SDLModValues.KMOD_RSHIFT.swigValue() | SDLModValues.KMOD_LSHIFT.swigValue())) != 0);
	}

	public boolean alt() {
		return ((mods & (SDLModValues.KMOD_RALT.swigValue() | SDLModValues.KMOD_LALT.swigValue())) != 0);
	}

	public boolean meta() {
		return ((mods & (SDLModValues.KMOD_RMETA.swigValue() | SDLModValues.KMOD_LMETA.swigValue())) != 0);
	}

	public int getState() {
		return mods;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLMod[leftShift=").append(leftShift()).append(", rightShift=").append(rightShift()).append(
				", leftCtrl=").append(leftCtrl()).append(", rightCtrl=").append(rightCtrl()).append(", leftAlt=")
				.append(leftAlt()).append(", rightAlt=").append(rightAlt()).append(", num=").append(num()).append(
						", caps=").append(caps()).append(", mode=").append(mode()).append("]");

		return buf.toString();
	}

	public String toStringBrief() {
		StringBuffer buf = new StringBuffer();

		buf.append(leftShift() ? " LSHIFT" : " ").append(rightShift() ? " RSHIFT" : " ").append(
				leftCtrl() ? " LCTRL" : " ").append(rightCtrl() ? " RCTRL" : " ").append(leftAlt() ? " LALT" : " ")
				.append(rightAlt() ? " RALT" : " ").append(num() ? " NUM" : " ").append(caps() ? " CAPS" : " ").append(
						mode() ? " MOD" : " ");

		return buf.toString();
	}
}