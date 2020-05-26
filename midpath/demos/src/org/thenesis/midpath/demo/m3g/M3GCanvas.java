/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
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
 *  * Neither the name of MIDPath nor the names of its contributors
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
package org.thenesis.midpath.demo.m3g;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Fog;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.World;


public class M3GCanvas extends Canvas {
	private World world = null;
	private Graphics3D g3d = null;

	private VertexBuffer vertexBuffer = null;
	private TriangleStripArray indexBuffer = null;
	private Camera camera = null;
	private Light light1 = null;
	private Light light2 = null;

	private Appearance appearance = null;
	private Mesh mesh = null;

	private float a = 0.0f;

	public M3GCanvas() {
		// set up this Displayable to listen to command events 
		setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				if (c.getCommandType() == Command.EXIT) {
					// exit the MIDlet
					M3GDemo.quitApp();
				}
			}
		});
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setTitle("TestMidlet");
	}

	private void init() throws Exception {
		
		// add the Exit command 
		addCommand(new Command("Exit", Command.EXIT, 1));

		// get the singleton Graphics3D instance 
		g3d = Graphics3D.getInstance();
		
		// Load world
//		Object3D[] objects = Loader.load("/org/thenesis/midpath/demo/m3g/data/scene.m3g");
//		for (int i = 0; i < objects.length; ++i) {
//			if (objects[i] instanceof World) {
//				world = (World) objects[i];
//			}
//		}
		initScene();
		
	}

	protected void paint(Graphics g) {
		
		//System.out.println("M3GCanvas.paint(): start");

		g3d.bindTarget(g);
		g3d.setViewport(0, 0, getWidth(), getHeight());
		
		drawScene();

		g3d.releaseTarget();
		
		//System.out.println("M3GCanvas.paint(): end");
	}

	private void drawScene() {
		/**/

		if (light1 != null) {
			a += 4f;
			light1.setTranslation(0, (float) Math.sin(a * 0.01f) * 20f, 25);
			light2.setTranslation((float) Math.sin(a * 0.032f) * 20f, 0, (float) Math.cos(a * 0.032f) * 20f);
			mesh.setOrientation(a, 0, 1, 0);
			mesh.setTranslation(0, 0, -(float) Math.abs(Math.cos(a * 0.0073) * 30f));
		}

		g3d.render(world);

		/*/
		 
		 g3d.clear(world.getBackground());
		 
		 Transform transform = new Transform();
		 
		 camera.getCompositeTransform(transform);
		 g3d.setCamera(camera, transform);
		 
		 g3d.resetLights();
		 light1.setTranslation(0, (float)Math.sin(a*0.01f) * 20f, 25);
		 light1.getCompositeTransform(transform);
		 g3d.addLight(light1, transform);
		 
		 // Render something to indicate light1
		 transform.postScale(0.1f, 0.1f, 0.1f);
		 g3d.render(vertexBuffer, indexBuffer, appearance, transform);
		 
		 light2.setTranslation((float)Math.sin(a*0.032f) * 20f, 0, (float)Math.cos(a*0.032f) * 20f);
		 light2.getCompositeTransform(transform);
		 g3d.addLight(light2, transform);
		 
		 // Render something to indicate light2
		 transform.postScale(0.1f, 0.1f, 0.1f);
		 g3d.render(vertexBuffer, indexBuffer, appearance, transform);

		 // Render our cube
		 transform.setIdentity();
		 a += 4f;
		 transform.postTranslate(0, (float)Math.sin(a*0.023)*8f, 0);
		 transform.postTranslate(0, 0, -(float)Math.abs(Math.sin(a*0.0053)*70f));
		 transform.postRotate(a, 0, 1, 0);
		 g3d.render(vertexBuffer, indexBuffer, appearance, transform);
		 
		 /**/
	}

	private void initScene() {
		
		//System.out.println("initScene() start");

		// Create new world
		world = new World();

		// Setup camera
		camera = new Camera();
		camera.setTranslation(0, 0, 40);
		camera.setPerspective(45.0f, (float) getWidth() / (float) getHeight(), 1.0f, 500.0f);
		world.addChild(camera);
		world.setActiveCamera(camera);

		// Setup light
		light1 = new Light();
		light1.setMode(Light.OMNI);
		light1.setColor(0x00FF8888); // red-ish
		light1.setIntensity(1.25f); // overbright
		world.addChild(light1);

		// Setup light
		light2 = new Light();
		light2.setMode(Light.OMNI);
		light2.setColor(0x0088FF88); // green-ish
		light2.setIntensity(1.25f); // overbright
		world.addChild(light2);

		// init some arrays for our object (cube)

		// Each line in this array declaration represents a triangle strip for
		// one side of a cube. The only primitive we can draw with is the
		// triangle strip so if we want to make a cube with hard edges we
		// need to construct one triangle strip per face of the cube.
		// 1 * * * * * 0
		//   * *     *
		//   *   *   *
		//   *     * *
		// 3 * * * * * 2
		// The ascii diagram above represents the vertices in the first line
		// (the first tri-strip)
		short[] vert = { 10, 10, 10, -10, 10, 10, 10, -10, 10, -10, -10, 10, // front
				-10, 10, -10, 10, 10, -10, -10, -10, -10, 10, -10, -10, // back
				-10, 10, 10, -10, 10, -10, -10, -10, 10, -10, -10, -10, // left
				10, 10, -10, 10, 10, 10, 10, -10, -10, 10, -10, 10, // right
				10, 10, -10, -10, 10, -10, 10, 10, 10, -10, 10, 10, // top
				10, -10, 10, -10, -10, 10, 10, -10, -10, -10, -10, -10 }; // bottom

		// create a VertexArray to hold the vertices for the object
		VertexArray vertArray = new VertexArray(vert.length / 3, 3, 2);
		vertArray.set(0, vert.length / 3, vert);

		// The per-vertex normals for the cube; these match with the vertices
		// above. Each normal is perpendicular to the surface of the object at
		// the corresponding vertex.
		byte[] norm = { 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, -127, 0, 0, -127, 0, 0, -127, 0, 0, -127,
				-127, 0, 0, -127, 0, 0, -127, 0, 0, -127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 0, 127, 0,
				0, 127, 0, 0, 127, 0, 0, 127, 0, 0, -127, 0, 0, -127, 0, 0, -127, 0, 0, -127, 0 };

		// create a vertex array for the normals of the object
		VertexArray normArray = new VertexArray(norm.length / 3, 3, 1);
		normArray.set(0, norm.length / 3, norm);

		// per vertex texture coordinates
		short[] tex = { 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1,
				1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1 };

		// create a vertex array for the texture coordinates of the object
		VertexArray texArray = new VertexArray(tex.length / 2, 2, 2);
		texArray.set(0, tex.length / 2, tex);

		// the length of each triangle strip        
		int[] stripLen = { 4, 4, 4, 4, 4, 4 };

		// create the VertexBuffer for our object
		vertexBuffer = new VertexBuffer();
		vertexBuffer.setPositions(vertArray, 1.0f, null); // unit scale, zero bias
		vertexBuffer.setNormals(normArray);
		vertexBuffer.setTexCoords(0, texArray, 1.0f, null); // unit scale, zero bias
		vertexBuffer.setTexCoords(1, texArray, 1.0f, null); // unit scale, zero bias

		// create the index buffer for our object (this tells how to
		// create triangle strips from the contents of the vertex buffer).
		indexBuffer = new TriangleStripArray(0, stripLen);

		// create the Image2D (we need this so we can make a Texture2D)
		Image midpImage = null;
		try {
			//System.out.println("initScene() : loading image 1");
			midpImage = Image.createImage("data/texture.png");
			//System.out.println("initScene() : image 1 loaded");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Image2D image2D = new Image2D(Image2D.RGB, midpImage);
		
		//System.out.println("initScene() : after image2D creation");

		// Setup background (blue)
		Background bg = new Background();
		bg.setImage(image2D);
		bg.setColor(0x00111166);
		world.setBackground(bg);

		// create the Texture2D and enable mipmapping
		// texture color is to be modulated with the lit material color
		Texture2D texture = new Texture2D(image2D);
		texture.setFiltering(Texture2D.FILTER_LINEAR, Texture2D.FILTER_LINEAR);
		texture.setWrapping(Texture2D.WRAP_CLAMP, Texture2D.WRAP_CLAMP);
		texture.setBlending(Texture2D.FUNC_MODULATE);

		// create the Image2D (we need this so we can make a Texture2D)
		try {
			//System.out.println("initScene() : loading image 2");
			midpImage = Image.createImage("data/dirt.png");
			//System.out.println("initScene() : image 2 loaded");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Image2D image = new Image2D(Image2D.RGB, midpImage);

		// create the Texture2D and enable mipmapping
		// texture color is to be modulated with the lit material color
		Texture2D texture2 = new Texture2D(image);
		texture2.setFiltering(Texture2D.FILTER_LINEAR, Texture2D.FILTER_LINEAR);
		texture2.setWrapping(Texture2D.WRAP_CLAMP, Texture2D.WRAP_CLAMP);
		texture2.setBlending(Texture2D.FUNC_MODULATE);

		// create the material
		Material material = new Material();
		material.setColor(Material.DIFFUSE, 0xFFFFFFFF); // white
		material.setColor(Material.SPECULAR, 0xFFFFFFFF); // white
		material.setShininess(100.0f);

		Fog fog = new Fog();
		fog.setColor(0x00111166);
		fog.setLinear(0, 150);

		CompositingMode compMode = new CompositingMode();
//		compMode.setColorWriteEnabled(false);
//		compMode.setDepthTestEnabled(false);
//		compMode.setBlending(CompositingMode.MODULATE);
//		compMode.setDepthOffsetFactor(-10.0f);

		PolygonMode polyMode = new PolygonMode();
//		polyMode.setCulling(PolygonMode.CULL_FRONT);
//		polyMode.setWinding(PolygonMode.WINDING_CW);

		// create the appearance
		appearance = new Appearance();
		appearance.setTexture(0, texture);
		appearance.setTexture(1, texture2);
		appearance.setMaterial(material);
		appearance.setFog(fog);
		appearance.setCompositingMode(compMode);
		appearance.setPolygonMode(polyMode);

		mesh = new Mesh(vertexBuffer, indexBuffer, appearance);
		world.addChild(mesh);
		
		//System.out.println("initScene() end");
	}

}
