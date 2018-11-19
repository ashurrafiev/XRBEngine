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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.builder.FastMeshBuilder;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.Projection;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLBasicTerrain extends ExampleClient {

	private static final int CHUNK_SIZE = 64;

	private StaticMesh[][] terrain = new StaticMesh[3][3];
	private StaticMesh water;
	private Texture diffuse;
	private Texture specular;
	private Texture normal;

	private StaticMeshActor[][] terrainActor = new StaticMeshActor[3][3];
	private StaticMeshActor[][] waterActor = new StaticMeshActor[3][3];
	
	private StaticMesh createTerrainMesh(long t, int x, int y) {

		HeightMap m0 = new HeightMap(CHUNK_SIZE, t);
		m0.setBaseXY(x*CHUNK_SIZE, y*CHUNK_SIZE);
		m0.generatePerlin(2f, 1.2f, 1.8f, true);
		HeightMap m1 = new HeightMap(CHUNK_SIZE, t);
		m1.setBaseXY(x*CHUNK_SIZE, y*CHUNK_SIZE);
		m1.generatePerlin(4f, 1f, 1.4f, true);

		HeightMap s = new HeightMap(CHUNK_SIZE, t);
		s.setBaseXY(x*CHUNK_SIZE, y*CHUNK_SIZE);
		s.generatePerlin(0f, 0.6f, 1.2f, true);
		s.amplify(0.25f, 0.5f, 0.5f);
		
		HeightMap map = HeightMap.blend(m0, m1, s);
		
		// TODO multi-texture terrain
		return FastMeshBuilder.terrain(CHUNK_SIZE, map.hmap, CHUNK_SIZE/2, StandardShader.standardVertexInfo, null);
		
		// TODO decals
	}

	private void createTerrain() {
		long t = System.currentTimeMillis();
		for(int x=0; x<3; x++)
			for(int y=0; y<3; y++) {
				terrain[x][y] = createTerrainMesh(t, x, y);
				if(terrainActor[x][y]==null) {
					terrainActor[x][y] = StaticMeshActor.make(scene, terrain[x][y], StandardShader.getInstance(), diffuse, specular, plainNormalTexture);
					terrainActor[x][y].position.set((x-1)*CHUNK_SIZE, 0f, (y-1)*CHUNK_SIZE);
					terrainActor[x][y].updateTransform();
				}
				else {
					terrainActor[x][y].setMesh(terrain[x][y]);
				}
			}
		System.out.printf("Mesh generated in %d ms\n", (System.currentTimeMillis() - t));
	}
	
	private void destroyTerrain() {
		for(int x=0; x<3; x++)
			for(int y=0; y<3; y++) {
				terrain[x][y].destroy();
			}
	}

	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>W</b> / <b>A</b> / <b>S</b> / <b>D</b>|Move around",
				"<b>SPACE</b> / <b>LSHIFT</b>|Fly up/down",
				"Hold <b>LMB</b>|Mouse look",
				"Drag <b>RMB</b>|Move light source",
				"<b>F1</b>|Toggle FPS limit and VSync",
				"<b>F2</b>|Toggle wireframe",
				"<b>F4</b>|Generate new terrain",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		specular = new Texture("ice2a.jpg");
		
		diffuse = new Texture("ice1a2.jpg");
		float anis = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
		System.out.printf("Max anisotropy: %.1f\n", anis);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anis);
		
		normal = new Texture("ice_n.jpg");
		
		createTerrain();
		water = FastMeshBuilder.plane(64f, 4, 32, StandardShader.standardVertexInfo, null);
		for(int x=0; x<3; x++)
			for(int y=0; y<3; y++) {
				waterActor[x][y] = StaticMeshActor.make(scene, water, StandardShader.getInstance(), BufferTexture.createPlainColor(4, 4, new Color(0.4f, 0.5f, 0.65f)), plainSpecularTexture, plainNormalTexture);
				waterActor[x][y].position.set((x-1)*CHUNK_SIZE, 0f, (y-1)*CHUNK_SIZE);
				waterActor[x][y].updateTransform();
			}
		// pickObjects = new StaticMesh[] {null, terrain};
		
		CLEAR_COLOR = new Color(0.7f, 0.75f, 0.82f);
		StandardShader.environment.setFog(0f, 50f, new Vector4f(0.7f, 0.75f, 0.82f, 1f));
		StandardShader.environment.ambientColor.set(0.05f, 0.1f, 0.2f);
		StandardShader.environment.lightColor.set(0.9f, 0.85f, 0.8f);
		lightActor.rotation.x = (float) Math.PI / 6f;
		lightActor.updateTransform();
	}
	
	public Matrix4f projectionMatrix() {
		return Projection.perspective(settings.fov, getAspectRatio(), 0.1f, 50.0f);
	}
	
	@Override
	protected void keyDown(int key) {
		switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_F4:
				destroyTerrain();
				createTerrain();
				break;
			default:
				super.keyDown(key);
		}
	}
	
	@Override
	protected void drawObjects(RenderTarget target) {
		for(int x=0; x<3; x++)
			for(int y=0; y<3; y++) {
				terrainActor[x][y].draw();
				waterActor[x][y].draw();
			}
	}
	
	@Override
	protected void destroyResources() {
		diffuse.destroy();
		specular.destroy();
		normal.destroy();
		water.destroy();
		for(int x=0; x<3; x++)
			for(int y=0; y<3; y++) {
				terrain[x][y].destroy();
			}
	}

	public static void main(String[] args) {
		new GLBasicTerrain().init("GLExample").run();
	}

}
