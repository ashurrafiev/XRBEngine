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
package com.xrbpowered.gl.ui;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shaders.Shader;

public class UIShader extends Shader {
	
	private UIShader() {
		super(UIManager.uiVertexInfo, "ui_v.glsl", "ui_f.glsl");
	}
	
	private int anchorLocation;
	private int alphaLocation;
	private int screenSizeLocation;
	
	@Override
	protected void storeUniformLocations() {
		alphaLocation = GL20.glGetUniformLocation(pId, "alpha");
		anchorLocation = GL20.glGetUniformLocation(pId, "anchor");
		screenSizeLocation = GL20.glGetUniformLocation(pId, "screenSize");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "tex"), 0);
		GL20.glUniform2f(screenSizeLocation, Display.getWidth(), Display.getHeight());
		GL20.glUseProgram(0);
	}
	
	public void resize() {
		GL20.glUseProgram(pId);
		GL20.glUniform2f(screenSizeLocation, Display.getWidth(), Display.getHeight());
		GL20.glUseProgram(0);
	}
	
	@Override
	public void use() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		super.use();
	}
	
	@Override
	public void updateUniforms() {
	}
	
	public void updateUniforms(float x, float y, float alpha) {
		GL20.glUniform2f(anchorLocation, x, y);
		GL20.glUniform1f(alphaLocation, alpha);
	}
	
	@Override
	public void unuse() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		super.unuse();
	}
	
	private static UIShader instance = null;
	
	public static UIShader getInstance() {
		if(instance==null)
			instance = new UIShader();
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.destroy();
			instance = null;
		}
	}
}
