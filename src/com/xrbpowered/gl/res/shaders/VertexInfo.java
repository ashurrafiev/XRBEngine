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

	public static class Attribute {
		public final String name;
		public final int elemCount;
		public final int offset;
		public Attribute(String name, int elemCount, int offset) {
			this.name = name;
			this.elemCount = elemCount;
			this.offset = offset;
		}
	}
	
	private int skip = 0;
	private List<Attribute> attribs = new ArrayList<>();
	
	public VertexInfo addAttrib(String name, int elemCount) {
		this.attribs.add(new Attribute(name, elemCount, skip));
		skip += elemCount;
		return this;
	}

	public int getStride() {
		return skip * 4;
	}
	
	public int getSkip() {
		return skip;
	}
	
	public int getAttributeCount() {
		return attribs.size();
	}
	
	public Attribute get(int index) {
		return attribs.get(index);
	}

	public int attributeIndex(String name) {
		if(name==null)
			return -1;
		for(int i=0; i<getAttributeCount(); i++) {
			Attribute a = attribs.get(i);
			if(a.name!=null && name.equals(a.name))
				return i;
		}
		return -1;
	}

	public Attribute get(String name) {
		int index = attributeIndex(name);
		if(index<0)
			return null;
		else
			return get(index);
	}
	
	public void initAttribPointers() {
		for(int i=0; i<getAttributeCount(); i++) {
			Attribute a = attribs.get(i);
			GL20.glVertexAttribPointer(i, a.elemCount, GL11.GL_FLOAT, false, getStride(), a.offset * 4);
		}
	}
	
	public void enableAttribs() {
		for(int i=0; i<getAttributeCount(); i++) {
			GL20.glEnableVertexAttribArray(i);
		}
	}

	public void disableAttribs() {
		for(int i=0; i<getAttributeCount(); i++) {
			GL20.glDisableVertexAttribArray(i);
		}
	}

	public int bindAttribLocations(int programId) {
		for(int i=0; i<getAttributeCount(); i++) {
			Attribute a = attribs.get(i);
			if(a.name!=null)
				GL20.glBindAttribLocation(programId, i, a.name);
		}
		return getAttributeCount();
	}
	
}
