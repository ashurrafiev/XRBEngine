package com.xrbpowered.gl.res.shaders;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.scene.Scene;

public class SceneShader extends Shader {

	protected int projectionMatrixLocation;
	protected int viewMatrixLocation;
	protected int cameraPositionLocation;

	protected Scene scene = null;
	
	public SceneShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	}
	
	public SceneShader(Scene scene, VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
		this.scene = scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public Scene getScene() {
		return scene;
	}

	@Override
	protected void storeUniformLocations() {
		projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
		cameraPositionLocation = GL20.glGetUniformLocation(pId, "cameraPosition");
	}

	@Override
	public void updateUniforms() {
		if(scene==null)
			return;
		uniform(projectionMatrixLocation, scene.activeCamera.getProjection());
		uniform(viewMatrixLocation, scene.activeCamera.getView());
		if(cameraPositionLocation>=0)
			uniform(cameraPositionLocation, scene.activeCamera.position);
	}

}
