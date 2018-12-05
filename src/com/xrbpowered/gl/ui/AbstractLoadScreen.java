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

import java.awt.Color;
import java.awt.Graphics2D;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.res.textures.BufferTexture;

public abstract class AbstractLoadScreen {

	private UIManager ui = new UIManager();
	private UIPane uiPane;
	private Color clearColor;
	
	private int progress = 0;
	private long timeStart;
	
	public AbstractLoadScreen(final int maxProgress, int w, int h, Color clearColor) {
		this.clearColor = clearColor;
		uiPane = new UIPane(ui, new BufferTexture(w, h, false, false, maxProgress>0) {
			@Override
			protected boolean updateBuffer(Graphics2D g2, int w, int h) {
				float prog = maxProgress>0 ? (float)progress/(float)maxProgress : 0f;
				if(prog>1f)
					prog = 1f;
				return AbstractLoadScreen.this.updateBuffer(g2, w, h, (System.currentTimeMillis()-timeStart)/1000f, prog);
			}
		});
	}
	
	private void display() {
		GL11.glClearColor(clearColor.getRed() / 255f, clearColor.getGreen() / 255f, clearColor.getBlue() / 255f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		uiPane.x = Display.getWidth()/2 - uiPane.getWidth()/2;
		uiPane.y = Display.getHeight()/2 - uiPane.getHeight()/2;
		ui.draw(Display.getWidth(), Display.getHeight());
		Display.update();
	}
	
	public AbstractLoadScreen start() {
		timeStart = System.currentTimeMillis();
		progress = 0;
		uiPane.repaint();
		display();
		return this;
	}
	
	public void addProgress(int delta) {
		progress += delta;
		uiPane.repaint();
		display();
	}
	
	public void destroy() {
		uiPane.destroy();
	}
	
	protected abstract boolean updateBuffer(Graphics2D g2, int w, int h, float time, float progress);
	
}
