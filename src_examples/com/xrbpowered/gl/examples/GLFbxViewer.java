package com.xrbpowered.gl.examples;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.builder.ObjMeshLoader;
import com.xrbpowered.gl.res.fbx.FbxMeshLoader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.FileAssetManager;

public class GLFbxViewer extends ExampleClient {

	public static final String INPUT_FBX_FILE = "anibox.fbx";
	public static final String INPUT_OBJ_FILE = "wall/wall_frame.obj";
	public static final String MODEL_NAME = "Model::Cube";
	
	private StaticMesh fbxMesh;
	private StaticMesh objMesh;
	
	private Texture texture;
	private Texture normal;
	
	private StaticMeshActor meshActor;
	
	private boolean rotating = false;
	
	public GLFbxViewer() {
		AssetManager.defaultAssets = new FileAssetManager("../Greenhouse/assets/prefabs",
				new FileAssetManager("../Greenhouse/assets_src/greenhouse", AssetManager.defaultAssets));
		init("ObjViewer");
		run();
	}

	@Override
	protected void setupResources() {
		super.setupResources();
		texture = new Texture("checker.png");
//		texture = BufferTexture.createPlainColor(4, 4, new Color(0xdd4422));
		normal = plainNormalTexture; //new Texture("normal.jpg");
		
//		mesh = StandardMeshBuilder.sphere(0.5f, 32);
//		mesh = StandardMeshBuilder.cube(1f);
//		mesh = ObjMeshLoader.loadObj(INPUT_OBJ_FILE, 0, 1f);
		fbxMesh = FbxMeshLoader.loadFbx(INPUT_FBX_FILE, MODEL_NAME, 1f, StandardShader.standardVertexInfo, null);
		objMesh = ObjMeshLoader.loadObj(INPUT_OBJ_FILE, 0, 1f, StandardShader.standardVertexInfo, null);
		
		meshActor = StaticMeshActor.make(scene, fbxMesh, StandardShader.getInstance(), texture, plainSpecularTexture, normal);
		meshActor.position = new Vector3f(0, 0, -2);
		meshActor.updateTransform();
		
		StandardShader.environment.ambientColor.set(0f, 0f, 0f);
		StandardShader.environment.lightColor.set(1f, 1f, 1f);
		lightActor.rotation = new Vector3f((float)Math.PI, 0, 0);
		lightActor.updateTransform();
		
		controller = new Controller().setActor(meshActor);
		activeController = controller;
		
		uiDebugTitle = INPUT_FBX_FILE;
	}

	@Override
	protected void destroyResources() {
		super.destroyResources();
		texture.destroy();
		fbxMesh.destroy();
		objMesh.destroy();
	}

	@Override
	protected void keyDown(int key) {
		switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_1:
				meshActor.setMesh(fbxMesh);
				uiDebugTitle = INPUT_FBX_FILE;
				break;
			case Keyboard.KEY_2:
				meshActor.setMesh(objMesh);
				uiDebugTitle = INPUT_OBJ_FILE;
				break;
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
	protected void update(float dt) {
		super.update(dt);
		if(rotating) {
//			meshActor.rotation.y += ((float)(Math.PI / 18f)) * dt;
//			meshActor.updateTransform();
			lightActor.rotation.y += ((float)(Math.PI / 6f)) * dt;
			lightActor.updateTransform();
		}
	}
	
	@Override
	protected void drawObjects(RenderTarget target) {
		GL11.glEnable(GL11.GL_CULL_FACE);
		meshActor.draw();
	}
	
	public static void main(String[] args) {
		new GLFbxViewer();
	}

}
