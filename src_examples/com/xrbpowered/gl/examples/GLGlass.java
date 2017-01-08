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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.builder.FastMeshBuilder;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLGlass extends ExampleClient {

	public static final int NUM_OBJECTS = 10;
	public static final int NUM_PLANES = 10;
	public static final float OBJECT_RANGE = 5f;
	
	protected Texture diffuse;
	protected Texture alpha1;
	protected Texture alpha2;
	protected Texture normal;
	protected Texture blurMask;

	protected StaticMeshActor[] objectActors;
	protected StaticMesh plane;
	protected StaticMeshActor[] planeActors1;
	protected StaticMeshActor[] planeActors2;
	
	protected OffscreenBuffers interBuffers;
	protected OffscreenBuffers blurBuffers;
	protected PostProcessShader postProc;
	
	protected Comparator<StaticMeshActor> depthSorter = new Comparator<StaticMeshActor>() {
		@Override
		public int compare(StaticMeshActor o1, StaticMeshActor o2) {
			return -Float.compare(o1.depth, o2.depth);
		}
	};
	
	private class GlassShader extends StandardShader {
		public GlassShader() {
			super("std_v.glsl", "std_glass_f.glsl");
			StandardShader.environment.addShader(this);
			specPower = 150f;
		}
		@Override
		protected void storeUniformLocations() {
			super.storeUniformLocations();
			initSamplers(new String[] {"texDiffuse", "texSpecular", "texNormal", "texBuffer", "texBlurMask", "texBlur"});
			GL20.glUseProgram(pId);
			GL20.glUniform1f(GL20.glGetUniformLocation(pId, "refraction"), 0.01f);
			GL20.glUseProgram(0);
		}
		public void useBuffer(boolean use) {
			GL20.glUseProgram(pId);
			GL20.glUniform1i(GL20.glGetUniformLocation(pId, "useBuffer"), use ? 1 : 0);
			GL20.glUseProgram(0);
		}
		@Override
		public void destroy() {
			super.destroy();
			StandardShader.environment.removeShader(this);
		}
	}
	private GlassShader glassShader;
	
	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>W</b> / <b>A</b> / <b>S</b> / <b>D</b>|Move around",
				"<b>SPACE</b> / <b>LSHIFT</b>|Fly up/down",
				"Hold <b>LMB</b>|Mouse look",
				"Drag <b>RMB</b>|Move light source",
				"<b>F1</b>|Toggle FPS limit and VSync",
				"<b>F2</b>|Toggle wireframe",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		glassShader = new GlassShader();
		
		diffuse = new Texture("checker.png");
		alpha1 = BufferTexture.createPlainColor(4, 4, new Color(0x22777777, true));
		alpha2 = BufferTexture.createPlainColor(4, 4, new Color(0x00ffffff, true));
		normal = new Texture("glass_n.png");
		blurMask = new Texture("glass_blur.png");
		plane = FastMeshBuilder.plane(4f, 1, 1, StandardShader.standardVertexInfo, null);
		
		Random random = new Random();
		objectActors = new StaticMeshActor[NUM_OBJECTS];
		for(int i=0; i<NUM_OBJECTS; i++) {
			objectActors[i] = StaticMeshActor.make(scene, plane, StandardShader.getInstance(), diffuse, noSpecularTexture, plainNormalTexture);
			objectActors[i].position.x = random.nextFloat()*OBJECT_RANGE*2f - OBJECT_RANGE;
			objectActors[i].position.y = random.nextFloat()*OBJECT_RANGE*2f - OBJECT_RANGE;
			objectActors[i].position.z = random.nextFloat()*OBJECT_RANGE*2f - OBJECT_RANGE;
			objectActors[i].rotation.x = (float)Math.PI/2f;
			objectActors[i].updateTransform();
		}
		planeActors1 = new StaticMeshActor[NUM_PLANES];
		planeActors2 = new StaticMeshActor[NUM_PLANES];
		for(int i=0; i<NUM_PLANES; i++) {
			planeActors1[i] = StaticMeshActor.make(scene, plane, StandardShader.getInstance(), alpha1, plainSpecularTexture, normal);
			planeActors1[i].setShader(glassShader);
			planeActors2[i] = StaticMeshActor.make(scene, plane, StandardShader.getInstance(), alpha2, noSpecularTexture, normal);
			planeActors2[i].setShader(glassShader);
			planeActors1[i].position.x = random.nextFloat()*OBJECT_RANGE*2f - OBJECT_RANGE;
			planeActors1[i].position.y = random.nextFloat()*OBJECT_RANGE*2f - OBJECT_RANGE;
			planeActors1[i].position.z = random.nextFloat()*OBJECT_RANGE*2f - OBJECT_RANGE;
			planeActors1[i].rotation.x = (float)Math.PI/2f;
			planeActors2[i].position = planeActors1[i].position;
			planeActors2[i].rotation = planeActors1[i].rotation;
			planeActors1[i].updateTransform();
			planeActors2[i].updateTransform();
		}
		
		interBuffers = new OffscreenBuffers(Display.getWidth(), Display.getHeight(), false);
		blurBuffers = new OffscreenBuffers(Display.getWidth()/6, Display.getHeight()/6, false);
		postProc = new PostProcessShader("post_blur_f.glsl") {
			@Override
			protected void storeUniformLocations() {
				super.storeUniformLocations();
				GL20.glUseProgram(pId);
				GL20.glUniform1i(GL20.glGetUniformLocation(pId, "numSamples"), 5);
				GL20.glUniform1f(GL20.glGetUniformLocation(pId, "range"), 15f);
				GL20.glUseProgram(0);
			}
		};
		
		lightActor.rotation = new Vector3f((float)Math.PI, 0, 0);
		lightActor.updateTransform();
	}
	
	@Override
	protected void resizeResources() {
		super.resizeResources();
		interBuffers.destroy();
		interBuffers = new OffscreenBuffers(Display.getWidth(), Display.getHeight(), false);
		blurBuffers.destroy();
		blurBuffers = new OffscreenBuffers(Display.getWidth()/6, Display.getHeight()/6, false);
	}
	
	@Override
	protected void drawObjects(RenderTarget target, float dt) {
		for(int i=0; i<NUM_PLANES; i++)
			planeActors1[i].calcDepth();
		Arrays.sort(planeActors1, depthSorter);
		
		Texture.unbind(3);
		Texture.unbind(5);
		for(int i=0; i<NUM_OBJECTS; i++)
			objectActors[i].draw();
		blurMask.bind(4);
		glassShader.useBuffer(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		for(int i=0; i<NUM_PLANES; i++)
			planeActors1[i].draw();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);

		OffscreenBuffers.blit(target, interBuffers, false);
		
		blurBuffers.use();
		postProc.draw(interBuffers, dt);
		
		target.use();
		glassShader.useBuffer(true);
		interBuffers.bindColorBuffer(3);
		blurBuffers.bindColorBuffer(5);
		for(int i=0; i<NUM_PLANES; i++) {
			planeActors2[i].draw();
		}
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		interBuffers.destroy();
		blurBuffers.destroy();
		postProc.destroy();
		plane.destroy();
		diffuse.destroy();
		alpha1.destroy();
		alpha2.destroy();
		normal.destroy();
		glassShader.destroy();
	}
	
	public static void main(String[] args) {
//		settings.multisample = 0;
		new GLGlass().init("GLGlass").run();
	}

}
