package jgl.test;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.MemoryImageSource;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import jgl.GL;
import jgl.GLBackend;
import jgl.context.GLContext;

public class CubePointerTest extends Applet implements ComponentListener {

	private GL gl;

	private static final byte[] s_cubeVertices = { -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10, 10, -10, 10, -10,
			10, -10, -10, 10, 10, -10, -10, -10, -10, -10, -10, 10, 10, -10, -10, 10, -10, 10, -10, -10, -10, -10, 10,
			10, 10, 10, -10, 10, 10, 10, -10, 10, -10, 10, -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10, -10, -10,
			10, -10, 10, -10, -10, 10, 10, -10, -10, -10 };
	private static final byte[] s_cubeColors = { 
		(byte) 40, (byte) 80, (byte) 160, (byte) 255, 
		(byte) 40, (byte) 80,(byte) 160, (byte) 255, 
		(byte) 40, (byte) 80, (byte) 160, (byte) 255, 
		(byte) 40, (byte) 80, (byte) 160,(byte) 255, 
		(byte) 40, (byte) 80, (byte) 160, (byte) 255, 
		(byte) 40, (byte) 80, (byte) 160, (byte) 255,
		(byte) 40, (byte) 80, (byte) 160, (byte) 255, 
		(byte) 40, (byte) 80, (byte) 160, (byte) 255, 
		(byte) 128, (byte) 128, (byte) 128, (byte) 255, 
		(byte) 128, (byte) 128, (byte) 128, (byte) 255, 
		(byte) 128, (byte) 128,(byte) 128, (byte) 255, 
		(byte) 128, (byte) 128, (byte) 128, (byte) 255, 
		(byte) 128, (byte) 128, (byte) 128, (byte) 255, 
		(byte) 128, (byte) 128, (byte) 128, (byte) 255, 
		(byte) 128, (byte) 128, (byte) 128, (byte) 255,
		(byte) 128, (byte) 128, (byte) 128, (byte) 255, 
		(byte) 255, (byte) 110, (byte) 10, (byte) 255, 
		(byte) 255, (byte) 110, (byte) 10, (byte) 255, 
		(byte) 255, (byte) 110, (byte) 10, (byte) 255, 
		(byte) 255, (byte) 110, (byte) 10, (byte) 255, 
		(byte) 255, (byte) 70, (byte) 60, (byte) 255, 
		(byte) 255, (byte) 70, (byte) 60, (byte) 255, 
		(byte) 255, (byte) 70, (byte) 60, (byte) 255, 
		(byte) 255, (byte) 70, (byte) 60, (byte) 255 };

	private static final byte[] s_cubeIndices = { 0, 3, 1, 2, 0, 1, /* front  */
	6, 5, 4, 5, 7, 4, /* back   */
	8, 11, 9, 10, 8, 9, /* top    */
	15, 12, 13, 12, 14, 13, /* bottom */
	16, 19, 17, 18, 16, 17, /* right  */
	23, 20, 21, 20, 22, 21 /* left   */
	};
	private static final byte[] s_cubeNormals = { 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, -128, 0, 0, -128,
			0, 0, -128, 0, 0, -128, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0,
			127, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0 };

	float time = 0.0f;
	ByteBuffer cubeVertices;
	ByteBuffer cubeColors;
	ByteBuffer cubeNormals;
	ByteBuffer cubeIndices;

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {

	}
	
	public void glDrawElements(int mode, int count, int type, Pointer colorPointer, Pointer normalPointer, Pointer vertexPointer, Buffer indices) {

		
		gl.glBegin(mode);

		for (int i = 0; i < count; i++) {

			// Get the current indice
			int indice = 0;
			if (type == GL.GL_UNSIGNED_BYTE) {
				indice = ((ByteBuffer) indices).get(i);
			} else if (type == GL.GL_UNSIGNED_SHORT) {
				indice = ((ShortBuffer) indices).get(i);
			}
			
			// Color
			if (colorPointer.type == GL.GL_UNSIGNED_BYTE) {
				byte c0 = 0, c1 = 0, c2 = 0, c3 = 0;
				ByteBuffer vBuffer = (ByteBuffer) colorPointer.pointer;
				int offset = indice * (colorPointer.size + colorPointer.stride);
				vBuffer.position(offset);
				c0 = vBuffer.get() ;
				c1 = vBuffer.get();
				c2 = vBuffer.get();
				c3 = vBuffer.get();
				gl.glColor4b(c0, c1, c2, c3);
				System.out.println("C: " + c0 + " " + c1 + " " + c2 + " " + c3 + " ");
			} 
			

			// Vertex
			float v0 = 0, v1 = 0, v2 = 0;
			if (vertexPointer.size == 3) {
				if (vertexPointer.type == GL.GL_BYTE) {
					ByteBuffer vBuffer = (ByteBuffer) vertexPointer.pointer;
					int offset = indice * (vertexPointer.size + vertexPointer.stride);
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
				} 
				gl.glVertex3f(v0, v1, v2);
			}
			System.out.println("V: " + v0 + " " + v1 + " " + v2);
			
			//	Normal
			if (normalPointer.type == GL.GL_BYTE) {
				byte n0 = 0, n1 = 0, n2 = 0;
				ByteBuffer vBuffer = (ByteBuffer) normalPointer.pointer;
				int offset = indice * (normalPointer.size + normalPointer.stride);
				vBuffer.position(offset);
				n0 = vBuffer.get();
				n1 = vBuffer.get();
				n2 = vBuffer.get();
				gl.glNormal3b(n0, n1, n2);
				System.out.println("N: " + n0 + " " + n1 + " " + n2);
			} 
				
			
			
			

		}

		gl.glEnd();

	}

