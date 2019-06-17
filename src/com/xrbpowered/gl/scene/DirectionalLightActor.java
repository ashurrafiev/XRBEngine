package com.xrbpowered.gl.scene;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.shaders.StandardShader;

public class DirectionalLightActor extends Actor {

	public DirectionalLightActor(Scene scene) {
		super(scene);
	}

	private static Matrix4f m = new Matrix4f();
	private static Vector4f d = new Vector4f();
	
	@Override
	public void updateTransform() {
		m.setIdentity();
		Actor.rotateYawPitchRoll(this.rotation, m);
		d.set(0, 0, 1);
		Matrix4f.transform(m, d, d);
		StandardShader.environment.lightDir.set(d.x, d.y, d.z);
	}
	
}
