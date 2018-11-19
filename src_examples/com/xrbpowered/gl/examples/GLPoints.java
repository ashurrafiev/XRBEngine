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

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.xrbpowered.gl.Client;
import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.FeedbackVertexInfo;
import com.xrbpowered.gl.res.shaders.FeedbackVertices;
import com.xrbpowered.gl.res.shaders.Shader;
import com.xrbpowered.gl.res.shaders.VertexInfo;

public class GLPoints extends ExampleClient {

	private static final int NUM_POINTS = 10000;
	private static final float POINTS_RANGE = 8f;

	private FeedbackVertices points;
	private Shader pointsTShader;
	private Shader pointsRShader;

	private StaticMesh simplePoints;
	private Shader simplePointsShader;

	private float[] pointData;
	private FloatBuffer feedbackData;
	private Integer[] indexData;
	
	private boolean simpleMode = false;
	
	public GLPoints() {
		CLEAR_COLOR = new Color(0.2f, 0.3f, 0.6f);
	}
	
	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>W</b> / <b>A</b> / <b>S</b> / <b>D</b>|Fly around",
				"<b>SPACE</b> / <b>LSHIFT</b>|Fly up/down",
				"Hold <b>LMB</b>|Mouse look",
				"<b>F1</b>|Toggle FPS limit and VSync",
				"<b>F2</b>|Change depth-sorting mode",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();

		FeedbackVertexInfo tInfo = (FeedbackVertexInfo) new FeedbackVertexInfo()
				.setFeedbackNames(new String[] {"out_Position", "out_Size"})
				.addAttrib("in_Position", 3).addAttrib("in_Size", 1);
		VertexInfo rInfo = new VertexInfo().addAttrib("in_Position", 4).addAttrib("in_Size", 1);
		VertexInfo simpleInfo = new VertexInfo().addAttrib("in_Position", 3).addAttrib("in_Size", 1);
		
		pointData = new float [NUM_POINTS*4];
		indexData = new Integer[NUM_POINTS];
		Random random = new Random();
		int offs = 0;
		for(int i=0; i<NUM_POINTS; i++) {
			pointData[offs++] = random.nextFloat() * 2f * POINTS_RANGE - POINTS_RANGE;
			pointData[offs++] = random.nextFloat() * 2f * POINTS_RANGE - POINTS_RANGE;
			pointData[offs++] = random.nextFloat() * 2f * POINTS_RANGE - POINTS_RANGE;
			pointData[offs++] = 0.1f;
		}
		// points = new StaticMesh(info, pointData, GL11.GL_POINTS, NUM_POINTS, true);
		
		pointsTShader = new Shader(tInfo, "points_tv.glsl", null) {
			private int projectionMatrixLocation;
			private int viewMatrixLocation;
			private int screenHeightLocation;
			@Override
			protected void storeUniformLocations() {
				projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
				viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
				screenHeightLocation  = GL20.glGetUniformLocation(pId, "screenHeight");
			}
			@Override
			public void updateUniforms() {
				uniform(projectionMatrixLocation, scene.activeCamera.getProjection());
				uniform(viewMatrixLocation, scene.activeCamera.getView());
				GL20.glUniform1f(screenHeightLocation, getTargetHeight());
			}
		};
		pointsRShader = new Shader(rInfo, "points_pv.glsl", "points_f.glsl") {
			@Override
			protected void storeUniformLocations() {
			}
			@Override
			public void updateUniforms() {
				GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
			@Override
			public void unuse() {
				GL11.glDisable(GL11.GL_BLEND);
				super.unuse();
			}
		};

		points = new FeedbackVertices(pointsTShader, pointsRShader, NUM_POINTS, true);
		points.updateVertexData(pointData);
		
		simplePointsShader = new Shader(simpleInfo, "points_v.glsl", "points_f.glsl") {
			private int projectionMatrixLocation;
			private int viewMatrixLocation;
			private int screenHeightLocation;
			@Override
			protected void storeUniformLocations() {
				projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
				viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
				screenHeightLocation  = GL20.glGetUniformLocation(pId, "screenHeight");
			}
			@Override
			public void updateUniforms() {
				GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				uniform(projectionMatrixLocation, scene.activeCamera.getProjection());
				uniform(viewMatrixLocation, scene.activeCamera.getView());
				GL20.glUniform1f(screenHeightLocation, getTargetHeight());
			}
			@Override
			public void unuse() {
				GL11.glDisable(GL11.GL_BLEND);
				super.unuse();
			}
		};
		simplePoints = new StaticMesh(simpleInfo, pointData, 1, NUM_POINTS, false);
		
		Client.checkError();
		updateInfo();
	}
	
	private void updateInfo() {
		uiDebugTitle = NUM_POINTS + " point sprites";
		uiDebugInfo = simpleMode ? "Depth-sort disabled" : "TF depth-sort";
		uiDebugPane.repaint();
	}
	
	@Override
	protected void keyDown(int key) {
		switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_F2:
				simpleMode = !simpleMode;
				updateInfo();
				break;
			default:
				super.keyDown(key);
		}
	}
	
	@Override
	protected void drawObjects(RenderTarget target) {
		if(simpleMode) {
			simplePointsShader.use();
			simplePoints.draw();
			simplePointsShader.unuse();
		}
		else {
			points.transform();
			Client.checkError();

			feedbackData = points.getFeedbackData();
			for(int i=0; i<NUM_POINTS; i++)
				indexData[i] = i;
			Arrays.sort(indexData, new Comparator<Integer>() {
				private float depth(int index) {
					return feedbackData.get(index*5+2);
				}
				@Override
				public int compare(Integer o1, Integer o2) {
					return Float.compare(depth(o2), depth(o1));
				}
			});
			points.updateIndexData(indexData);
			
			points.draw();
		}

		Client.checkError();
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		// TODO points.destroy();
		simplePoints.destroy();
		simplePointsShader.destroy();
		pointsTShader.destroy();
		pointsRShader.destroy();
	}

	public static void main(String[] args) {
		settings.multisample = 0;
		new GLPoints().init("GLExample").run();
	}

}
