package com.xrbpowered.gl.collider;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.scene.Actor;

public class ColliderEdge {

	public Vector2f pivot;
	public Vector2f delta;
	public Vector2f tangent;
	public Vector2f normal;
	
	public ColliderEdge(Vector2f pivot, Vector2f delta) {
		this.pivot = pivot;
		this.delta = delta;
		recalculate();
	}
	
	public ColliderEdge(ColliderEdge e) {
		this.pivot = e.pivot;
		this.delta = e.delta;
		this.tangent = e.tangent;
		this.normal = e.normal;
	}
	
	private void recalculate() {
		this.tangent = delta.normalise(null);
		this.normal = new Vector2f(-tangent.y, tangent.x);
	}
	
	public ColliderEdge transformWithActor(Actor actor) {
		Matrix4f m = actor.getTransform();
		Vector4f p = new Vector4f(pivot.x, 0f, pivot.y, 1f);
		Vector4f d = new Vector4f(delta.x, 0f, delta.y, 0f);
		Matrix4f.transform(m, p, p);
		Matrix4f.transform(m, d, d);
		this.pivot = new Vector2f(p.x, p.z);
		this.delta = new Vector2f(d.x, d.z);
		recalculate();
		return this;
	}
	
}
