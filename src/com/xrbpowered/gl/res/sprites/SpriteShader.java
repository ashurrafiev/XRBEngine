package com.xrbpowered.gl.res.sprites;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shaders.InstanceBuffer;
import com.xrbpowered.gl.res.shaders.Shader;

public class SpriteShader extends Shader {

	public static final String[] INS_ATTRIB_NAMES = {"ins_Position", "ins_Size", "ins_RotationScale", "ins_TexCoord", "ins_Color"};
	public static final int INS_STRIDE = 12;

	private SpriteShader() {
		super(SpriteLayer.spriteVertexInfo, "sprite_v.glsl", "sprite_f.glsl");
	}
	
	private int screenSizeLocation;
	
	@Override
	protected void storeUniformLocations() {
		screenSizeLocation = GL20.glGetUniformLocation(pId, "screenSize");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "tex"), 0);
		GL20.glUniform2f(screenSizeLocation, Display.getWidth(), Display.getHeight());
		GL20.glUseProgram(0);
	}
	
	@Override
	protected int bindAttribLocations() {
		int startAttrib = super.bindAttribLocations();
		return InstanceBuffer.bindAttribLocations(this, startAttrib, INS_ATTRIB_NAMES);
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
	
	@Override
	public void unuse() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		super.unuse();
	}
	
	private static SpriteShader instance = null;
	
	public static SpriteShader getInstance() {
		if(instance==null)
			instance = new SpriteShader();
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.destroy();
			instance = null;
		}
	}
	
	public static InstanceBuffer createInstanceBuffer(int maxCount) {
		return new InstanceBuffer(1, maxCount, 1, new int[] {2, 2, 2, 2, 4});
	}
}
