package com.xrbpowered.gl.ui.widgets.menu;

import com.xrbpowered.gl.ui.widgets.Widget;
import com.xrbpowered.gl.ui.widgets.WidgetBox;
import com.xrbpowered.gl.ui.widgets.WidgetPainter;

public class MenuItem extends WidgetBox {

	public static WidgetPainter<MenuItem> painter = null;
	
	public int style;
	public int captionWidth;
	public String caption;
	
	public MenuItem(WidgetBox parent, String caption, int style) {
		super(parent);
		this.caption = caption;
		this.style = style;
	}
	
	@Override
	public boolean isInteractive() {
		return true;
	}
	
	@Override
	public boolean isHover() {
		if(super.isHover())
			return true;
		for(Widget w : children) {
			if(w.isHover())
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Widget> WidgetPainter<T> getPainter() {
		return (WidgetPainter<T>) painter;
	}
}
