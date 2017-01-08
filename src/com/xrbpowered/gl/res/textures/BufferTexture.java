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
package com.xrbpowered.gl.res.textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public abstract class BufferTexture extends Texture {

	protected BufferedImage imgBuffer = null;
	private IntBuffer intBuffer = null;
	private int[] pixels = null;
	private boolean staticBuffers = false;
	
	public BufferTexture(int w, int h, boolean wrap, boolean filter, boolean staticBuffers) {
		this.staticBuffers = staticBuffers;
		this.width = w;
		this.height = h;
		createBuffers();
		updateBuffer((Graphics2D) imgBuffer.getGraphics());
		create(imgBuffer, intBuffer, wrap, filter);
		if(!staticBuffers)
			destroyBuffers();
	}
	
	private void createBuffers() {
		this.imgBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.intBuffer = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder()).asIntBuffer();
	}
	
	private void destroyBuffers() {
		this.imgBuffer = null;
		this.intBuffer = null;
		this.pixels = null;
	}
	
	protected abstract boolean updateBuffer(Graphics2D g2);
	
	public void update() {
		if(this.imgBuffer==null)
			createBuffers();
		
		if(updateBuffer((Graphics2D) imgBuffer.getGraphics())) {
			pixels = imgBuffer.getRGB(0, 0, width, height, pixels, 0, width);
			intBuffer.put(pixels);
			intBuffer.flip();
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, getId());
	
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);
//			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); // TODO needless mipmaps?
		}
		
		if(!staticBuffers)
			destroyBuffers();
	}
	
	public static final Color CLEAR_COLOR = new Color(0, 0, 0, 0);
	
	public static void clearBuffer(Graphics2D g2, int w, int h) {
		g2.setBackground(CLEAR_COLOR);
		g2.clearRect(0, 0, w, h);
	}
	
	public static BufferTexture createPlainColor(int w, int h, final Color color, boolean wrap, boolean filter) {
		return new BufferTexture(w, h, true, wrap, filter) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				g2.setBackground(color);
				g2.clearRect(0, 0, getWidth(), getHeight());
				return true;
			}
		};
	}
	
	public static BufferTexture createPlainColor(int w, int h, final Color color) {
		return createPlainColor(w, h, color, true, true);
	}
}
