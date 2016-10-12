package com.xrbpowered.gl.ui;

public class UIPaneSet implements UIPage {

	protected UIPane[]  panes;
	
	private boolean visible = true;
	
	public UIPaneSet(UIPane... panes) {
		this.panes = panes;
		for(UIPane pane : panes)
			pane.parentPage = this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public void layout(int screenWidth, int screenHeight) {
		for(UIPane pane : panes)
			pane.layout(screenWidth, screenHeight);
	}
}
