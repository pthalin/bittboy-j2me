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
package org.thenesis.midpath.svg.awt;

import java.awt.Graphics;

import javax.microedition.m2g.ScalableGraphics;

/**
 *
 */
public class ScalableGraphicsImpl extends ScalableGraphics  {

    /**
     * The Graphics object bound to this class
     */
    Graphics g = null;

    /**
    * Constructor
    */
    ScalableGraphicsImpl() {
    	super();
    }
	
    /**
     *
     */
    public void bindTarget(java.lang.Object target) {
        if (target == null) {
            throw new NullPointerException();
        }
        
        if (!(target instanceof Graphics)) {
            throw new IllegalArgumentException();
        }
        
        if (g != null) {
            throw new IllegalStateException("bindTarget(" + target + ") with g : " + g);
        }
        
        g = (Graphics) target;
        gsd = new GraphicsSurfaceDestinationImpl(g);
    }
    
    /**
     *
     */
    public void releaseTarget() {
        if (g == null) {
            throw new IllegalStateException("releaseTarget() with null current target");
        }

        g = null;
        gsd = null;
    }
    
    protected boolean isGraphicsNull() {
    	return (g == null);
    }

    /**
     *
     */
    public static ScalableGraphics createInstance() {
        return new ScalableGraphicsImpl();
    }

}
