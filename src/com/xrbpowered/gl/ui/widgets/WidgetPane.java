package com.xrbpowered.gl.ui.widgets;

import java.awt.Graphics2D;

import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.ui.UIPane;

public class WidgetPane extends UIPane {

	private boolean dirty = false; 
	private WidgetBox root = null;
	
	public final WidgetManager manager;
	
	public WidgetPane(WidgetManager manager, int x, int y, int w, int h, boolean staticBuffers) {
		super(manager.ui);
		this.manager = manager;
		manager.add(this);
		setTexture(new BufferTexture(w, h, false, false, staticBuffers) {
			@Override
			protected boolean updateBuffer(Graphics2D g2, int w, int h) {
				return updateBufferTexture(g2, w, h);
			}
		});
		setAnchor(x, y);
	}
	
	public void requestRepaint() {
		dirty = true;
	}
	
	public void setRoot(WidgetBox root, boolean fill) {
		this.root = root;
		if(root!=null) {
			root.setPane(this);
			if(fill) {
				root.setPosition(0, 0);
				root.setSize(getWidth(), getHeight());
			}
		}
		dirty = true;
	}
	
	public WidgetBox getRoot() {
		return root;
	}
	
	private boolean updateBufferTexture(Graphics2D g2, int w, int h) {
		BufferTexture.clearBuffer(g2, w, h);
		if(root!=null)
			root.paint(g2);
		dirty = false;
		return true;
	}
	
	@Override
	public void draw() {
		if(dirty)
			repaint();
		super.draw();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		manager.remove(this);
	}
}
