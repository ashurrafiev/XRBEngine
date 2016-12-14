package com.xrbpowered.gl.res.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.ui.UIManager;

public class PostProcessShader extends Shader {

	private static StaticMesh quad = null;
	
	private float time = 0f;
	private int timeLocation;
	
	public PostProcessShader(String shaderPath) {
		super(UIManager.uiVertexInfo, "scrn_v.glsl", shaderPath);
	}
	
	@Override
	protected void storeUniformLocations() {
		timeLocation = GL20.glGetUniformLocation(pId, "time");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "colorBuf"), 0);
		GL20.glUseProgram(0);
	}
	@Override
	public void updateUniforms() {
		GL20.glUniform1f(timeLocation, time);
	}
	
	public void draw(OffscreenBuffers src, float dt) {
		time += dt;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		createQuad();
		
		use();
		if(src!=null)
			src.bindColorBuffer(0);
		quad.draw();
		Texture.unbind(0);
		unuse();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
	}
	
	public void destroy() {
		super.destroy();
		if(quad!=null) {
			quad.destroy();
			quad = null;
		}
	}
	
	private static void createQuad() {
		if(quad==null) {
			quad = new StaticMesh(UIManager.uiVertexInfo, new float[] {
					-1, -1, 0, 0,
					1, -1, 1, 0,
					1, 1, 1, 1,
					-1, 1, 0, 1
			}, new short[] {
					0, 3, 2, 2, 1, 0
			});
		}
	}
}
