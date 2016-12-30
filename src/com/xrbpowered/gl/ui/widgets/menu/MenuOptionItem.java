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

import com.xrbpowered.gl.ui.widgets.Widget;
import com.xrbpowered.gl.ui.widgets.WidgetBox;
import com.xrbpowered.gl.ui.widgets.WidgetPainter;

public class MenuOptionItem extends MenuItem {

	public static WidgetPainter<MenuOptionItem> painter = null;

	private static final int ARROW_LEFT = -1;
	private static final int ARROW_RIGHT = 1;
	
	public class ArrowButton extends Widget {
		public final int direction;
		private ArrowButton(WidgetBox parent, int direction) {
			super(parent);
			this.direction = direction;
		}
		@Override
		public boolean isInteractive() {
			return true;
		}
		@Override
		public void onMouseDown(int x, int y, int button) {
			changeOption(direction);
		}
	}

	public class ValueBox extends Widget {
		private ValueBox(WidgetBox parent) {
			super(parent);
		}
	}

	private int selectedIndex = 0;
	private Object[] options;
	public String disableOverriveValue = null;
	
	public final ArrowButton left, right;
	public final ValueBox valueBox;
	
	public MenuOptionItem(WidgetBox parent, String caption, Object[] options, int selectedIndex, int style) {
		super(parent, caption, style);
		this.options = options;
		this.selectedIndex = selectedIndex;
		left = new ArrowButton(this, ARROW_LEFT);
		left.setSize(getHeight(), getHeight());
		right = new ArrowButton(this, ARROW_RIGHT);
		right.setSize(getHeight(), getHeight());
		valueBox = new ValueBox(this);
	}
	
	public void setOptions(Object[] options, int selectedIndex) {
		this.options = options;
		this.selectedIndex = selectedIndex;
		requestRepaint();
	}
	
	@Override
	protected void layoutChildren(int width, int height) {
		if(valueBox==null)
			return;
		left.setPosition(captionWidth, 0);
		right.setPosition(width-right.getWidth(), 0);
		valueBox.setSize(width-captionWidth-left.getWidth()-right.getWidth(), getHeight());
		valueBox.setPosition(captionWidth+left.getWidth(), 0);
	}
	
	public void onChangeValue(int index) {
	}
	
	public int changeOption(int direction) {
		selectedIndex = (selectedIndex+options.length+direction) % options.length;
		onChangeValue(selectedIndex);
		requestRepaint();
		return selectedIndex;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public String getValueName() {
		if(isEnabled() || disableOverriveValue==null)
			return options[selectedIndex].toString();
		else
			return disableOverriveValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Widget> WidgetPainter<T> getPainter() {
		return (WidgetPainter<T>) painter;
	}
	
}
