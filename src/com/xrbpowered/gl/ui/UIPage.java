package com.xrbpowered.gl.ui;

public interface UIPage {

	public boolean isVisible();
	public void setVisible(boolean visible);
	public void layout(int screenWidth, int screenHeight);
	
}
