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

import com.xrbpowered.gl.scene.Actor;

public class ActorShader extends SceneShader {

	public ActorShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	}

	public ActorShader(Actor actor, VertexInfo info, String pathVS, String pathFS) {
		super(actor.scene, info, pathVS, pathFS);
	}

	private int modelMatrixLocation;
	
	private Actor actor = null;
	
	public void setActor(Actor actor) {
		this.actor = actor;
		super.setScene(actor.scene);
	}
	
	public Actor getActor() {
		return actor;
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");
	}
	
	@Override
	public void updateUniforms() {
		if(actor==null)
			return;
		super.updateUniforms();
		uniform(modelMatrixLocation, actor.getTransform());
	}
}
