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

import java.awt.Graphics2D;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.Scene;

public class UIPointerActor extends Actor {

	private class PointerTexture extends BufferTexture {
		public PointerTexture(int w, int h, boolean staticBuffers) {
			super(w, h, false, false, staticBuffers);
		}
		@Override
		protected boolean updateBuffer(Graphics2D g2) {
			return UIPointerActor.this.updateBuffer(g2);
		}
	}
	
	public final UIPane pane;
	public int pivotx, pivoty;
	
	public float dist;
	public float maxDist = -1f;
	
	public boolean visible = true;
	
	public UIPointerActor(UIManager ui, Scene scene, int w, int h, boolean staticBuffers) {
		super(scene);
		pane = new UIPane(ui, new PointerTexture(w, h, staticBuffers));
		pivotx = w/2;
		pivoty = h/2;
	}
	
	public UIPointerActor(UIManager ui, Scene scene, BufferTexture texture) {
		super(scene);
		pane = new UIPane(ui, texture);
		pivotx = texture.getWidth()/2;
		pivoty = texture.getHeight()/2;
	}
	
	public void updateView() {
		dist = scene.activeCamera.getDistTo(this);
		Vector4f p = calcViewPos(null);
		pane.x = (p.x+1f)*(float)Display.getWidth()/2f - pivotx;
		pane.y = (1f-p.y)*(float)Display.getHeight()/2f - pivoty;
		pane.setVisible(visible && (p.x>=-1f && p.x<=1f) && (p.y>=-1f && p.y<=1f) && (p.z>0 && (maxDist<0f || dist<=maxDist)));
	}
	
	protected boolean updateBuffer(Graphics2D g2) {
		return false;
	}

}
