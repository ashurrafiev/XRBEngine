package com.xrbpowered.gl.ui.widgets;

import java.awt.Graphics2D;

public interface WidgetPainter<T extends Widget> {

	public void paint(Graphics2D g2, T w);
	public int getDefaultWidth(T w);
	public int getDefaultHeight(T w);
	
}
