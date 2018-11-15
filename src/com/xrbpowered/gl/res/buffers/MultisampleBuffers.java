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
package com.xrbpowered.gl.res.buffers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class MultisampleBuffers extends OffscreenBuffers {

	private int colorMSTexId;
	private int depthMSTexId;
	private OffscreenBuffers resolve;
	
	public MultisampleBuffers(int w, int h, int samples, boolean hdr) {
		super(GL30.glGenFramebuffers(), w, h);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		create(w, h, samples, true, hdr);
		resolve = new OffscreenBuffers(w, h, false, hdr);
	}

	public MultisampleBuffers(int w, int h, int samples) {
		this(w, h, samples, false);
	}

	protected void create(int w, int h, int samples, boolean depthBuffer, boolean hdr) {
		colorMSTexId = GL11.glGenTextures();
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, colorMSTexId);
		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samples, hdr ? GL30.GL_RGB16F : GL11.GL_RGB, w, h, false);
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, colorMSTexId, 0);
		
		depthMSTexId = 0;
		if(depthBuffer) {
			depthMSTexId = GL11.glGenTextures();
			GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, depthMSTexId);
			GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samples, GL30.GL_DEPTH24_STENCIL8, w, h, false);
			GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthMSTexId, 0);
		}
		checkStatus();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	@Override
	public OffscreenBuffers resolve() {
		blit(this, resolve, false);
		return resolve;
	}
	
	public void bindColorBuffer(int index) {
		resolve.bindColorBuffer(index);
	}

	public void bindDepthBuffer(int index) {
		if(depthMSTexId>0)
			resolve.bindDepthBuffer(index);
	}
	
	@Override
	public boolean isMultisample() {
		return true;
	}
	
	@Override
	public void destroy() {
		resolve.destroy();
		super.destroy();
	}

}
