package com.xrbpowered.gl.ui;

import java.awt.Graphics2D;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.Scene;

public class UIPointerActor extends Actor {

	private class PointerTexture extends BufferTexture {
		public PointerTexture(int w, int h, boolean staticBuffers) {
			super(w, h, false, false, staticBuffers);
		}
		@Override
		protected boolean updateBuffer(Graphics2D g2, int w, int h) {
			return UIPointerActor.this.updateBuffer(g2);
		}
	}
	
	public final UIPane pane;
	public int pivotx, pivoty;
	
	public float dist;
	public float maxDist = -1f;
	
	public boolean visible = true;
	
	public UIPointerActor(UIManager ui, Scene scene, int w, int h, boolean staticBuffers) {
		super(scene);
		pane = new UIPane(ui, new PointerTexture(w, h, staticBuffers));
		pivotx = w/2;
		pivoty = h/2;
	}
	
	public UIPointerActor(UIManager ui, Scene scene, BufferTexture texture) {
		super(scene);
		pane = new UIPane(ui, texture);
		pivotx = texture.getWidth()/2;
		pivoty = texture.getHeight()/2;
	}
	
	public void updateView() {
		dist = scene.activeCamera.getDistTo(this);
		Vector4f p = calcViewPos(null);
		pane.x = (p.x+1f)*(float)Display.getWidth()/2f - pivotx;
		pane.y = (1f-p.y)*(float)Display.getHeight()/2f - pivoty;
		pane.setVisible(visible && (p.x>=-1f && p.x<=1f) && (p.y>=-1f && p.y<=1f) && (p.z>0 && (maxDist<0f || dist<=maxDist)));
	}
	
	protected boolean updateBuffer(Graphics2D g2) {
		return false;
	}

}
