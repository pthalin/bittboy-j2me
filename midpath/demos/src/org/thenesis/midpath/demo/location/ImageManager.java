/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.location;

import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Image;

/**
 * Lets you load image from resource and stores it in cache for
 * later usage
 */
public class ImageManager {
	private static ImageManager im = null;
	private static Hashtable imageCache = null;

	private ImageManager() {
		imageCache = new Hashtable();
	}

	public static ImageManager getInstance() {
		if (im == null) {
			im = new ImageManager();
		}

		return im;
	}

	/**
	 * Load image from resource and store it
	 * in cache.
	 */
	public Image getImage(String name) {
		Image image = null;

		try {
			if (null == (image = (Image) imageCache.get(name))) {
				InputStream is = getClass().getResourceAsStream("data/" + name + ".png");
				image = Image.createImage(is);
				imageCache.put(name, image);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return image;
	}

	/**
	 * Batch load images into cache
	 */
	public void loadImagesCache(String[] names) {
		for (int i = 0; i < names.length; i++) {
			try {
				if (names[i] != null) {
					InputStream is = getClass().getResourceAsStream("data/" + names[i] + ".png");
					imageCache.put(names[i], Image.createImage(is));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Batch load images into cache
	 */
	public void loadImagesCache(Enumeration e) {
		for (; e.hasMoreElements();) {
			try {
				String name = (String) e.nextElement();
				imageCache.put(name, Image.createImage("/" + name + ".png"));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
