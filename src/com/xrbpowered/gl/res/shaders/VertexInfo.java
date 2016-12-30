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

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class VertexInfo {

	private int stride = 0;
	private List<String> name = new ArrayList<>();
	private List<Integer> elemCount = new ArrayList<>();
	private List<Integer> type = new ArrayList<>();
	private List<Boolean> norm = new ArrayList<>();
	private List<Integer> offset = new ArrayList<>();
	
	public VertexInfo addAttrib(String name, int elemCount, int glType, boolean norm) {
		int size = sizeOf(glType);
		if(size==0)
			return this;
		this.name.add(name);
		this.elemCount.add(elemCount);
		this.type.add(glType);
		this.norm.add(norm);
		this.offset.add(stride);
		stride += size * elemCount;
		return this;
	}

	public VertexInfo addFloatAttrib(String name, int elemCount) {
		return addAttrib(name, elemCount, GL11.GL_FLOAT, false);
	}

	public VertexInfo addFloatAttrib(String name, int elemCount, boolean norm) {
		return addAttrib(name, elemCount, GL11.GL_FLOAT, norm);
	}
	
	public int getStride() {
		return stride;
	}
	
	public int getAttributeCount() {
		return name.size();
	}
	
	public void initAttribPointers() {
		for(int i=0; i<name.size(); i++) {
			GL20.glVertexAttribPointer(i, elemCount.get(i), type.get(i), norm.get(i), stride, offset.get(i));
		}
	}
	
	public void enableAttribs() {
		for(int i=0; i<name.size(); i++) {
			GL20.glEnableVertexAttribArray(i);
		}
	}

	public void disableAttribs() {
		for(int i=0; i<name.size(); i++) {
			GL20.glDisableVertexAttribArray(i);
		}
	}

	public int bindAttribLocations(int programId) {
		for(int i=0; i<name.size(); i++) {
			if(name.get(i)!=null)
				GL20.glBindAttribLocation(programId, i, name.get(i));
		}
		return name.size();
	}
	
	public static final int sizeOf(int glType) {
		switch(glType) {
			case GL11.GL_BYTE:
			case GL11.GL_UNSIGNED_BYTE:
				return 1;
			case GL11.GL_2_BYTES:
			case GL11.GL_SHORT:
			case GL11.GL_UNSIGNED_SHORT:
				return 2;
			case GL11.GL_3_BYTES:
				return 3;
			case GL11.GL_4_BYTES:
			case GL11.GL_INT:
			case GL11.GL_UNSIGNED_INT:
			case GL11.GL_FLOAT:
				return 4;
			case GL11.GL_DOUBLE:
				return 8;
			default:
				return 0;
		}
	}
	
}
