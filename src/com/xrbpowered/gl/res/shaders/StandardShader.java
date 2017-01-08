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
package com.xrbpowered.gl.res.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.scene.ShaderEnvironment;

public class StandardShader extends ActorShader {
	
	public static final VertexInfo standardVertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Normal", 3)
			.addAttrib("in_Tangent", 3)
			.addAttrib("in_TexCoord", 2);
	
	public static class StandardShaderEnvironment  extends ShaderEnvironment<StandardShader> {
		public Vector3f lightDir = new Vector3f(0, 0, 1);
		public Vector4f lightColor = new Vector4f(1, 1, 1, 1);
		public Vector4f ambientColor = new Vector4f(0, 0, 0, 1);
		public float time = 0f;
		
		public void setPointLights(int n, Vector3f[] positions, Vector4f[] colors, Vector3f[] att) {
			for(StandardShader shader : shaders)
				shader.setPointLights(n, positions, colors, att);
		}
		
		public void setFog(float near, float far, Vector4f color) {
			for(StandardShader shader : shaders)
				shader.setFog(near, far, color);
		}
	};
	public static final StandardShaderEnvironment environment = new StandardShaderEnvironment();
	
	public static final String[] SAMLER_NAMES = {"texDiffuse", "texSpecular", "texNormal"};
	
	public float specPower = 20f;
	public float alpha = 1f;
	
	private int normalMatrixLocation;
	private int lightDirLocation;
	private int lightColorLocation;
	private int ambientColorLocation;
	private int specPowerLocation;
	private int alphaLocation;
	private int timeLocation;

	private StandardShader() {
		super(standardVertexInfo, "std_v.glsl", "std_f.glsl");
	}
	
	protected StandardShader(String pathVS, String pathFS) {
		super(standardVertexInfo, pathVS, pathFS);
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		normalMatrixLocation = GL20.glGetUniformLocation(pId, "normalMatrix");
		lightDirLocation = GL20.glGetUniformLocation(pId, "lightDirection");
		lightColorLocation = GL20.glGetUniformLocation(pId, "lightColor");
		ambientColorLocation = GL20.glGetUniformLocation(pId, "ambientColor");
		specPowerLocation = GL20.glGetUniformLocation(pId, "specPower");
		alphaLocation = GL20.glGetUniformLocation(pId, "alpha");
		timeLocation = GL20.glGetUniformLocation(pId, "time");
		initSamplers(SAMLER_NAMES);
	}
	
	private void setPointLights(int n, Vector3f[] positions, Vector4f[] colors, Vector3f[] att) {
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "numPointLights"), n);
		for(int i=0; i<n; i++) {
			String s = String.format("pointLights[%d].", i);
			uniform(GL20.glGetUniformLocation(pId, s+"position"), positions[i]);
			uniform(GL20.glGetUniformLocation(pId, s+"att"), att[i]);
			uniform(GL20.glGetUniformLocation(pId, s+"color"), colors[i]);
		}
		GL20.glUseProgram(0);
	}
	
	private void setFog(float near, float far, Vector4f color) {
		GL20.glUseProgram(pId);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), near);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), far);
		uniform(GL20.glGetUniformLocation(pId, "fogColor"), color);
		GL20.glUseProgram(0);
	}
	
	@Override
	public void updateUniforms() {
		super.updateUniforms();
		
		Matrix3f norm = new Matrix3f();
		Matrix4f model = new Matrix4f(getActor().getTransform());
		Matrix4f.mul(getActor().scene.activeCamera.getView(), model, model);
		norm.m00 = model.m00;
		norm.m01 = model.m01;
		norm.m02 = model.m02;
		norm.m10 = model.m10;
		norm.m11 = model.m11;
		norm.m12 = model.m12;
		norm.m20 = model.m20;
		norm.m21 = model.m21;
		norm.m22 = model.m22;
		Matrix3f.invert(norm, norm);
		Matrix3f.transpose(norm, norm);
		uniform(normalMatrixLocation, norm);
		
		uniform(lightDirLocation, environment.lightDir);
		uniform(lightColorLocation, environment.lightColor);
		uniform(ambientColorLocation, environment.ambientColor);
		
		GL20.glUniform1f(specPowerLocation, specPower);
		GL20.glUniform1f(alphaLocation, alpha);
		GL20.glUniform1f(timeLocation, environment.time);
	}
	
	private static StandardShader instance = null;
	
	public static StandardShader getInstance() {
		if(instance==null) {
			instance = new StandardShader();
			environment.addShader(instance);
		}
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.destroy();
			environment.removeShader(instance);
			instance = null;
		}
	}
	

}