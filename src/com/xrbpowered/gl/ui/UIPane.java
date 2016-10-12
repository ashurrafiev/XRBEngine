package com.xrbpowered.gl.ui;

import org.lwjgl.opengl.Display;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;

public class UIPane implements UIPage {

	private StaticMesh quad = null;
	protected Texture texture = null;
	
	public float x = 0f;
	public float y = 0f;
	public float alpha = 1f;
	
	private boolean visible = true;
	public UIPage parentPage = null;
	
	private final UIManager ui;
	
	protected UIPane(UIManager ui) {
		this.ui = ui;
		ui.panes.add(this);
	}
	
	public UIPane(UIManager ui, Texture texture) {
		this.ui = ui;
		ui.panes.add(this);
		setTexture(texture);
	}
	
	protected void setTexture(Texture texture) {
		if(this.texture!=null)
			this.texture.destroy();
		if(this.quad!=null)
			this.quad.destroy();
		
		this.texture = texture;
		int w = texture.getWidth();
		int h = texture.getHeight();
		this.quad = new StaticMesh(UIManager.uiVertexInfo, new float[] {
				0, 0, 0, 0,
				w, 0, 1, 0,
				w, h, 1, 1,
				0, h, 0, 1
		}, new short[] {
				0, 3, 2, 2, 1, 0
		});
	}
	
	public UIPane setAnchor(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public UIPane centerX() {
		this.x = (Display.getWidth()-getWidth())/2;
		return this;
	}

	public UIPane centerY() {
		this.y = (Display.getHeight()-getHeight())/2;
		return this;
	}
	
	public UIPane center() {
		return centerX().centerY();
	}

	public int getWidth() {
		return texture.getWidth();
	}

	public int getHeight() {
		return texture.getHeight();
	}
	
	@Override
	public boolean isVisible() {
		return parentPage==null ? visible : parentPage.isVisible() && visible;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public void layout(int screenWidth, int screenHeight) {
	}

	public void repaint() {
		if(texture instanceof BufferTexture)
			((BufferTexture) texture).update();
	}
	
	public void draw() {
		if(!isVisible() || alpha<=0f)
			return;
		UIShader shader = UIShader.getInstance();
		shader.updateUniforms(x, y, alpha);
		texture.bind(0);
		quad.draw();
	}
	
	public void destroy() {
		ui.panes.remove(this);
		quad.destroy();
		texture.destroy();
	}
	
}