	private void drawScene() {

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		time += 0.1f * 40;

		gl.glTranslatef(0.f, 0.f, -30.f);
		gl.glRotatef((float) (time * 29.77f), 1.0f, 2.0f, 0.0f);
		gl.glRotatef((float) (time * 22.311f), -0.1f, 0.0f, -5.0f);

		Pointer vertexPointer = new Pointer();
		vertexPointer.set(3, GL.GL_BYTE, 0, cubeVertices);
		Pointer colorPointer = new Pointer();
		colorPointer.set(4, GL.GL_UNSIGNED_BYTE, 0, cubeColors);
		Pointer normalPointer = new Pointer();
		normalPointer.set(3, GL.GL_BYTE, 0, cubeNormals);
		
//		gl.glVertexPointer(3, GL10.GL_BYTE, 0, cubeVertices);
//		gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, cubeColors);
//		gl.glNormalPointer(GL10.GL_BYTE, 0, cubeNormals);

		glDrawElements(GL.GL_TRIANGLES, 6 * 6, GL.GL_UNSIGNED_BYTE, colorPointer, normalPointer, vertexPointer, cubeIndices);
		gl.glFlush();


	}

	private void perspective(float fovy, float aspect, float zNear, float zFar) {
		float xmin;
		float xmax;
		float ymin;
		float ymax;

		ymax = zNear * (float) Math.tan((fovy * Math.PI) / 360.0);
		ymin = -ymax;
		xmin = ymin * aspect;
		xmax = ymax * aspect;

		gl.glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	private void updateState(int width, int height) {
		float[] light_position = { -50.f, 50.f, 50.f, 0.f };
		float[] light_ambient = { 0.125f, 0.125f, 0.125f, 1.f };
		float[] light_diffuse = { 1.0f, 1.0f, 1.0f, 1.f };
		float[] material_spec = { 1.0f, 1.0f, 1.0f, 0.f };
		float[] zero_vec4 = { 0.0f, 0.0f, 0.0f, 0.f };

		float aspect = (height != 0) ? ((float) width / (float) height) : 1.0f;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, zero_vec4);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, material_spec);

		gl.glEnable(GL.GL_NORMALIZE);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glEnable(GL.GL_CULL_FACE);

		//gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);

		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glDisable(GL.GL_DITHER);

		// Clear background to blue
		gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

		//		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		//		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		//		gl.glEnableClientState(GL.GL_COLOR_ARRAY);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();

		perspective(90.f, aspect, 0.1f, 100.f);

	}

	public void update(Graphics g) {
		// skip the clear screen command....
		paint(g);
	}

	public void paint(Graphics g) {
		gl.glXSwapBuffers();
	}

	public void init() {
		
//		 Initialize data Buffers
		this.cubeVertices = ByteBuffer.allocateDirect(s_cubeVertices.length);
		cubeVertices.put(s_cubeVertices);
		cubeVertices.rewind();

		this.cubeColors = ByteBuffer.allocateDirect(s_cubeColors.length);
		cubeColors.put(s_cubeColors);
		cubeColors.rewind();

		this.cubeNormals = ByteBuffer.allocateDirect(s_cubeNormals.length);
		cubeNormals.put(s_cubeNormals);
		cubeNormals.rewind();

		this.cubeIndices = ByteBuffer.allocateDirect(s_cubeIndices.length);
		cubeIndices.put(s_cubeIndices);
		cubeIndices.rewind();

		int w = 500;
		int h = 500;
		this.setSize(w, h);

		SEBackend backend = new SEBackend(this, 0, 0);
		GLContext context = new GLContext();
		gl = new GL(context);

		// Attach a GL backend to a GL rendering context
		gl.glXMakeCurrent(context, backend);

		updateState(backend.getWidth(), backend.getHeight());

		// Draw scene
		drawScene();
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
			offscreenImage = c.createImage(new MemoryImageSource(w, h, pix, off, scan));
		}

	}
	
	class Pointer {

		int size;
		int type;
		int stride;
		Buffer pointer;

		public void set(int size, int type, int stride, Buffer pointer) {
			this.size = size;
			this.type = type;
			this.stride = stride;
			this.pointer = pointer;
		}
	}

}
