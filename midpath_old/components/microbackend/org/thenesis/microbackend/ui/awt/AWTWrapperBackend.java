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
package org.thenesis.microbackend.ui.awt;

import java.awt.Container;
import java.awt.Panel;
import java.io.IOException;

import org.thenesis.microbackend.ui.Configuration;

public class AWTWrapperBackend extends AbstractAWTBackend {

	private Container container;

	public AWTWrapperBackend() {
	}

    public AWTWrapperBackend(Object c) {
	    container = (Container)c;
        canvasWidth = container.getWidth();
        canvasHeight = container.getHeight();     
    }
	
	/* UIBackend interface */

	public void configure(Configuration conf, int width, int height) {
	    // Do nothing
	}
	
	public void open() throws IOException {
	    Panel panel = createPanel();
		container.add(panel);
		//container.setVisible(false);
		container.setVisible(true);
//		container.invalidate();
//		container.repaint();
	}
	
	public void close() {
	    // Do nothing
	}

}
