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
package com.xrbpowered.gl.ui.widgets.menu;

import com.xrbpowered.gl.ui.UIPage;
import com.xrbpowered.gl.ui.UIPages;
import com.xrbpowered.gl.ui.widgets.Widget;
import com.xrbpowered.gl.ui.widgets.WidgetBox;
import com.xrbpowered.gl.ui.widgets.WidgetManager;
import com.xrbpowered.gl.ui.widgets.WidgetPane;

public class WidgetMenuBuilder {

	protected final WidgetManager manager;
	protected final UIPages pages;
	
	public int itemHeight = 20;
	public int itemPadding = 2;
	public int buttonHeight = 30;
	public int margin = 0;
	
	private int width, captionWidth;
	private int y;
	private boolean addPadding;
	private WidgetBox root;
	
	public WidgetMenuBuilder(WidgetManager manager, UIPages pages) {
		this.manager = manager;
		this.pages = pages;
	}
	
	public void startPage(int width, int captionWidth) {
		this.width = width;
		this.captionWidth = captionWidth;
		y = margin;
		addPadding = false;
		root = createRoot();
	}
	
	public int getWidth() {
		return width;
	}

	protected WidgetBox createRoot() {
		return new WidgetBox(null);
	}
	
	public WidgetBox getPageRoot() {
		return root;
	}
	
	public void addBlank(int dy) {
		y += dy;
		addPadding = false;
	}
	
	public Widget addWidget(Widget w) {
		w.setPosition(0, y);
		y += w.getHeight();
		addPadding = false;
		return w;
	}

	public MenuItem addMenuItem(MenuItem w) {
		if(addPadding)
			y += itemPadding;
		w.captionWidth = captionWidth;
		w.setPosition(margin, y);
		w.setSize(width, 0);
		y += w.getHeight();
		addPadding = true;
		return w;
	}
	
	public MenuItem addPageItem(String caption, final UIPage page, int style) {
		return addMenuItem(new MenuItem(root, caption, style) {
			@Override
			public void onMouseDown(int x, int y, int button) {
				pages.push(page);
			}
		});
	}

	public MenuItem addCancelItem(String caption, int style) {
		return addMenuItem(new MenuItem(root, caption, style) {
			@Override
			public void onMouseDown(int x, int y, int button) {
				pages.pop();
			}
		});
	}
	
	public WidgetPane finishPage() {
		WidgetPane pane = new WidgetPane(manager, 0, 0, margin*2+width, y+margin, true) {
			@Override
			public void layout(int screenWidth, int screenHeight) {
				center();
			}
		};
		pane.setRoot(root, true);
		pane.setVisible(false);
		return pane;
	}
	
}
