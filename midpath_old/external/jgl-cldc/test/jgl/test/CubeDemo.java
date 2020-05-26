package jgl.test;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.MemoryImageSource;

import jgl.GL;
import jgl.GLBackend;
import jgl.context.GLContext;

public class CubeDemo extends Applet implements ComponentListener {
	
	private GL myGL;
	private GLAUX myAUX;

	private void display() {
		myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
		myGL.glColor3f(1.0f, 1.0f, 1.0f);
		myGL.glLoadIdentity(); /* clear the matrix */
		myGL.glTranslatef(0.0f, 0.0f, -5.0f);
		/* viewing transformation */
		myGL.glScalef(1.0f, 2.0f, 1.0f);
		/* modeling transformation */
		myAUX.auxWireCube(1.0); /* draw the cube */
		myGL.glFlush();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		// get window width and height by myself
		myReshape(getSize().width, getSize().height);
		display();
		repaint();
	}

	private void myReshape(int w, int h) {
		myGL.glMatrixMode(GL.GL_PROJECTION); /* prepare for and then */
		myGL.glLoadIdentity(); /* define the projection */
		myGL.glFrustum(-1.0f, 1.0f, /* transformation */
		-1.0f, 1.0f, 1.5f, 20.0f);
		myGL.glMatrixMode(GL.GL_MODELVIEW); /* back to modelview matrix */
		myGL.glViewport(0, 0, w, h); /* define the viewport */
	}

	public void update(Graphics g) {
		// skip the clear screen command....
		paint(g);
	}

	public void paint(Graphics g) {
		myGL.glXSwapBuffers();
	}

	public void init() {
		
		int w = 500;
		int h = 500;
		this.setSize(w, h);
		
		SEBackend backend = new SEBackend(this, 0, 0);
		GLContext context = new GLContext();
		myGL = new GL(context);
		myAUX = new GLAUX(myGL);
		
		// Attach a GL backend to a GL rendering context
		myGL.glXMakeCurrent(context, backend);
		myGL.glShadeModel(GL.GL_FLAT);

		// as call auxReshapeFunc()
		addComponentListener(this);
		myReshape(backend.getWidth(), backend.getHeight());

		// call display as call auxMainLoop(display);
		display();
	}
	
	private class SEBackend implements GLBackend {
		
		private Component c;
		private Image offscreenImage;
		private int startY;
		private int startX;

		public SEBackend(Component c, int startX, int startY) {
			this.c = c;
			this.startX = startX;
			this.startY = startY;
		}

		public int[] getColorBuffer(int size) {
			return new int[size];
		}

		public float[] getDepthBuffer(int size) {
			return new float[size];
		}

		public int getHeight() {
			return c.getHeight();
		}

		public int[] getStencilBuffer(int size) {
			return new int[size];
		}

		public int getWidth() {
			return c.getWidth();
		}

		public int getX() {
			return startX;
		}

		public int getY() {
			return startY;
		}

		public void sync() {
			c.getGraphics().drawImage(offscreenImage, startX, startY, null);
		}

		public void updatePixels(int w, int h, int[] pix, int off, int scan) {
			offscreenImage = c.createImage(new MemoryImageSource(w, h,
					pix, off, scan));
		}
		
	}

}
