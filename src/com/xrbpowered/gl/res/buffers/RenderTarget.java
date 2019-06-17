package com.xrbpowered.gl.res.buffers;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.xrbpowered.gl.Client;

public abstract class RenderTarget {

	public final int fbo;
	private final int width, height;
	
	protected RenderTarget(int fbo, int w, int h) {
		this.fbo = fbo;
		this.width = w;
		this.height = h;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean isMultisample() {
		return false;
	}
	
	public void use() {
		GL11.glViewport(0, 0, getWidth(), getHeight());
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
	}
	
	public RenderTarget resolve() {
		return this;
	}
	
	public void destroy() {
	}
	
	public static final RenderTarget primaryBuffer = new RenderTarget(0, 0, 0) {
		@Override
		public int getWidth() {
			return Display.getWidth();
		}
		@Override
		public int getHeight() {
			return Display.getHeight();
		}
	};
	
	public static void blit(RenderTarget source, RenderTarget target, boolean filter) {
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, source.fbo);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.fbo);
//		System.out.printf("[%d] %dx%d -> [%d] %dx%d\n", source.fbo, source.getWidth(), source.getHeight(), target.fbo, target.getWidth(), target.getHeight());
		GL30.glBlitFramebuffer(0, 0, source.getWidth(), source.getHeight(), 0, 0, target.getWidth(), target.getHeight(), GL11.GL_COLOR_BUFFER_BIT, filter ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		Client.checkError();
	}
	
	protected static void checkStatus() {
		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if(status != GL30.GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException(String.format("Framebuffer not complete: %04X", status));
	}

}
