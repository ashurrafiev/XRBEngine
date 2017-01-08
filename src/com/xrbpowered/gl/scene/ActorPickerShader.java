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

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.shaders.VertexInfo;

public class ActorPickerShader extends ActorShader {
	
	private ActorPickerShader() {
		super(new VertexInfo().addAttrib("in_Position", 3), "pick_v.glsl", "pick_f.glsl");
	}
	
	private int objIdLocation;
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		objIdLocation = GL20.glGetUniformLocation(pId, "objId");
	}
	
	public void updateUniforms(Actor actor, int objId) {
		setActor(actor);
		updateUniforms();
		GL20.glUniform3f(objIdLocation, (float)((objId>>16)&0xff) / 255f, (float)((objId>>8)&0xff) / 255f, (float)(objId&0xff) / 255f);
	}
	
	private static ActorPickerShader instance = null;
	
	public static ActorPickerShader getInstance() {
		if(instance==null)
			instance = new ActorPickerShader();
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.destroy();
			instance = null;
		}
	}
}
