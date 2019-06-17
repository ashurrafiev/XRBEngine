package com.xrbpowered.gl.ui.widgets;

public class Button extends Widget {

	public static WidgetPainter<Button> painter = null;
	
	private String caption;
	
	public Button(WidgetBox parent, String caption) {
		super(parent);
		this.caption = caption;
	}
	
	public String getCaption() {
		return caption;
	}
	
	@Override
	public boolean isInteractive() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Widget> WidgetPainter<T> getPainter() {
		return (WidgetPainter<T>) painter;
	}

}
