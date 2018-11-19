/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Ashur Rafiev
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

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.FileAssetManager;

public class GLPerlin extends ExampleClient {

	private OffscreenBuffers buffer = null;
	
	private PostProcessShader shader;

	private float pivotx = 123f;
	private float pivoty = 345f;
	private int seed = 0;

	public GLPerlin() {
		settings.multisample = 0;
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		init("Perlin Noise").run();
	}
	
	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>F1</b>|Toggle FPS limit and VSync",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		shader = new PostProcessShader("post_noise_f.glsl") {
			private int pivotLocation;
			private int scaleLocation;
			private int seedLocation;
			@Override
			protected void storeUniformLocations() {
				super.storeUniformLocations();
				pivotLocation = GL20.glGetUniformLocation(pId, "pivot");
				scaleLocation = GL20.glGetUniformLocation(pId, "scale");
				seedLocation = GL20.glGetUniformLocation(pId, "seed");
				GL20.glUseProgram(pId);
				GL20.glUniform1i(GL20.glGetUniformLocation(pId, "palette"), 0);
				GL20.glUseProgram(0);
			}
			@Override
			public void updateUniforms() {
				super.updateUniforms();
				GL20.glUniform2f(pivotLocation, pivotx, pivoty);
				GL20.glUniform2f(scaleLocation, buffer.getWidth()/2, buffer.getHeight()/2);
				GL20.glUniform1i(seedLocation, seed);
			}
		};
		resetBuffer();
	}
	
	private void resetBuffer() {
		if(buffer!=null)
			buffer.destroy();
		buffer = new OffscreenBuffers(getTargetWidth(), getTargetHeight(), false);
	}
	
	@Override
	protected void resizeResources() {
		super.resizeResources();
		resetBuffer();
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		shader.destroy();
	}
	
	@Override
	public void updateTime(float dt) {
		pivoty += 20f*dt;
		pivotx += 30f*dt;
	}
	
	@Override
	protected void drawObjects(RenderTarget target) {
		buffer.use();
		shader.draw(null);
		OffscreenBuffers.blit(buffer, target, true);
	}
	
	public static void main(String[] args) {
		new GLPerlin();
	}


}
