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
package com.xrbpowered.gl.res.builder;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.shaders.VertexInfo;

public abstract class MeshBuilder {

	public static class Options {
		public final String positionName;
		public final String normalName;
		public final String tangentName;
		public final String texCoordName;
		public final String colorName;
		
		public Options(String position, String normal, String tangent, String texCoord, String color) {
			this.positionName = position;
			this.normalName = normal;
			this.tangentName = tangent;
			this.texCoordName = texCoord;
			this.colorName = color;
		}
		
		private Options() {
			this.positionName = "in_Position";
			this.normalName = "in_Normal";
			this.tangentName = "in_Tangent";
			this.texCoordName = "in_TexCoord";
			this.colorName = "in_Color";
		}
	}
	
	private static final Options defaultOptions = new Options(); 
	
	public abstract class Vertex {
		protected abstract void set(VertexInfo.Attribute attrib, int offs, float x);
		
		protected void set(VertexInfo.Attribute attrib, float x, float y, float z, float w) {
			if(attrib!=null) {
				set(attrib, 0, x);
				set(attrib, 1, y);
				set(attrib, 2, z);
				set(attrib, 3, w);
			}
		}
		
		public Vertex setPosition(float x, float y, float z, float w) {
			set(positionAttrib, x, y, z, w);
			return this;
		}

		public Vertex setPosition(float x, float y, float z) {
			set(positionAttrib, x, y, z, 1f);
			return this;
		}

		public Vertex setPosition(Vector4f v) {
			set(positionAttrib, v.x, v.y, v.z, v.w);
			return this;
		}
		
		public Vertex setPosition(Vector3f v) {
			set(positionAttrib, v.x, v.y, v.z, 1f);
			return this;
		}

		public Vertex setNormal(float x, float y, float z) {
			set(normalAttrib, x, y, z, 0f);
			return this;
		}

		public Vertex setNormal(Vector3f v) {
			set(normalAttrib, v.x, v.y, v.z, 0f);
			return this;
		}
		
		public Vertex setTangent(float x, float y, float z) {
			set(tangentAttrib, x, y, z, 0f);
			return this;
		}
		
		public Vertex setTangent(Vector3f v) {
			set(tangentAttrib, v.x, v.y, v.z, 0f);
			return this;
		}

		public Vertex setTexCoord(float u, float v) {
			set(texCoordAttrib, u, v, 0f, 0f);
			return this;
		}

		public Vertex setTexCoord(Vector2f v) {
			set(texCoordAttrib, v.x, v.y, 0f, 0f);
			return this;
		}

		public Vertex setColor(float r, float g, float b, float a) {
			set(colorAttrib, r, g, b, a);
			return this;
		}
		
		public Vertex setColor(float r, float g, float b) {
			set(colorAttrib, r, g, b, 1f);
			return this;
		}
		
		public Vertex setColor(Vector4f v) {
			set(colorAttrib, v.x, v.y, v.z, v.w);
			return this;
		}

		public Vertex setColor(Vector3f v) {
			set(colorAttrib, v.x, v.y, v.z, 1f);
			return this;
		}
		
		public Vertex setCustom(float x) {
			if(customAttrib!=null)
				set(customAttrib, customOffs, x);
			return this;
		}
		
		public Vertex setCustom(int offs, float x) {
			if(customAttrib!=null)
				set(customAttrib, offs, x);
			return this;
		}
	}
	
	public final VertexInfo info;
	
	protected VertexInfo.Attribute positionAttrib;
	protected VertexInfo.Attribute normalAttrib;
	protected VertexInfo.Attribute tangentAttrib;
	protected VertexInfo.Attribute texCoordAttrib;
	protected VertexInfo.Attribute colorAttrib;
	
	protected VertexInfo.Attribute customAttrib = null;
	protected int customOffs = 0;
	
	public MeshBuilder(VertexInfo info, Options options) {
		this.info = info;
		options = options==null ? defaultOptions : options;
		positionAttrib = info.get(options.positionName);
		normalAttrib = info.get(options.normalName);
		tangentAttrib = info.get(options.tangentName);
		texCoordAttrib = info.get(options.texCoordName);
		colorAttrib = info.get(options.colorName);
	}
	
	public MeshBuilder setCustomAttrib(String name, int offs) {
		this.customAttrib = info.get(name);
		this.customOffs = offs;
		return this;
	}
	
	public abstract StaticMesh create();
	
	protected static void setData(VertexInfo info, VertexInfo.Attribute attrib, int offs, float[] data, int index, float x) {
		if(offs<attrib.elemCount) {
			data[offs + attrib.offset + index*info.getSkip()] = x;
		}
	}
	
}
