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

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.xrbpowered.gl.InputHandler;
import com.xrbpowered.gl.ui.UIManager;

public class WidgetManager implements InputHandler {

	protected final UIManager ui;
	
	private ArrayList<WidgetPane> panes = new ArrayList<>();
	
	private Widget hover = null;
	
	public WidgetManager(UIManager ui) {
		this.ui = ui;
	}
	
	protected void add(WidgetPane pane) {
		panes.add(pane);
	}
	
	protected void remove(WidgetPane pane) {
		panes.remove(pane);
	}
	
	public Widget getHover() {
		return hover;
	}
	
	public void unhover() {
		hover = null;
	}
	
	// TODO auto-align panes on window resize?
	
	@Override
	public void processInput(float dt) {
		while(Mouse.next()) {
			Widget h = null;
			for(WidgetPane pane : panes) {
				WidgetBox root = pane.getRoot();
				if(!pane.isVisible() || root==null)
					continue;
				int x = Mouse.getEventX() - (int)pane.x - root.getX();
				int y = (Display.getHeight()-Mouse.getEventY()) - (int)pane.y - root.getY();
				h = root.topWidget(x, y);
			}
			if(h!=hover) {
				if(hover!=null) {
					hover.onMouseOut();
					hover.requestRepaint();
				}
				hover = h;
				if(hover!=null) {
					hover.onMouseIn();
					hover.requestRepaint();
				}
			}
			if(hover!=null) {
				int button = Mouse.getEventButton();
				int x = hover.toLocalX(Mouse.getEventX());
				int y = hover.toLocalY(Display.getHeight()-Mouse.getEventY());
				if(button<0)
					hover.onMouseMove(x, y);
				else if(Mouse.getEventButtonState())
					hover.onMouseDown(x, y, button);
				else
					hover.onMouseUp(x, y, button);
			}
		}
	}
	
}
