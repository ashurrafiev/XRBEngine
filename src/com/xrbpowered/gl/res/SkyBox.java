package com.xrbpowered.gl.res;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shaders.Shader;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.res.textures.CubeTexture;
import com.xrbpowered.gl.scene.Scene;

public class SkyBox {

	private CubeTexture texture;
	private StaticMesh cube;
	private Shader shader;

	public SkyBox(final Scene scene, String texturePathFormat) {
		texture = new CubeTexture(texturePathFormat);
		
		VertexInfo info = new VertexInfo().addAttrib("in_Position", 3);

		shader = new Shader(info, "sky_v.glsl", "sky_f.glsl") {
			private int projectionMatrixLocation;
			private int viewMatrixLocation;
			@Override
			protected void storeUniformLocations() {
				projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
				viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
				GL11.glDepthMask(false);
			}
			@Override
			public void updateUniforms() {
				uniform(projectionMatrixLocation, scene.activeCamera.getProjection());
				uniform(viewMatrixLocation, scene.activeCamera.getFollowView());
				GL11.glDepthMask(true);
			}
		};
		
		cube = new StaticMesh(info, new float[] {
				-1.0f, 1.0f, -1.0f,
				-1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, -1.0f,

				-1.0f, -1.0f, 1.0f,
				-1.0f, -1.0f, -1.0f,
				-1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f,

				1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, -1.0f,

				-1.0f, -1.0f, 1.0f,
				-1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, -1.0f, 1.0f,
				-1.0f, -1.0f, 1.0f,

				-1.0f, 1.0f, -1.0f,
				1.0f, 1.0f, -1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				-1.0f, 1.0f, 1.0f,
				-1.0f, 1.0f, -1.0f,

				-1.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, 1.0f,
				1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, 1.0f,
				1.0f, -1.0f, 1.0f
		}, null);
	}

	public void draw() {
		shader.use();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture.getId());
		cube.draw();
		shader.unuse();
	}
	
}
