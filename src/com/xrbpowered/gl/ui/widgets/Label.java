package com.xrbpowered.gl.ui.widgets;

public class Label extends Widget {

	public static WidgetPainter<Label> painter = null;
	
	private String caption;
	private int style;
	
	public Label(WidgetBox parent, String caption, int style) {
		super(parent);
		this.caption = caption;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public int getStyle() {
		return style;
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
