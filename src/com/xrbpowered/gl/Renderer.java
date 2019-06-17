package com.xrbpowered.gl;

import com.xrbpowered.gl.res.buffers.RenderTarget;

public interface Renderer {
	public void updateTime(float dt);
	public void redraw(RenderTarget target);
}
