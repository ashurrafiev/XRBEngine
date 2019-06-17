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
