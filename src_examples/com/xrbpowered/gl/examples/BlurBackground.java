package com.xrbpowered.gl.examples;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.res.shaders.PostProcessRenderer;
import com.xrbpowered.gl.res.shaders.PostProcessShader;
import com.xrbpowered.utils.MathUtils;
import com.xrbpowered.utils.TweenUtils;

public class BlurBackground extends PostProcessRenderer {

	public static class BlurShader extends PostProcessShader {
		private float startMul = 1f;
		private long tweenStart = 0;
		private long tweenDuration = 0;
		
		private int mulColorLocation;
		private int addColorLocation;

		public BlurShader() {
			super("post_blur_f.glsl");
		}
		
		@Override
		protected void storeUniformLocations() {
			super.storeUniformLocations();
			mulColorLocation = GL20.glGetUniformLocation(pId, "mulColor");
			addColorLocation = GL20.glGetUniformLocation(pId, "addColor");
		}
		
		@Override
		public void updateUniforms() {
			super.updateUniforms();
			long t = Sys.getTime();
			float mul, add;
			if(t<tweenStart+tweenDuration) {
				float s = (float) TweenUtils.easeIn(TweenUtils.tween(t, tweenStart, tweenDuration));
				mul = MathUtils.lerp(startMul, 0.4f, s);
				add = 0f;
			}
			else {
				mul = 0.4f;
				add = 0f;
			}
			GL20.glUniform4f(mulColorLocation, mul, mul, mul, 1f);
			GL20.glUniform4f(addColorLocation, add, add, add, 1f);
		}
	}
	
	public BlurBackground(Renderer parent) {
		super(parent, new BlurShader(), false);
	}
	
	@Override
	public void resizeBuffers() {
		int wscaled = (int)((float)Display.getWidth()*120f/(float)Display.getHeight());
		resizeBuffers(wscaled, 120, wscaled, 120);
	}
	
	public void startTween(float startMul, float duration) {
		if(postProc==null)
			return;
		BlurShader blur = (BlurShader) postProc;
		blur.startMul = startMul;
		blur.tweenStart = Sys.getTime(); 
		blur.tweenDuration = (long)(duration*Sys.getTimerResolution());
	}

}
