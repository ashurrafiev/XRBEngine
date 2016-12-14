package com.xrbpowered.gl.examples;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.examples.ExampleClient;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.FileAssetManager;

public class GLFractal extends ExampleClient {

	private OffscreenBuffers buffer = null;
	private PostProcessShader shader;
	
	private Texture palette;

	private int multisample = 1;
	private float pivotx = 0f;
	private float pivoty = 0f;
	private float zoom = 1f;
	private int maxi = 512;
	
	private boolean redraw = true;
	
	public GLFractal() {
		settings.multisample = 0;
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		init("Life").run();
	}
	
	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"Hold <b>LMB</b>|Zoom in",
				"Hold <b>RMB</b>|Zoom out",
				"Hold <b>MMB</b>|Pan view",
				"<b>1</b>|Reduce palette steps",
				"<b>2</b>|Increase palette steps",
				"<b>F4</b>|Toggle multisampled post-processing",
				"<b>F5</b>|Reset view",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		palette = new Texture("palette.png");
		shader = new PostProcessShader("post_fractal_f.glsl") {
			private int aspectLocation;
			private int pivotLocation;
			private int zoomLocation;
			private int maxiLocation;
			@Override
			protected void storeUniformLocations() {
				super.storeUniformLocations();
				aspectLocation = GL20.glGetUniformLocation(pId, "aspect");
				pivotLocation = GL20.glGetUniformLocation(pId, "pivot");
				zoomLocation = GL20.glGetUniformLocation(pId, "zoom");
				maxiLocation = GL20.glGetUniformLocation(pId, "maxi");
				GL20.glUseProgram(pId);
				GL20.glUniform1i(GL20.glGetUniformLocation(pId, "palette"), 0);
				GL20.glUseProgram(0);
			}
			@Override
			public void updateUniforms() {
				super.updateUniforms();
				GL20.glUniform1f(aspectLocation, (float)Display.getWidth()/(float)Display.getHeight());
				GL20.glUniform2f(pivotLocation, pivotx, pivoty);
				GL20.glUniform1f(zoomLocation, zoom);
				GL20.glUniform1i(maxiLocation, maxi);
			}
		};
		uiDebugPane.setVisible(false);
		resetBuffer();
	}
	
	private void resetBuffer() {
		if(buffer!=null)
			buffer.destroy();
		buffer = new OffscreenBuffers(Display.getWidth()*multisample, Display.getHeight()*multisample, false);
		redraw = true;
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
	protected void keyDown(int key) {
		switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_F5:
				pivotx = 0f;
				pivoty = 0f;
				zoom = 1f;
				redraw = true;
				break;
			case Keyboard.KEY_F4:
				multisample = (multisample==1) ? 2 : 1;
				resetBuffer();
				break;
			case Keyboard.KEY_1:
				if(maxi>64)
					maxi = maxi / 2;
				redraw = true;
				break;
			case Keyboard.KEY_2:
				if(maxi<8*1024)
					maxi = maxi * 2;
				redraw = true;
				break;
			default:
				super.keyDown(key);
				break;
		}
	}
	
	@Override
	protected void updateControllers(float dt) {
		int button = -1;
		if(Mouse.isButtonDown(0))
			button = 0;
		else if(Mouse.isButtonDown(1))
			button = 1;
		else if(Mouse.isButtonDown(2))
			button = 2;
		if(button>=0) {
			float x = (float)Mouse.getX()/(float)Display.getHeight()*2f - (float)Display.getWidth()/(float)Display.getHeight();
			float y = (float)Mouse.getY()/(float)Display.getHeight()*2f - 1f;
			if(button==2) {
				pivotx += 0.05*x*zoom;
				pivoty += 0.05*y*zoom;
				redraw = true;
			}
			else {
				float zoom2 = zoom;
				if(button==0)
					zoom2 *= 0.99f;
				else if(button==1)
					zoom2 /= 0.99f;
				if(zoom2<0.000025f)
					zoom2 = 0.000025f;
				pivotx += x*(zoom-zoom2);
				pivoty += y*(zoom-zoom2);
				zoom = zoom2;
				redraw = true;
			}
			uiDebugInfo = String.format("%f", zoom);
		}
	}
	
	@Override
	protected void drawObjects(RenderTarget target, float dt) {
		if(redraw) {
			palette.bind(0);
			buffer.use();
			shader.draw(null, dt);
			redraw = false;
		}
		OffscreenBuffers.blit(buffer, target, true);
	}
	
	public static void main(String[] args) {
		new GLFractal();
	}

}
