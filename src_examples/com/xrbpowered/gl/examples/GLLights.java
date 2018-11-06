/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2016 Ashur Rafiev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.xrbpowered.gl.examples;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.MultisampleBuffers;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.builder.FastMeshBuilder;
import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.ActorPicker;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLLights extends ExampleClient {

	public static final float PLANE_Y = -4f;
	public static final float PLANE_SIZE = 12f;
	public static final int NUM_OBJECTS = 5;
	public static final int NUM_LIGHTS = 8;
	
	public static final Vector3f LIGHT_ATT = new Vector3f(1f, 0.14f, 0.07f);
	
	public static class GlobeShader extends ActorShader {
		public GlobeShader() {
			super(StandardShader.standardVertexInfo, "std_v.glsl", "blank_f.glsl");
		}
		public void setColor(Vector4f color) {
			GL20.glUseProgram(pId);
			uniform(GL20.glGetUniformLocation(pId, "color"), color);
		}
	}
	
	protected Texture diffuse;
	protected Texture specular;
	protected Texture normal;
	protected GlobeShader globeShader;
	
	protected PostProcessShader postProc;

	protected StaticMesh plane;
	protected StaticMeshActor planeActor;
	protected StaticMesh[] objects;
	protected StaticMeshActor[] objectActors;
	protected StaticMesh[] lightGlobes;
	protected StaticMeshActor[] lightActors;
	protected Vector4f[] lightColors;

	protected int hoverObject = 0;
	protected int activeObject = 0;
	
	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>W</b> / <b>A</b> / <b>S</b> / <b>D</b>|Move around",
				"Hold <b>LMB</b>|Mouse look",
				"<b>E</b>|Select a light under the cursor",
				"Hold <b>RMB</b>|Use camera controls to move the<br>selected light source",
				"<b>F1</b>|Toggle FPS limit and VSync",
				"<b>F2</b>|Toggle wireframe",
				"<b>F4</b>|Toggle post-processing effect",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		postProc = new PostProcessShader("post_toxic_f.glsl");
		
		diffuse = BufferTexture.createPlainColor(4, 4, new Color(0x99bbdd));
		specular = BufferTexture.createPlainColor(4, 4, new Color(0xaaaaaa));
		normal = new Texture("normal.jpg");
		globeShader = new GlobeShader();
		
		plane = FastMeshBuilder.plane(PLANE_SIZE, 1, 4, StandardShader.standardVertexInfo, null);
		planeActor = StaticMeshActor.make(scene, plane, StandardShader.getInstance(), diffuse, specular, normal);
		planeActor.position.y = PLANE_Y;
		planeActor.updateTransform();
		
		Random random = new Random();
		objects = new StaticMesh[NUM_OBJECTS];
		objectActors = new StaticMeshActor[NUM_OBJECTS];
		for(int i=0; i<NUM_OBJECTS; i++) {
			float r = random.nextFloat()+0.5f;
			objects[i] = FastMeshBuilder.sphere(r, 16, StandardShader.standardVertexInfo, null);
			objectActors[i] = StaticMeshActor.make(scene, objects[i], StandardShader.getInstance(), diffuse, specular, normal);
			float d = PLANE_SIZE/2f - r;
			objectActors[i].position.x = random.nextFloat()*d*2f - d;
			objectActors[i].position.y = PLANE_Y + r;
			objectActors[i].position.z = random.nextFloat()*d*2f - d;
			objectActors[i].updateTransform();
		}
		lightGlobes = new StaticMesh[NUM_LIGHTS];
		lightActors = new StaticMeshActor[NUM_LIGHTS];
		lightColors = new Vector4f[NUM_LIGHTS];
		for(int i=0; i<NUM_LIGHTS; i++) {
			lightGlobes[i] = FastMeshBuilder.sphere(0.1f, 8, StandardShader.standardVertexInfo, null);
			lightActors[i] = StaticMeshActor.make(scene, lightGlobes[i], StandardShader.getInstance(), specular, specular, plainNormalTexture);
			lightActors[i].setShader(globeShader);
			lightActors[i].position.x = random.nextFloat()*PLANE_SIZE - PLANE_SIZE/2f;
			lightActors[i].position.y = -1f + random.nextFloat() * 2f;
			lightActors[i].position.z = random.nextFloat()*PLANE_SIZE - PLANE_SIZE/2f;
			lightActors[i].updateTransform();
			Color color = Color.getHSBColor(i / (float) NUM_LIGHTS, 1f, 1f);
			lightColors[i] = new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
		}
		
		StandardShader.environment.ambientColor.set(0, 0, 0);
		StandardShader.environment.lightColor.set(0.2f, 0.2f, 0.2f);
		lightController = new Controller().setActor(lightActors[0]);
		
		GL11.glEnable(GL13.GL_MULTISAMPLE);
	}
		
	private void updateInfo() {
		uiDebugTitle = String.format("Light %d", activeObject+1);
		uiDebugInfo = hoverObject>0 ? (hoverObject-1!=activeObject ? String.format("Press E to select Light %d", hoverObject) : "Selected") : "";
		uiDebugPane.repaint();
	}
	
	@Override
	protected void keyDown(int key) {
		switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_F4:
				if(offscreenBuffers==null) {
					offscreenBuffers = new MultisampleBuffers(getTargetWidth(), getTargetHeight(), settings.multisample);
				}
				else {
					offscreenBuffers.destroy();
					offscreenBuffers = null;
				}
				break;
			case Keyboard.KEY_E:
				if(hoverObject>0 && hoverObject-1!=activeObject) {
					activeObject = hoverObject-1;
					lightController.setActor(lightActors[activeObject]);
					updateInfo();
				}
				break;
			default:
				super.keyDown(key);
		}
	}
	
	@Override
	protected void drawObjects(RenderTarget target, float dt) {
		ActorPicker.instance.startPicking(Mouse.getX(), Mouse.getY(), RenderTarget.primaryBuffer);
		for(int i=0; i<NUM_LIGHTS; i++)
			ActorPicker.instance.drawActor(lightActors[i], i+1);
		int hover = ActorPicker.instance.finishPicking(target);
		if(hover!=hoverObject) {
			hoverObject = hover;
			updateInfo();
		}

		Vector3f[] lightPositions = new Vector3f[NUM_LIGHTS];
		Vector3f[] lightAtt = new Vector3f[NUM_LIGHTS];
		for(int i=0; i<NUM_LIGHTS; i++) {
			lightPositions[i] = lightActors[i].position;
			lightAtt[i] = LIGHT_ATT; 
		}
		StandardShader.environment.setPointLights(NUM_LIGHTS, lightPositions, lightColors, lightAtt);
		
		planeActor.draw();
		for(int i=0; i<NUM_OBJECTS; i++)
			objectActors[i].draw();
		for(int i=0; i<NUM_LIGHTS; i++) {
			globeShader.setColor(lightColors[i]);
			lightActors[i].draw();
		}
	}
	
	@Override
	protected void drawOffscreenBuffers(OffscreenBuffers buffers, RenderTarget target, float dt) {
		postProc.draw(buffers, dt);
	}
	
	@Override
	protected void resizeResources() {
		super.resizeResources();
		if(offscreenBuffers!=null) {
			offscreenBuffers.destroy();
			offscreenBuffers = new MultisampleBuffers(getTargetWidth(), getTargetHeight(), settings.multisample);
		}
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		postProc.destroy();
		plane.destroy();
		for(int i=0; i<NUM_OBJECTS; i++)
			objects[i].destroy();
		for(int i=0; i<NUM_LIGHTS; i++)
			lightGlobes[i].destroy();
		diffuse.destroy();
		specular.destroy();
		normal.destroy();
		globeShader.destroy();
	}
	
	public static void main(String[] args) {
		new GLLights().init("GLLights").run();
	}

}
