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
package javax.microedition.m3g;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import org.thenesis.m3g.engine.util.Color;

public final class Graphics3D {

	private static final String PROPERTY_SUPPORT_ANTIALIASING = "supportAntialiasing";
	private static final String PROPERTY_SUPPORT_TRUECOLOR = "supportTrueColor";
	private static final String PROPERTY_SUPPORT_DITHERING = "supportDithering";
	private static final String PROPERTY_SUPPORT_MIPMAPPING = "supportMipmapping";
	private static final String PROPERTY_SUPPORT_PERSPECTIVE_CORRECTION = "supportPerspectiveCorrection";
	private static final String PROPERTY_MAX_LIGHTS = "maxLights";
	private static final String PROPERTY_MAX_VIEWPORT_WIDTH = "maxViewportWidth";
	private static final String PROPERTY_MAX_VIEWPORT_HEIGHT = "maxViewportHeight";
	private static final String PROPERTY_MAX_VIEWPORT_DIMENSION = "maxViewportDimension";
	private static final String PROPERTY_MAX_TEXTURE_DIMENSION = "maxTextureDimension";
	private static final String PROPERTY_MAX_SPRITE_CROP_DIMENSION = "maxSpriteCropDimension";
	private static final String PROPERTY_MAX_TRANSFORM_PER_VERTEX = "maxTransformsPerVertex";
	private static final String PROPERTY_MAX_TEXTURE_UNITS = "numTextureUnits";

	private static Graphics3D instance = null;

	private int maxTextureUnits = 1;
	private int maxTextureSize;

	private int viewportX = 0;
	private int viewportY = 0;
	private int viewportWidth = 0;
	private int viewportHeight = 0;
	private int maxViewportWidth = 0;
	private int maxViewportHeight = 0;

	private EGL10 egl;
	private EGLConfig eglConfig;
	private EGLDisplay eglDisplay;
	private EGLSurface eglWindowSurface;
	private EGLContext eglContext;
	private GL10 gl = null;

	private Object renderTarget;
	private boolean targetBound = false;

	private Camera camera;
	private Transform cameraTransform;

	private Vector lights = new Vector();
	private Vector lightTransforms = new Vector();
	private static boolean[] lightFlags;
	private int maxLights = 1;
	private boolean lightHasChanged = false;

	private CompositingMode defaultCompositioningMode = new CompositingMode();
	private PolygonMode defaultPolygonMode = new PolygonMode();
	private Background defaultBackground = new Background();

	private boolean cameraHasChanged = false;

	private float depthRangeNear = 0;
	private float depthRangeFar = 1;
	private boolean depthRangeHasChanged = false;

	private boolean depthBufferEnabled;
	private int hints;

	private static Hashtable implementationProperties = new Hashtable();

	private Graphics3D() {
		initGLES();
		populateProperties();
	}

	public static Graphics3D getInstance() {
		if (instance == null) {
			instance = new Graphics3D();
		}
		return instance;
	}

	private void initGLES() {

		// Create EGL context
		this.egl = (EGL10) EGLContext.getEGL();
		this.eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		EGL_ASSERT(eglDisplay != EGL10.EGL_NO_DISPLAY);

		int[] major_minor = new int[2];
		EGL_ASSERT(egl.eglInitialize(eglDisplay, major_minor));

		int[] num_config = new int[1];
		EGL_ASSERT(egl.eglGetConfigs(eglDisplay, null, 0, num_config));

		int redSize = 8;
		int greenSize = 8;
		int blueSize = 8;
		int alphaSize = 8;
		int depthSize = EGL10.EGL_DONT_CARE;
		int stencilSize = EGL10.EGL_DONT_CARE;
		int[] s_configAttribs = { EGL10.EGL_RED_SIZE, redSize, EGL10.EGL_GREEN_SIZE, greenSize, EGL10.EGL_BLUE_SIZE,
				blueSize, EGL10.EGL_ALPHA_SIZE, alphaSize, EGL10.EGL_DEPTH_SIZE, depthSize, EGL10.EGL_STENCIL_SIZE,
				stencilSize, EGL10.EGL_NONE };

		EGLConfig[] eglConfigs = new EGLConfig[num_config[0]];
		EGL_ASSERT(egl.eglChooseConfig(eglDisplay, s_configAttribs, eglConfigs, eglConfigs.length, num_config));
		this.eglConfig = eglConfigs[0];

		this.eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, null);
		EGL_ASSERT(eglContext != EGL10.EGL_NO_CONTEXT);
		this.gl = (GL10) eglContext.getGL();

