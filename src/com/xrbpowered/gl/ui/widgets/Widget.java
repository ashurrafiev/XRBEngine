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

public class Widget {

	private int x, y, width, height;
	
	public final WidgetBox parent;
	private WidgetPane pane = null;
	
	private boolean enabled = true;
	
	public Widget(WidgetBox parent) {
		this.parent = parent;
		if(parent!=null)
			parent.addChild(this);
		setSize(0, 0);
	}
	
	protected void setPane(WidgetPane pane) {
		this.pane = pane;
	}
	
	public WidgetPane getPane() {
		return pane;
	}
	
	public Widget setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Widget setSize(int width, int height) {
		this.width = width>0 ? width : (getPainter()==null ? 0 : getPainter().getDefaultWidth(this));
		this.height = height>0 ? height : (getPainter()==null ? 0 : getPainter().getDefaultHeight(this));
		return this;
	}
	
	public Widget setEnabled(boolean enabled) {
		this.enabled = enabled;
		requestRepaint();
		if(pane!=null && isHover() && !enabled)
			pane.manager.unhover();
		return this;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isInteractive() {
		return false;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	protected void requestRepaint() {
		if(pane!=null)
			pane.requestRepaint();
	}
	
	protected <T extends Widget> WidgetPainter<T> getPainter() {
		return null;
	}
	
	public void paint(Graphics2D g2) {
		if(getPainter()!=null)
			getPainter().paint(g2, this);
	}
	
	public boolean contains(int x, int y) {
		return x>=0 && x<=width && y>=0 && y<=height;
	}
	
	public Widget topWidget(int x, int y) {
		return isInteractive() && isEnabled() && contains(x, y) ? this : null;
	}
	
	public boolean isHover() {
		return pane.manager.getHover()==this;
	}
	
	public int toLocalX(int x) {
		x -= this.x;
		if(parent!=null)
			return parent.toLocalX(x);
		else
			return x - (int)pane.x;
	}

	public int toLocalY(int y) {
		y -= this.y;
		if(parent!=null)
			return parent.toLocalY(y);
		else
			return y - (int)pane.y;
	}

	public void onMouseIn() {
	}
	
	public void onMouseOut() {
	}
	
	public void onMouseMove(int x, int y) {
	}
	
	public void onMouseDown(int x, int y, int button) {
	}
	
	public void onMouseUp(int x, int y, int button) {
	}
	
}
