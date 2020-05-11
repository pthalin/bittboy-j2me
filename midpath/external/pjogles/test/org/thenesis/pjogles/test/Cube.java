package org.thenesis.pjogles.test;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.thenesis.pjogles.GLBackend;
import org.thenesis.pjogles.GL11;
import org.thenesis.pjogles.GL11Software;
import org.thenesis.pjogles.GLSoftwareContext;

public class Cube {

	private static final float[] s_cubeVertices = { -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10, 10,

	-10, 10, -10, 10, -10, -10, 10, 10, -10, -10, -10, -10,

	-10, -10, 10, 10, -10, -10, 10, -10, 10, -10, -10, -10,

	-10, 10, 10, 10, 10, -10, 10, 10, 10, -10, 10, -10,

	10, -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10,

	-10, -10, 10, -10, 10, -10, -10, 10, 10, -10, -10, -10 };
	private static final float[] s_cubeColors = { (byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80,
			(byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160,
			(byte) 255,

			(byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40,
			(byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160, (byte) 255,

			(byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128,
			(byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255,

			(byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128,
			(byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255,

			(byte) 255, (byte) 110, (byte) 10, (byte) 255, (byte) 255, (byte) 110, (byte) 10, (byte) 255, (byte) 255,
			(byte) 110, (byte) 10, (byte) 255, (byte) 255, (byte) 110, (byte) 10, (byte) 255,

			(byte) 255, (byte) 70, (byte) 60, (byte) 255, (byte) 255, (byte) 70, (byte) 60, (byte) 255, (byte) 255,
			(byte) 70, (byte) 60, (byte) 255, (byte) 255, (byte) 70, (byte) 60, (byte) 255 };
	private static final short[] s_cubeIndices = { 0, 3, 1, 2, 0, 1, /* front  */
	6, 5, 4, 5, 7, 4, /* back   */
	8, 11, 9, 10, 8, 9, /* top    */
	15, 12, 13, 12, 14, 13, /* bottom */
	16, 19, 17, 18, 16, 17, /* right  */
	23, 20, 21, 20, 22, 21 /* left   */
	};
	private static final float[] s_cubeNormals = { 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127,

	0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128,

	0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0,

	0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0,

	127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0,

	-128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0 };

	BufferedImage image;
	AWTBackend backend;
	GLSoftwareContext context;
	GL11Software gl;
	int width;
	int height;
	Frame frame;
	float time = 0f;

	public void init() {

		backend = new AWTBackend();
		context = new GLSoftwareContext(null, null);
		gl = new GL11Software(context);

		frame = new Frame() {

			public void update(Graphics g) {
				paint(g);
			}

			public void paint(Graphics g) {

				if (image == null) {
					width = getWidth();
					height = getHeight();
					image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					gl.glXMakeCurrent(context, backend);
				}

				drawScene();

				gl.glXSwapBuffers();
				g.drawImage(image, 0, 0, null);

			}

		};

		frame.setSize(200, 200);
		frame.setVisible(true);

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
		gl.glScissor(0, 0, width, height);

		gl.glMatrixMode(GL11.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, light_position);
		gl.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, light_ambient);
		gl.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, light_diffuse);
		gl.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, zero_vec4);
		gl.glMaterialfv(GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR, material_spec);

		gl.glEnable(GL11.GL_NORMALIZE);
		gl.glEnable(GL11.GL_LIGHTING);
		gl.glEnable(GL11.GL_LIGHT0);
		gl.glEnable(GL11.GL_COLOR_MATERIAL);
		gl.glEnable(GL11.GL_CULL_FACE);

		gl.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);

		gl.glShadeModel(GL11.GL_SMOOTH);
		gl.glDisable(GL11.GL_DITHER);

		// Clear background to blue
		gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

		gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL11.GL_COLOR_ARRAY);

		gl.glMatrixMode(GL11.GL_PROJECTION);
		gl.glLoadIdentity();

		perspective(90.f, aspect, 0.1f, 100.f);

		gl.glFinish();
	}

	private void drawScene() {

		updateState(width, height);
		gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		gl.glFinish();

		gl.glMatrixMode(GL11.GL_MODELVIEW);
		gl.glLoadIdentity();

		time += 0.1f;
		gl.glTranslatef(0.f, 0.f, -30.f);
		gl.glRotatef((float) (time * 29.77f), 1.0f, 2.0f, 0.0f);
		gl.glRotatef((float) (time * 22.311f), -0.1f, 0.0f, -5.0f);

		float[] v = { -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10, 10,

		-10, 10, -10, 10, -10, -10 };

		gl.glEnable(GL11.GL_DEPTH_TEST);
		gl.glDepthFunc(GL11.GL_LESS);

		gl.glVertexPointer(3, GL11.GL_FLOAT, 0, s_cubeVertices); // 
		gl.glColorPointer(4, GL11.GL_FLOAT, 0, s_cubeColors);
		gl.glNormalPointer(GL11.GL_FLOAT, 0, s_cubeNormals);

		gl.glDrawElements(GL11.GL_TRIANGLES, s_cubeIndices.length, GL11.GL_SHORT, s_cubeIndices);

		//		gl.glBegin(GL11.GL_TRIANGLES);
		//		for (int vi = 0; vi < s_cubeIndices.length; vi++) {		
		//			int colorIndex = s_cubeIndices[vi] * 4;
		//			int vertexIndex = s_cubeIndices[vi] * 3;
		//			gl.glColor4f(s_cubeColors[colorIndex], s_cubeColors[colorIndex + 1], s_cubeColors[colorIndex + 2], s_cubeColors[colorIndex + 3]);
		//			gl.glVertex3f(s_cubeVertices[vertexIndex], s_cubeVertices[vertexIndex + 1], s_cubeVertices[vertexIndex + 2]);
		//			gl.glNormal3f(s_cubeNormals[vertexIndex], s_cubeNormals[vertexIndex + 1], s_cubeNormals[vertexIndex + 2]);
		//		}
		//		gl.glEnd();

		gl.glFinish();

	}

	public void start() {
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
					frame.repaint();
				}
			}
		};

		new Thread(runnable).start();
	}

	class AWTBackend implements GLBackend {

		public void updatePixels(int x, int y, int width, int height, int[] pixels, int offset, int scanline) {
			image.setRGB(x, y, width, height, pixels, offset, scanline);
		}

		public int getHeight() {
			return image.getHeight();
		}

		public int getWidth() {
			return image.getWidth();
		}

		public int getX() {
			return 0;
		}

		public int getY() {
			return 0;
		}

		public int[] getColorBuffer(int size) {
			return new int[size];
		}

	}

	public static void main(final String[] args) {
		Cube c = new Cube();
		c.init();
		c.start();
	}

}
