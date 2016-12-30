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

import java.awt.Color;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;

public class PostProcessRenderer implements Renderer {

	public final Renderer parent;
	public final PostProcessShader postProc;

	private boolean requestUpdate = true;
	private boolean updatePerFrame;
	
	protected Color clearColor = Color.BLACK;
	protected OffscreenBuffers bgBuffer = null;
	protected RenderTarget interBuffer = null;
	protected RenderTarget largeBuffer = null;
	
	public PostProcessRenderer(Renderer parent, PostProcessShader shader, boolean updatePerFrame) {
		this.parent = parent;
		this.postProc = shader;
		this.updatePerFrame = updatePerFrame;
		resizeBuffers();
	}

	protected void destroyBuffers() {
		if(bgBuffer!=null) {
			bgBuffer.destroy();
			bgBuffer = null;
		}
		if(interBuffer!=null) {
			interBuffer.destroy();
			interBuffer = null;
		}
		if(largeBuffer!=null) {
			largeBuffer.destroy();
			largeBuffer = null;
		}
	}
	
	protected void resizeBuffers(int bgWidth, int bgHeight, int interWidth, int interHeight) {
		destroyBuffers();
		bgBuffer = new OffscreenBuffers(bgWidth, bgHeight, true);
		if(postProc!=null)
			interBuffer = new OffscreenBuffers(interWidth, interHeight, false);
	}
	
	public void resizeBuffers() {
		int w = Display.getWidth();
		int h = Display.getHeight();
		resizeBuffers(w, h, w, h);
	}
	
	public void requestUpdate() {
		requestUpdate = true;
//		redrawBackgroundBuffer();
	}
	
	public void setUpdatePerFrame(boolean updatePerFrame) {
		this.updatePerFrame = updatePerFrame;
	}
	
	protected void redrawBackgroundBuffer(float dt) {
		if(bgBuffer==null)
			return;
		bgBuffer.use();
		parent.redraw(bgBuffer, dt);
		requestUpdate = false;
	}
	
	private void blit(RenderTarget src, RenderTarget target) {
		if(target.isMultisample()) {
			if(largeBuffer!=null && (largeBuffer.getWidth()!=target.getWidth() || largeBuffer.getHeight()!=target.getHeight())) {
				largeBuffer.destroy();
				largeBuffer = null;
			}
			if(largeBuffer==null) {
				largeBuffer = new OffscreenBuffers(target.getWidth(), target.getHeight(), false);
			}
			OffscreenBuffers.blit(src, largeBuffer, true);
			OffscreenBuffers.blit(largeBuffer, target, false);
		}
		else {
			OffscreenBuffers.blit(src, target, true);
		}
	}
	
	@Override
	public void redraw(RenderTarget target, float dt) {
		if(requestUpdate || updatePerFrame) {
			redrawBackgroundBuffer(updatePerFrame ? dt : 0f);
		}
		
		if(bgBuffer!=null) {
			if(postProc==null)
				blit(bgBuffer, target);
			else {
				GL11.glDisable(GL11.GL_CULL_FACE);
				interBuffer.use();
				postProc.draw(bgBuffer, dt);
				blit(interBuffer, target);
			}
			target.use();
		}
		else {
			target.use();
			GL11.glClearColor(clearColor.getRed() / 255f, clearColor.getGreen() / 255f, clearColor.getBlue() / 255f, 0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}
	}
	
}
