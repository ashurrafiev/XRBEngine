/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2016 Ashur Rafiev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.xrbpowered.gl.examples;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import com.xrbpowered.gl.examples.ExampleClient;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.FileAssetManager;

public class GLLife extends ExampleClient {

	private static final Random random = new Random();
	
	private OffscreenBuffers[] buffers = new OffscreenBuffers[2];
	private int targetBuffer = 1;
	
	private PostProcessShader shader;

	private int turn = 0;
	private int speed = 8;
	private boolean pause = false;

	public GLLife() {
		settings.multisample = 0;
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		init("Life").run();
	}
	
	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>SPACE</b>|Pause/unpause simulation",
				"<b>1</b>|Reduce simulation speed",
				"<b>2</b>|Increase simulation speed",
				"<b>F4</b>|Blank screen",
				"<b>F5</b>|Restart simulation by filling the screen with random noise",
				"<b>Q</b>|Spawn 4 gliders in the middle of the screen",
				"Left Click|Spawn 4 gliders at the cursor location",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		shader = new PostProcessShader("post_life_f.glsl");
		resetBuffers(true);
	}
	
	private void resetBuffers(boolean fill) {
		if(buffers[0]!=null)
			buffers[0].destroy();
		if(buffers[1]!=null)
			buffers[1].destroy();
		
		int width = Display.getWidth();
		int height = Display.getHeight();
		buffers[0] = new OffscreenBuffers(width, height, false);
		buffers[1] = new OffscreenBuffers(width, height, false);
		targetBuffer = 1;
		turn = 0;
		
		IntBuffer intBuffer = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder()).asIntBuffer();
		int[] pixels = new int[width * height];
		for(int x=1; x<width; x++)
			for(int y=1; y<height; y++) {
				int v = fill && random.nextInt(27)==0 ? 0xffffffff : 0xff000000;
				pixels[y*width + x] = v;
			}
		intBuffer.put(pixels);
		intBuffer.flip();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers[0].getColorTexId());
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers[1].getColorTexId());
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);
	}
	
	private static final int[][] GLIDER = {
		{
			0xffffffff, 0xffffffff, 0xff000000, 0xff000000,
			0xffffffff, 0xff000000, 0xffffffff, 0xff000000,
			0xffffffff, 0xff000000, 0xff000000, 0xff000000,
			0xff000000, 0xff000000, 0xff000000, 0xff000000
		},
		{
			0xff000000, 0xffffffff, 0xffffffff, 0xffffffff,
			0xff000000, 0xff000000, 0xff000000, 0xffffffff,
			0xff000000, 0xff000000, 0xffffffff, 0xff000000,
			0xff000000, 0xff000000, 0xff000000, 0xff000000
		},
		{
			0xff000000, 0xff000000, 0xff000000, 0xff000000,
			0xff000000, 0xff000000, 0xff000000, 0xffffffff,
			0xff000000, 0xffffffff, 0xff000000, 0xffffffff,
			0xff000000, 0xff000000, 0xffffffff, 0xffffffff
		},
		{
			0xff000000, 0xff000000, 0xff000000, 0xff000000,
			0xff000000, 0xffffffff, 0xff000000, 0xff000000,
			0xffffffff, 0xff000000, 0xff000000, 0xff000000,
			0xffffffff, 0xffffffff, 0xffffffff, 0xff000000
		},
	};
	
	private void addGlider(int x, int y, int width, int height, int[] pixels) {
		IntBuffer intBuffer = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder()).asIntBuffer();
		intBuffer.put(pixels);
		intBuffer.flip();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers[1-targetBuffer].getColorTexId());
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, x, y, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);
	}
	
	private void addGliders(int x, int y) {
		addGlider(x-5, y-5, 4, 4, GLIDER[3]);
		addGlider(x-5, y+1, 4, 4, GLIDER[2]);
		addGlider(x+1, y+1, 4, 4, GLIDER[1]);
		addGlider(x+1, y-5, 4, 4, GLIDER[0]);
	}
	
	@Override
	protected void resizeResources() {
		super.resizeResources();
		resetBuffers(true);
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		buffers[0].destroy();
		buffers[1].destroy();
		shader.destroy();
	}
	
	@Override
	protected void keyDown(int key) {
		switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_F4:
				resetBuffers(false);
				break;
			case Keyboard.KEY_F5:
				resetBuffers(true);
				break;
			case Keyboard.KEY_SPACE:
				pause = !pause;
				break;
			case Keyboard.KEY_1:
				if(speed>1)
					speed = speed / 2;
				break;
			case Keyboard.KEY_2:
				if(speed<16)
					speed = speed * 2;
				break;
			case Keyboard.KEY_Q:
				addGliders(Display.getWidth()/2, Display.getHeight()/2);
				break;
			default:
				super.keyDown(key);
				break;
		}
	}
	
	@Override
	protected void updateControllers(float dt) {
		while(Mouse.next()) {
			int button = Mouse.getEventButton();
			if(button==0 && Mouse.getEventButtonState()) {
				int x = Mouse.getEventX();
				int y = Mouse.getEventY();
				addGliders(x, y);
			}
		}
	}
	
	@Override
	protected void drawObjects(RenderTarget target, float dt) {
		if(!pause && dt>0f) {
			for(int step=0; step<speed; step++) {
				buffers[targetBuffer].use();
				shader.draw(buffers[1-targetBuffer], 0f);
				targetBuffer = 1-targetBuffer;
				turn++;
			}
			uiDebugInfo = String.format("Speed x%d  Turn: %d", speed, turn);
		}
		else {
			uiDebugInfo = String.format("PAUSE  Turn: %d", turn);
		}
		OffscreenBuffers.blit(buffers[targetBuffer], target, false);
	}
	
	public static void main(String[] args) {
		new GLLife();
	}

}
