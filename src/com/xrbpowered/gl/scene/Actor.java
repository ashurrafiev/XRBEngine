package com.xrbpowered.gl.scene;

import java.util.Comparator;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Actor {

	public final Scene scene;
	
	public Vector3f position = new Vector3f(0, 0, 0);
	public Vector3f scale = new Vector3f(1, 1, 1);
	public Vector3f rotation = new Vector3f(0, 0, 0);
	public float depth;
	
	private final Matrix4f transform = new Matrix4f();
	
	public Actor(Scene scene) {
		this.scene = scene;
	}
	
	public void updateTransform() {
		setTransform(position, scale, rotation, transform);
	}
	
	public Matrix4f getTransform() {
		return transform;
	}
	
	public Vector4f calcViewPos(Vector4f p) {
		if(p==null)
			p = new Vector4f();
		Matrix4f m = Matrix4f.mul(scene.activeCamera.getProjection(), scene.activeCamera.getView(), null);
		Matrix4f.mul(m, getTransform(), m);
		p.set(0f, 0f, 0f, 1f);
		Matrix4f.transform(m, p, p);
		p.x /= p.w;
		p.y /= p.w;
		return p;
	}
	
	public float calcDepth() {
		Vector4f p = calcViewPos(null);
		depth = p.z;
		return depth;
	}
	
	public float getDistTo(Actor actor) {
		Vector3f d = Vector3f.sub(position, actor.position, null);
		return d.length();
	}
	
	protected static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	protected static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	protected static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
	
	public static Matrix4f setTransform(Vector3f position, Vector3f scale, Vector3f rotation, Matrix4f m) {
		Matrix4f.setIdentity(m);
		Matrix4f.scale(scale, m, m);
		Matrix4f.translate(position, m, m);
		rotateYawPitchRoll(rotation, m);
		return m;
	}
	
	public static Matrix4f rotateYawPitchRoll(Vector3f rotation, Matrix4f m) {
		Matrix4f.rotate(rotation.z, Z_AXIS, m, m);
		Matrix4f.rotate(rotation.y, Y_AXIS, m, m);
		Matrix4f.rotate(rotation.x, X_AXIS, m, m);
		return m;
	}
	
	public Comparator<Actor> sortBackToFront = new Comparator<Actor>() {
		@Override
		public int compare(Actor o1, Actor o2) {
			return -Float.compare(o1.depth, o2.depth);
		}
	};
	
	public Comparator<Actor> sortFrontToBack = new Comparator<Actor>() {
		@Override
		public int compare(Actor o1, Actor o2) {
			return Float.compare(o1.depth, o2.depth);
		}
	};
	
}
