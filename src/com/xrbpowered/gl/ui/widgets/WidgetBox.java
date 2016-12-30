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
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class WidgetBox extends Widget {

	protected ArrayList<Widget> children = new ArrayList<>();

	public WidgetBox(WidgetBox parent) {
		super(parent);
	}
	
	@Override
	protected void setPane(WidgetPane pane) {
		for(Widget w : children) {
			w.setPane(pane);
		}
		super.setPane(pane);
	}
	
	protected void addChild(Widget child) {
//		child.setPane(pane);
		children.add(child);
	}
	
	public boolean hasChildren() {
		return children.size()>0;
	}
	
	@Override
	public Widget setSize(int width, int height) {
		super.setSize(width, height);
		layoutChildren(width, height);
		return this;
	}
	
	protected void layoutChildren(int width, int height) {
	}
	
	protected void paintChildren(Graphics2D g2) {
		AffineTransform t = g2.getTransform();
		for(Widget w : children) {
			g2.setTransform(t);
			g2.translate(w.getX(), w.getY());
			w.paint(g2);
		}
		g2.setTransform(t);
	}
	
	@Override
	public void paint(Graphics2D g2) {
		super.paint(g2);
		paintChildren(g2);
	}
	
	@Override
	public Widget topWidget(int x, int y) {
		if(!isEnabled())
			return null;
		for(Widget w : children) {
			Widget top = w.topWidget(x - w.getX(), y - w.getY());
			if(top!=null)
				return top;
		}
		return super.topWidget(x, y);
	}
}
