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
package com.xrbpowered.gl.ui.widgets;

import java.awt.Graphics2D;

import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.ui.UIPane;

public class WidgetPane extends UIPane {

	private boolean dirty = false; 
	private WidgetBox root = null;
	
	public final WidgetManager manager;
	
	public WidgetPane(WidgetManager manager, int x, int y, int w, int h, boolean staticBuffers) {
		super(manager.ui);
		this.manager = manager;
		manager.add(this);
		setTexture(new BufferTexture(w, h, false, false, staticBuffers) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				return updateBufferTexture(g2, getWidth(), getHeight());
			}
		});
		setAnchor(x, y);
	}
	
	public void requestRepaint() {
		dirty = true;
	}
	
	public void setRoot(WidgetBox root, boolean fill) {
		this.root = root;
		if(root!=null) {
			root.setPane(this);
			if(fill) {
				root.setPosition(0, 0);
				root.setSize(getWidth(), getHeight());
			}
		}
		dirty = true;
	}
	
	public WidgetBox getRoot() {
		return root;
	}
	
	private boolean updateBufferTexture(Graphics2D g2, int w, int h) {
		BufferTexture.clearBuffer(g2, w, h);
		if(root!=null)
			root.paint(g2);
		dirty = false;
		return true;
	}
	
	@Override
	public void draw() {
		if(dirty)
			repaint();
		super.draw();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		manager.remove(this);
	}
}
