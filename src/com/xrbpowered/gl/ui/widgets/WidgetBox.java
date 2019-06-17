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
