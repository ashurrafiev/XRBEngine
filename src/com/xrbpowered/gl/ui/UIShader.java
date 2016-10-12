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
