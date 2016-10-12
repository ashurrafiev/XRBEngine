package com.xrbpowered.gl.scene;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.shaders.StandardShader;

public class DirectionalLightActor extends Actor {

	public DirectionalLightActor(Scene scene) {
		super(scene);
	}

	@Override
	public void updateTransform() {
		Matrix4f m = new Matrix4f();
		Actor.rotateYawPitchRoll(this.rotation, m);
		Vector4f d = new Vector4f();
		d.set(0, 0, 1);
		Matrix4f.transform(m, d, d);
		StandardShader.environment.lightDir.set(d.x, d.y, d.z);
	}
	
}
