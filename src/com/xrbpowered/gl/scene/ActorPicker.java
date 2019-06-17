package com.xrbpowered.gl.scene;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.res.buffers.RenderTarget;

public class ActorPicker {

	private static final int[] ATTRIB_MASK = {0};
	
	private int x, y;
	private ByteBuffer pixels = ByteBuffer.allocateDirect(4);
	
	public void startPicking(int x, int y, RenderTarget pickTarget) {
		this.x = x;
		this.y = y;
		pickTarget.use();
		GL11.glScissor(x, y, 1, 1);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glClearColor(0f, 0f, 0f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		ActorPickerShader.getInstance().use();
	}
	
	public void drawActor(StaticMeshActor actor, int objId) {
		ActorPickerShader.getInstance().updateUniforms(actor, objId);
		actor.getMesh().draw(ATTRIB_MASK);
	}
	
	public int finishPicking(RenderTarget nextTarget) {
		GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		nextTarget.use();
		return pixels.asIntBuffer().get(0) >> 8;
	}
	
	public static final ActorPicker instance = new ActorPicker();
}
