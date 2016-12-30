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
