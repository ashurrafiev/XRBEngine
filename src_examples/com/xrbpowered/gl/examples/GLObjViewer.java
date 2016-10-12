package com.xrbpowered.gl.examples;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.ObjMeshLoader;
import com.xrbpowered.gl.res.StandardMeshBuilder;
import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLObjViewer extends ExampleClient {

	public static final String INPUT_OBJ_FILE = "test.obj";
	
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
	protected void setupResources() {
		super.setupResources();
		texture = BufferTexture.createPlainColor(4, 4, new Color(0xdd4422));
		normal = new Texture("normal.jpg");
		
//		mesh = StandardMeshBuilder.sphere(0.5f, 32);
//		mesh = StandardMeshBuilder.cube(1f);
		mesh = ObjMeshLoader.loadObj(INPUT_OBJ_FILE, 0, 1f);
		
		meshActor = StandardMeshBuilder.makeActor(scene, mesh, texture, plainSpecularTexture, normal);
		meshActor.position = new Vector3f(0, 0, -2);
		meshActor.updateTransform();
		
		StandardShader.environment.ambientColor.set(0f, 0f, 0f);
		StandardShader.environment.lightColor.set(1f, 1f, 1f);
		lightActor.rotation = new Vector3f((float)Math.PI, 0, 0);
		lightActor.updateTransform();
		
		controller = new Controller().setActor(meshActor);
		activeController = controller;
		
		uiDebugTitle = INPUT_OBJ_FILE;
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
//			meshActor.rotation.y += ((float)(Math.PI / 18f)) * dt;
//			meshActor.updateTransform();
			lightActor.rotation.y += ((float)(Math.PI / 6f)) * dt;
			lightActor.updateTransform();
		}
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		meshActor.draw();
	}
	
	public static void main(String[] args) {
		new GLObjViewer();
	}

}
