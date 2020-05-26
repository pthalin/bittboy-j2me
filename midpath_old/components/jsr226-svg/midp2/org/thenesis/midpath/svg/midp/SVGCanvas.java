/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */
package org.thenesis.midpath.svg.midp;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import com.sun.perseus.model.AbstractSVGCanvas;
import com.sun.perseus.model.DocumentNode;

/**
 * This class provides support for an LCDUI Canvas extension which can display
 * an SVG Document.
 *
 * @version $Id: SVGCanvas.java,v 1.16 2006/04/21 06:40:56 st125089 Exp $
 */
class SVGCanvas extends AbstractSVGCanvas {

	protected Canvas midpCanvas;


	/**
	 * @param documentNode the documentNode this component will render. The input
	 *        DocumentNode must be fully loaded before this method is called.
	 *        Note: if the DocumentNode already has an associated RunnableQueue,
	 *        it is simply replaced. It is the responsibility of the caller to 
	 *        stop that RunnableQueue if need be.
	 * @throws IllegalArgumentException see {@link #setURI setURI}.
	 */
	public SVGCanvas(final DocumentNode documentNode) {
		super(documentNode);
		midpCanvas = new MIDPCanvas();
	}

	protected int getNativeCanvasWidth() {
		return midpCanvas.getWidth();
	}

	protected int getNativeCanvasHeight() {
		return midpCanvas.getHeight();
	}

	protected void repaintNative(int x, int y, int width, int height) {
		midpCanvas.repaint(x, y, width, height);
	}

	// ========================================================================

	class MIDPCanvas extends Canvas {

		/**
		 * @see javax.microedition.lcdui.Canvas#paint
		 */
		protected void paint(final Graphics g) {
			checkOffscreen();
			int x = g.getClipX();
			int y = g.getClipY();
			int w = g.getClipWidth();
			int h = g.getClipHeight();

			synchronized (canvasManager.lock) {
				if (x != 0 || y != 0 || w != documentNode.getWidth() || h != documentNode.getHeight()) {
					// The repaint area is not exactly the same as the viewport area
					// so we need to clear the background first.
					g.setColor(CLEAR_COLOR);
					g.fillRect(x, y, w, h);
				}

				if (gsd == null) {
					gsd = new GraphicsSurfaceDestinationImpl(g);
				}
				gsd.drawSurface(offscreen, 0, 0, 0, 0, offscreenWidth, offscreenHeight, 1);
				canvasManager.consume();
			}
		}

		/**
		 * Event Listeners used to turn MIDP Events into DOM Events. It also
		 * switches between the MIDP event thread and the document's update
		 * thread (i.e., the <code>RunnableQueue</code>'s thread.
		 */

		
		/**
		 * Invoked when a mouse button has been pressed on a component.
		 * @param x the x-axis coordinate of the pointer event
		 * @param y the y-axis coordinate of the pointer event
		 */
		protected void pointerPressed(final int x, final int y) {
			handlePointerPressed(x, y);
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 * @param x the x-axis coordinate of the pointer event
		 * @param y the y-axis coordinate of the pointer event
		 */
		protected void pointerReleased(final int x, final int y) {
			handlePointerReleased(x, y);
		}

		/**
		 * Invoked when a key has been pressed.
		 * @param keyCode the code of the event key
		 */
		protected void keyPressed(int keyCode) {
			handleKeyPressed(keyCode);
		}

		/**
		 * Invoked when a key has been released.
		 * @param keyCode the code of the event key
		 */
		protected void keyReleased(int keyCode) {
			handleKeyReleased(keyCode);
		}

		/**
		 * Invoked when the component's size changes.
		 *
		 * @param w the new width
		 * @param h the new height
		 */
		protected void sizeChanged(final int w, final int h) {
			handleSizeChanged(w, h);
		}

		/**
		 * Invoked when the component is hidden.
		 */
		protected void hideNotify() {
			handleHideNotify();
		}

		/**
		 * Invoked when the component is shown.
		 */
		protected void showNotify() {
			handleShowNotify();
		}

	}

	// ========================================================================

	public Canvas getMidpCanvas() {
		return midpCanvas;
	}

}