		/*
		 * Retrieve the OpenGL ES implementation specific properties with a faked EGLSurface (a bit hacky)
		 */

		// Create an offscreen surface
		EGLSurface tmpSurface = egl.eglCreatePbufferSurface(eglDisplay, eglConfig, null);
		EGL_ASSERT(tmpSurface != EGL10.EGL_NO_SURFACE);
		EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, tmpSurface, tmpSurface, eglContext));
		// Get parameters from the GL instance
		int[] params = new int[2];
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_UNITS, params, 0);
		maxTextureUnits = params[0];
		gl.glGetIntegerv(GL10.GL_MAX_LIGHTS, params, 0);
		maxLights = params[0];
		lightFlags = new boolean[maxLights];
		gl.glGetIntegerv(GL10.GL_MAX_VIEWPORT_DIMS, params, 0);
		maxViewportWidth = params[0];
		maxViewportHeight = params[1];
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, params, 0);
		maxTextureSize = params[0];
		// Destroy the offscreen surface
		EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT));
		EGL_ASSERT(egl.eglDestroySurface(eglDisplay, tmpSurface));

	}

	private void populateProperties() {
		implementationProperties.put(PROPERTY_SUPPORT_ANTIALIASING, new Boolean(false));
		implementationProperties.put(PROPERTY_SUPPORT_TRUECOLOR, new Boolean(true));
		implementationProperties.put(PROPERTY_SUPPORT_DITHERING, new Boolean(false));
		implementationProperties.put(PROPERTY_SUPPORT_MIPMAPPING, new Boolean(false));
		implementationProperties.put(PROPERTY_SUPPORT_PERSPECTIVE_CORRECTION, new Boolean(false));
		implementationProperties.put(PROPERTY_MAX_LIGHTS, new Integer(maxLights));
		implementationProperties.put(PROPERTY_MAX_VIEWPORT_WIDTH, new Integer(maxViewportWidth));
		implementationProperties.put(PROPERTY_MAX_VIEWPORT_HEIGHT, new Integer(maxViewportHeight));
		implementationProperties.put(PROPERTY_MAX_VIEWPORT_DIMENSION, new Integer(Math.min(maxViewportWidth,
				maxViewportHeight)));
		implementationProperties.put(PROPERTY_MAX_TEXTURE_DIMENSION, new Integer(maxTextureSize));
		implementationProperties.put(PROPERTY_MAX_SPRITE_CROP_DIMENSION, new Integer(maxTextureSize));
		implementationProperties.put(PROPERTY_MAX_TRANSFORM_PER_VERTEX, new Boolean(false));
		implementationProperties.put(PROPERTY_MAX_TEXTURE_UNITS, new Integer(2));
	}

	public void bindTarget(Object target) {
		bindTarget(target, true, 0);
	}

	public void bindTarget(Object target, boolean depthBuffer, int hints) {

		if (target == null) {
			throw new NullPointerException("Rendering target must not be null");
		}

		// A target should not be already bound
		if (targetBound) {
			throw new IllegalStateException("Graphics3D already has a rendering target");
		}
		// Now bind the target
		targetBound = true;

		// Note: We let the EGL implementation to check if the target object is of a suitable type for the underlying platform

		// Depth buffer
		depthBufferEnabled = depthBuffer;
		this.hints = hints;

		// Create a new window surface if the target changes (i.e, for MIDP2, the target Canvas changed)
		if (target != renderTarget) {
			renderTarget = target;
			this.eglWindowSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, target, null);
			EGL_ASSERT(eglWindowSurface != EGL10.EGL_NO_SURFACE);
		}

		// Make the context current on this thread
		EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, eglWindowSurface, eglWindowSurface, eglContext));

		// Wait before any rendering operation
		EGL_ASSERT(egl.eglWaitNative(EGL10.EGL_CORE_NATIVE_ENGINE, target));
		
		int[] w = new int[1];
		EGL_ASSERT(egl.eglQuerySurface(eglDisplay, eglWindowSurface, EGL10.EGL_WIDTH, w));
        int[] h = new int[1];
        EGL_ASSERT(egl.eglQuerySurface(eglDisplay, eglWindowSurface, EGL10.EGL_HEIGHT, h));
        setViewport(0, 0, w[0],  h[0]);

	}
	
	private static void EGL_ASSERT(boolean val) {
	    if (!val) {
	        throw new IllegalStateException();
	    }
	}

	public Object getTarget() {
		return this.renderTarget;
	}

	public void releaseTarget() {
		if (targetBound) {
		    EGL_ASSERT(egl.eglWaitGL());
			// Release the context
			EGL_ASSERT(egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT));
			targetBound = false;
		}
	}

	public void clear(Background background) {

		if (!targetBound) {
			throw new IllegalStateException("Graphics3D does not have a rendering target");
		}

		if (background != null)
			background.setupGL(gl);
		else {
			defaultBackground.setupGL(gl);
		}
	}

	public int addLight(Light light, Transform transform) {
		if (light == null)
			throw new NullPointerException("Light must not be null");

		lights.addElement(light);

		// Use identity transform if the given transform is null
		Transform t = new Transform();
		if (transform != null) {
			t.set(transform);
		}
		lightTransforms.addElement(t);

		int index = lights.size() - 1;

		// limit the number of lights
		if (index < maxLights) {
			lightFlags[index] = true;
		}

		lightHasChanged = true;
		return index;
	}

	public void setLight(int index, Light light, Transform transform) {
		lights.setElementAt(light, index);
		lightTransforms.setElementAt(transform, index);
		if (index < maxLights) {
			lightFlags[index] = true;
		}
		lightHasChanged = true;
	}

	public void resetLights() {
		lights.removeAllElements();
		lightTransforms.removeAllElements();
		for (int i = 0; i < maxLights; i++) {
			lightFlags[i] = false;
		}
		lightHasChanged = true;
	}

	public int getLightCount() {
		return lights.size();
	}

	public Light getLight(int index, Transform transform) {
		if (transform != null) {
			transform.set((Transform) lightTransforms.elementAt(index));
		}
		return (Light) lights.elementAt(index);
	}

	private void applyLights() {

		// Enable or disable lights depending on the light flags
		if (lightHasChanged) {
			for (int i = 0; i < maxLights; i++) {
				if (lightFlags[i]) {
					Light light = (Light) lights.elementAt(i);
					Transform transform = (Transform) lightTransforms.elementAt(i);
					gl.glEnable(GL10.GL_LIGHT0 + i);
					gl.glPushMatrix();
					transform.multGL(gl); // TODO: should this really be setGL? I was thinking multGL...
					light.setupGL(gl, GL10.GL_LIGHT0 + i);
					gl.glPopMatrix();
				} else {
					gl.glDisable(GL10.GL_LIGHT0 + i);
				}
			}
			lightHasChanged = false;
		}

	}

	public int getHints() {
		return hints;
	}

	public boolean isDepthBufferEnabled() {
		return depthBufferEnabled;
	}

	public void setViewport(int x, int y, int width, int height) {

		if ((width <= 0) || (height <= 0) || (width > maxViewportWidth) || (height > maxViewportHeight)) {
			throw new IllegalArgumentException("Viewport coordinates are out of the allowed range");
		}

		this.viewportX = x;
		this.viewportY = y;
		this.viewportWidth = width;
		this.viewportHeight = height;

		gl.glViewport(x, y, width, height);
	}

	public int getViewportX() {
		return this.viewportX;
	}

	public int getViewportY() {
		return this.viewportY;
	}

	public int getViewportWidth() {
		return this.viewportWidth;
	}

	public int getViewportHeight() {
		return this.viewportHeight;
	}

	public void setDepthRange(float near, float far) {

		if ((near < 0) || (near > 1) || (far < 0) || (far > 1)) {
			throw new IllegalArgumentException("Bad depth range");
		}

		if ((depthRangeNear != near) || (depthRangeFar != far)) {
			depthRangeNear = near;
			depthRangeFar = far;
			depthRangeHasChanged = true;
		}
	}

	private void applyDepthRange() {
		if (depthRangeHasChanged) {
			gl.glDepthRangef(depthRangeNear, depthRangeFar);
			depthRangeHasChanged = false;
		}
	}

	/** Returns the near distance of the depth range. */
	public float getDepthRangeNear() {
		return depthRangeNear;
	}

	/** Returns the far distance of the depth range. */
	public float getDepthRangeFar() {
		return depthRangeFar;
	}

	/**
	 * @return the properties
	 */
	public static final Hashtable getProperties() {
		// Force initialization of Graphics3D in order to populate implementationProperties
		if (instance == null) {
			getInstance();
		}
		return implementationProperties;
	}

	public void setCamera(Camera camera, Transform transform) {
		// TODO Check if transform is invertible, otherwise throw an ArithmeticException
		this.camera = camera;
		
		// If the given transform is null, use the identity matrix
		Transform t = new Transform();
		if (transform != null) {
			t.set(transform);
		} 
		this.cameraTransform = t;
		cameraHasChanged = true;
	}

	/**
	 * Apply camera configuration to OpenGL
	 */
	private void applyCamera() {
		if (cameraHasChanged) {
			Transform t = new Transform();

			gl.glMatrixMode(GL10.GL_PROJECTION);
			camera.getProjection(t);
			t.setGL(gl);

			gl.glMatrixMode(GL10.GL_MODELVIEW);
			t.set(cameraTransform);
			t.invert();
			t.setGL(gl);

			cameraHasChanged = false;
		}
	}

	public Camera getCamera(Transform transform) {
		if (transform != null)
			transform.set(this.cameraTransform);
		return camera;
	}

	public void render(Node node, Transform transform) {

		if ((!targetBound) || (camera == null)) {
			throw new IllegalStateException("Graphics3D does not have a rendering target or a current camera");
		}
		
		// If the given transform is null, use the identity matrix
		if (transform == null) {
			transform = new Transform();
		}

		// Apply Graphics3D settings to the OpenGL pipeline
		applyCamera();
		applyDepthRange();
		applyLights();

		if ((node instanceof Mesh) || (node instanceof Sprite3D) || (node instanceof Group)) {
			renderNode(node, transform);
		} else {
			throw new IllegalArgumentException("Node is not a Sprite3D, Mesh, or Group");
		}

	}

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform) {

		if (vertices == null)
			throw new NullPointerException("vertices == null");
		if (triangles == null)
			throw new NullPointerException("triangles == null");
		if (appearance == null)
			throw new NullPointerException("appearance == null");
		if ((!targetBound) || (camera == null)) {
			throw new IllegalStateException("Graphics3D does not have a rendering target or a current camera");
		}
		// TODO Check if vertices or triangles violates the constraints defined in VertexBuffer or IndexBuffer
		
		// If the given transform is null, use the identity matrix
		if (transform == null) {
			transform = new Transform();
		}

		// Apply Graphics3D settings to the OpenGL pipeline
		applyCamera();
		applyDepthRange();
		applyLights();

		// Vertices
		float[] scaleBias = new float[4];
		VertexArray positions = vertices.getPositions(scaleBias);
		FloatBuffer pos = positions.getFloatBuffer();
		pos.position(0);
		gl.glVertexPointer(positions.getComponentCount(), GL10.GL_FLOAT, 0, pos);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// Normals
		VertexArray normals = vertices.getNormals();
		if (normals != null) {
			FloatBuffer norm = normals.getFloatBuffer();
			norm.position(0);
			gl.glEnable(GL10.GL_NORMALIZE);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, norm);
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		} else {
			gl.glDisable(GL10.GL_NORMALIZE);
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}

		// Colors
		VertexArray colors = vertices.getColors();
		if (colors != null) {
			Buffer buffer = colors.getARGBBuffer();
			buffer.position(0);
			// Force number of color components to 4 (i.e. don't use colors.getComponentCount())
			gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, buffer);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		} else {
			// Use default color as we don't have color per vertex
			Color color = new Color(vertices.getDefaultColor());
			float[] colorArray = color.toRBGAArray();
			gl.glColor4f(colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}

		// Textures
		for (int i = 0; i < maxTextureUnits; ++i) {
			float[] texScaleBias = new float[4];
			VertexArray texcoords = vertices.getTexCoords(i, texScaleBias);
			gl.glClientActiveTexture(GL10.GL_TEXTURE0 + i);
			if ((texcoords != null) && (appearance.getTexture(i) != null)) {
				// Enable the texture coordinate array 
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				FloatBuffer tex = texcoords.getFloatBuffer();
				tex.position(0);

				// Activate the texture unit
				gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
				appearance.getTexture(i).setupGL(gl, texScaleBias);

				// Set the texture coordinates
				gl.glTexCoordPointer(texcoords.getComponentCount(), GL10.GL_FLOAT, 0, tex);
			} else {
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
		}

		// Appearance
		setAppearance(appearance);

		// Scene
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		transform.multGL(gl);

		gl.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
		gl.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);

		// Draw
		ShortBuffer indices = triangles.getBuffer();
		indices.position(0);
		if (triangles instanceof TriangleStripArray) {
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, triangles.getIndexCount(), GL10.GL_UNSIGNED_SHORT, indices);
		} else {
			gl.glDrawElements(GL10.GL_TRIANGLES, triangles.getIndexCount(), GL10.GL_UNSIGNED_SHORT, indices);
		}

		gl.glPopMatrix();
	}

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform,
			int scope) {
		// TODO: check scope
		render(vertices, triangles, appearance, transform);
	}

	public void render(World world) {

		if (!targetBound) {
			throw new IllegalStateException("Graphics3D does not have a rendering target");
		}

		clear(world.getBackground());

		Transform t = new Transform();

		// Setup camera
		Camera c = world.getActiveCamera();
		if (c == null)
			throw new IllegalStateException("World has no active camera.");
		if (!c.getTransformTo(world, t))
			throw new IllegalStateException("Camera is not in world.");

		// Camera
		setCamera(c, t);
		applyCamera();

		// Depth range
		applyDepthRange();

		// Setup lights
		resetLights();
		populateLights(world, world);
		applyLights();

		// Begin traversal of scene graph
		renderDescendants(world, world);
	}

	private void populateLights(World world, Object3D obj) {
		int numReferences = obj.getReferences(null);
		if (numReferences > 0) {
			Object3D[] objArray = new Object3D[numReferences];
			obj.getReferences(objArray);
			for (int i = 0; i < numReferences; ++i) {
				if (objArray[i] instanceof Light) {
					Transform t = new Transform();
					Light light = (Light) objArray[i];
					if (light.isRenderingEnabled() && light.getTransformTo(world, t))
						addLight(light, t);
				}
				populateLights(world, objArray[i]);
			}
		}
	}

	private void renderDescendants(Node topNode, Object3D obj) {
		int numReferences = obj.getReferences(null);
		if (numReferences > 0) {
			Object3D[] objArray = new Object3D[numReferences];
			obj.getReferences(objArray);
			for (int i = 0; i < numReferences; ++i) {
				if (objArray[i] instanceof Node) {
					Node subNode = (Node) objArray[i];
					if (subNode instanceof Group) {
						renderDescendants(topNode, subNode);
					} else {
						Transform t = new Transform();
						subNode.getTransformTo(topNode, t);
						renderNode(subNode, t);
					}
				}
			}
		}
	}

	private void renderNode(Node node, Transform transform) {

		if (node instanceof Mesh) {
			Mesh mesh = (Mesh) node;
			int subMeshes = mesh.getSubMeshCount();
			VertexBuffer vertices = mesh.getVertexBuffer();
			for (int i = 0; i < subMeshes; ++i)
				render(vertices, mesh.getIndexBuffer(i), mesh.getAppearance(i), transform);
		} else if (node instanceof Sprite3D) {
			Sprite3D sprite = (Sprite3D) node;
			sprite.render(gl, transform);
		} else if (node instanceof Group) {
			renderDescendants(node, node);
		}

	}

	void setAppearance(Appearance appearance) {
		if (appearance == null)
			throw new NullPointerException("Appearance must not be null");

		// Polygon mode
		PolygonMode polyMode = appearance.getPolygonMode();
		if (polyMode == null) {
			polyMode = defaultPolygonMode;
		}
		polyMode.setupGL(gl);

		// Material
		if (appearance.getMaterial() != null) {
			appearance.getMaterial().setupGL(gl, polyMode.getLightTarget());
		} else {
			gl.glDisable(GL10.GL_LIGHTING);
		}

		// Fog
		if (appearance.getFog() != null) {
			appearance.getFog().setupGL(gl);
		} else {
			gl.glDisable(GL10.GL_FOG);
		}

		// Compositing mode
		if (appearance.getCompositingMode() != null) {
			appearance.getCompositingMode().setupGL(gl, depthBufferEnabled);
		} else {
			defaultCompositioningMode.setupGL(gl, depthBufferEnabled);
		}
	}

	int getTextureUnitCount() {
		return maxTextureUnits;
	}

	int getMaxTextureSize() {
		return maxTextureSize;
	}

	void disableTextureUnits() {
		for (int i = 0; i < maxTextureUnits; i++) {
			gl.glActiveTexture(GL10.GL_TEXTURE0 + i);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
	}

}
