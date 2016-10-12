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
