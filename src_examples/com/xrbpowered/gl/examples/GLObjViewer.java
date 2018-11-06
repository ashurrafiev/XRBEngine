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
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.builder.ObjMeshLoader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLObjViewer extends ExampleClient {

	public static final String INPUT_OBJ_FILE = "../Prototypes/assets/leaves.obj";
	//public static final String INPUT_OBJ_FILE = "test.obj";
	
	private StaticMesh mesh;
	private Texture texture;
	private Texture normal;
	
	private StaticMeshActor meshActor;
	
	private boolean rotating = false;
	
	public GLObjViewer() {
		init("ObjViewer");
		run();
	}

	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>W</b> / <b>A</b> / <b>S</b> / <b>D</b>|Move object",
				"<b>SPACE</b> / <b>LSHIFT</b>|Move up/down",
				"Drag <b>LMB</b>|Rotate object",
				"Drag <b>RMB</b>|Move light source",
				"<b>F1</b>|Toggle FPS limit and VSync",
				"<b>F2</b>|Toggle wireframe",
				"<b>F3</b>|Toggle light rotation",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		texture = new Texture("../Prototypes/assets/leaves.png", false, true);
		//texture = BufferTexture.createPlainColor(4, 4, new Color(0xdd4422));
		normal = new Texture("normal.jpg");
		
//		mesh = StandardMeshBuilder.sphere(0.5f, 32);
//		mesh = StandardMeshBuilder.cube(1f);
		mesh = ObjMeshLoader.loadObj(INPUT_OBJ_FILE, 0, 1f, StandardShader.standardVertexInfo, null);
		
		meshActor = StaticMeshActor.make(scene, mesh, StandardShader.getInstance(), texture, BufferTexture.createPlainColor(4, 4, Color.BLACK), plainNormalTexture);
		meshActor.position = new Vector3f(0, 0, -2);
		meshActor.updateTransform();
		
		StandardShader.environment.ambientColor.set(0.5f, 0.5f, 0.5f);
		StandardShader.environment.lightColor.set(0.5f, 0.5f, 0.5f);
		lightActor.rotation = new Vector3f((float)Math.PI, 0, 0);
		lightActor.updateTransform();
		
		controller = new Controller().setActor(meshActor);
		activeController = controller;
		
		uiDebugTitle = INPUT_OBJ_FILE.substring(INPUT_OBJ_FILE.lastIndexOf('/')+1);
		uiDebugInfo = String.format("Triangles: %d", mesh.countTris());

	}

	@Override
	protected void destroyResources() {
		super.destroyResources();
		texture.destroy();
		mesh.destroy();
	}

	@Override
	protected void keyDown(int key) {
		switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_F3:
				rotating = !rotating;
				break;
			case Keyboard.KEY_R:
				meshActor.position.z += 0.1f;
				meshActor.updateTransform();
				break;
			case Keyboard.KEY_F:
				meshActor.position.z -= 0.1f;
				meshActor.updateTransform();
				break;
			default:
				super.keyDown(key);
		}
	}
	
	@Override
	protected void drawObjects(RenderTarget target, float dt) {
		if(rotating) {
			meshActor.rotation.y += ((float)(Math.PI / 18f)) * dt;
			meshActor.updateTransform();
//			lightActor.rotation.y += ((float)(Math.PI / 6f)) * dt;
//			lightActor.updateTransform();
		}
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		//GL11.glDisable(GL11.GL_CULL_FACE);
		meshActor.draw();
	}
	
	public static void main(String[] args) {
		new GLObjViewer();
	}

}
