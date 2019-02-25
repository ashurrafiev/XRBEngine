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

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.Client;
import com.xrbpowered.utils.assets.AssetManager;

public abstract class Shader {

	public final VertexInfo info;
	protected int pId;
	
	protected Shader(VertexInfo info) {
		this.info = info;
	}
	
	public Shader(VertexInfo info, String pathVS, String pathFS) {
//		System.out.println("Compile: "+pathVS+", "+pathFS);
		this.info = info;
		int vsId = loadShader(pathVS, GL20.GL_VERTEX_SHADER);
		int fsId = loadShader(pathFS, GL20.GL_FRAGMENT_SHADER);

		pId = GL20.glCreateProgram();
		if(vsId>0)
			GL20.glAttachShader(pId, vsId);
		if(fsId>0)
			GL20.glAttachShader(pId, fsId);
		
		bindAttribLocations();
		
//		System.out.println("Link: "+pathVS+", "+pathFS);
		GL20.glLinkProgram(pId);
		if (GL20.glGetProgrami(pId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not link program "+pathVS+", "+pathFS);
			System.err.println(GL20.glGetProgramInfoLog(pId, 8000));
			System.exit(-1);
		}
		GL20.glValidateProgram(pId);
		
		storeUniformLocations();
		Client.checkError();
//		System.out.println("Done: "+pathVS+", "+pathFS+"\n");
	}
	
	protected abstract void storeUniformLocations();
	public abstract void updateUniforms(); 
	
	public int getProgramId() {
		return pId;
	}
	
	public void use() {
		GL20.glUseProgram(pId);
		updateUniforms();
	}
	
	public void unuse() {
		GL20.glUseProgram(0);
	}
	
	public void destroy() {
		GL20.glUseProgram(0);
		GL20.glDeleteProgram(pId);
	}
	
	protected int bindAttribLocations() {
		return (info==null) ? 0 : info.bindAttribLocations(pId);
	}
	
	protected void initSamplers(String[] names) {
		GL20.glUseProgram(pId);
		for(int i=0; i<names.length; i++) {
			GL20.glUniform1i(GL20.glGetUniformLocation(pId, names[i]), i);
		}
		GL20.glUseProgram(0);
	}
	
	public static int loadShader(String path, int type) {
		if(path==null)
			return 0;
		int shaderId = 0;
		String shaderSource;
		try {
			shaderSource = AssetManager.defaultAssets.loadString(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		shaderId = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderId, shaderSource);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader "+path);
			System.err.println(GL20.glGetShaderInfoLog(shaderId, 8000));
			System.exit(-1); // FIXME handle this exception!!
		}
		
		return shaderId;
	}
	
	private static final FloatBuffer matrix4Buffer = BufferUtils.createFloatBuffer(16);
	protected static void uniform(int location, Matrix4f matrix) {
		matrix.store(matrix4Buffer);
		matrix4Buffer.flip();
		GL20.glUniformMatrix4(location, false, matrix4Buffer);
	}

	private static final FloatBuffer matrix3Buffer = BufferUtils.createFloatBuffer(9);
	protected static void uniform(int location, Matrix3f matrix) {
		matrix.store(matrix3Buffer);
		matrix3Buffer.flip();
		GL20.glUniformMatrix3(location, false, matrix3Buffer);
	}

	private static final FloatBuffer vec4Buffer = BufferUtils.createFloatBuffer(4);
	protected static void uniform(int location, Vector4f v) {
		v.store(vec4Buffer);
		vec4Buffer.flip();
		GL20.glUniform4(location, vec4Buffer);
	}

	private static final FloatBuffer vec3Buffer = BufferUtils.createFloatBuffer(3);
	protected static void uniform(int location, Vector3f v) {
		v.store(vec3Buffer);
		vec3Buffer.flip();
		GL20.glUniform3(location, vec3Buffer);
	}

	private static final FloatBuffer vec2Buffer = BufferUtils.createFloatBuffer(2);
	protected static void uniform(int location, Vector2f v) {
		v.store(vec2Buffer);
		vec2Buffer.flip();
		GL20.glUniform2(location, vec2Buffer);
	}

}
