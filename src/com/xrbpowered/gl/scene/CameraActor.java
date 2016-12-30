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
package com.xrbpowered.gl.scene;

import org.lwjgl.util.vector.Matrix4f;

public class CameraActor extends Actor {

	private Matrix4f projection;
	private final Matrix4f view = new Matrix4f();
	private final Matrix4f followView = new Matrix4f();
	
	public CameraActor(Scene scene) {
		super(scene);
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
