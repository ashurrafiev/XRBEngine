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
