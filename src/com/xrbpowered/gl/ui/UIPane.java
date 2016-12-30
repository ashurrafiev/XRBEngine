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

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;

public class UIPane implements UIPage {

	private StaticMesh quad = null;
	protected Texture texture = null;
	
	public float x = 0f;
	public float y = 0f;
	public float alpha = 1f;
	
	private boolean visible = true;
	public UIPage parentPage = null;
	
	private final UIManager ui;
	
	protected UIPane(UIManager ui) {
		this.ui = ui;
		ui.panes.add(this);
	}
	
	public UIPane(UIManager ui, Texture texture) {
		this.ui = ui;
		ui.panes.add(this);
		setTexture(texture);
	}
	
	protected void setTexture(Texture texture) {
		if(this.texture!=null)
			this.texture.destroy();
		if(this.quad!=null)
			this.quad.destroy();
		
		this.texture = texture;
		int w = texture.getWidth();
		int h = texture.getHeight();
		this.quad = new StaticMesh(UIManager.uiVertexInfo, new float[] {
				0, 0, 0, 0,
				w, 0, 1, 0,
				w, h, 1, 1,
				0, h, 0, 1
		}, new short[] {
				0, 3, 2, 2, 1, 0
		});
	}
	
	public UIPane setAnchor(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public UIPane centerX() {
		this.x = (Display.getWidth()-getWidth())/2;
		return this;
	}

	public UIPane centerY() {
		this.y = (Display.getHeight()-getHeight())/2;
		return this;
	}
	
	public UIPane center() {
		return centerX().centerY();
	}

	public int getWidth() {
		return texture.getWidth();
	}

	public int getHeight() {
		return texture.getHeight();
	}
	
	@Override
	public boolean isVisible() {
		return parentPage==null ? visible : parentPage.isVisible() && visible;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public void layout(int screenWidth, int screenHeight) {
	}

	public void repaint() {
		if(texture instanceof BufferTexture)
			((BufferTexture) texture).update();
	}
	
	public void draw() {
		if(!isVisible() || alpha<=0f)
			return;
		UIShader shader = UIShader.getInstance();
		shader.updateUniforms(x, y, alpha);
		texture.bind(0);
		quad.draw();
	}
	
	public void destroy() {
		ui.panes.remove(this);
		quad.destroy();
		texture.destroy();
	}
	
}
