package com.xrbpowered.gl.scene;

import org.lwjgl.util.vector.Matrix4f;

public class CameraActor extends Actor {

	private Matrix4f projection;
	private final Matrix4f view = new Matrix4f();
	private final Matrix4f followView = new Matrix4f();
	
	public CameraActor(Scene scene) {
		super(scene);
		scene.activeCamera = this;
	}
	
	public CameraActor setProjection(Matrix4f projection) {
		this.projection = projection;
		return this;
	}
	
	public Matrix4f getProjection() {
		return projection;
	}
	
	public Matrix4f getView() {
		return view;
	}
	
	public Matrix4f getFollowView() {
		return followView;
	}
	
	@Override
	public void updateTransform() {
		super.updateTransform();
		Matrix4f.setIdentity(view);
		Matrix4f.translate(position, view, view);
		rotateYawPitchRoll(rotation, view);
		Matrix4f.invert(view, view);
		
		Matrix4f.setIdentity(followView);
		rotateYawPitchRoll(rotation, followView);
		Matrix4f.invert(followView, followView);
	}
	
}
