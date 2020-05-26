package org.thenesis.midpath.opengles.pjogles;

import java.util.Hashtable;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import org.thenesis.midpath.opengles.AbstractJavaEGL10;
import org.thenesis.midpath.opengles.JavaEGLSurface;
import org.thenesis.pjogles.GLBackend;
import org.thenesis.pjogles.GLSoftwareContext;

public class JavaEGL10 extends AbstractJavaEGL10 {

	
	public void makeCurrent(EGLDisplay display, EGLSurface draw, EGLSurface read, EGLContext context) {
		JavaEGLContext jContext = (JavaEGLContext) context;
		MIDPBackend backend = new MIDPBackend(((JavaEGLSurface) draw));
		GLSoftwareContext jglContext = jContext.getPJOGLESContext();
		JavaGL10 gl = (JavaGL10)jContext.getGL();
		gl.getGL11Software().glXMakeCurrent(jglContext, backend);
	}
	
	public Hashtable getContextsByThread() {
		return JavaGL10.contextsByThread;
	}


	public void grabContext() {
		JavaGL10.grabContext();
	}


	public void setCurrentContext(EGLContext context) {
		JavaGL10.currentContext = context;
	}
	
	public EGLContext createEGLContext() {
		return new JavaEGLContext();
	}
	
	
	private class MIDPBackend implements GLBackend {
		
		private JavaEGLSurface surface;

		public MIDPBackend(JavaEGLSurface surface) {
			this.surface = surface;
		}

		public int[] getColorBuffer(int size) {
			return surface.getBuffer();
		}

		public float[] getDepthBuffer(int size) {
			return new float[size];
		}

		public int getHeight() {
			return surface.getHeight();
		}

		public int[] getStencilBuffer(int size) {
			return new int[size];
		}

		public int getWidth() {
			return surface.getWidth();
		}

		public int getX() {
			return 0;
		}

		public int getY() {
			return 0;
		}

		public void sync() {
			// Not used because direct drawing
		}


		public void updatePixels(int x, int y, int w, int h, int[] pix, int off, int scan) {
			// Not used because direct drawing
			
		}
		
	}


}
