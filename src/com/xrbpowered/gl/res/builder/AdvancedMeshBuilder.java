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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.res.shaders.VertexInfo.Attribute;

public class AdvancedMeshBuilder extends MeshBuilder {

	public class Vertex extends MeshBuilder.Vertex {
		private short index = 0;
		private float[] data;
		
		public Vertex() {
			data = new float[info.getSkip()];
		}
		
		@Override
		protected void set(Attribute attrib, int offs, float x) {
			MeshBuilder.setData(info, attrib, offs, data, 0, x);
		}
		
		public void put(FloatBuffer buf) {
			buf.put(data);
		}
	}
	
	public static abstract class Face {
		public Vertex[] vertices = null;
		public long smoothGroupMask = 0;
		public abstract int countTris();
		public abstract int verticesPerElement();
		public abstract void putIndices(ShortBuffer buf);
		public abstract int putIndices(short[] buf, int start);
		
		public Face setNormals(Vector3f norm) {
			for(Vertex v : vertices)
				v.setNormal(norm);
			return this;
		}
		
		public Face setTangents(Vector3f tan) {
			for(Vertex v : vertices)
				v.setTangent(tan);
			return this;
		}
		
/*		public Face calcTangents() {
			// doesn't work?
			
			Vector3f edge1 = new Vector3f();
			Vector3f.sub(vertices[2].position, vertices[0].position, edge1);
			Vector3f edge2 = new Vector3f();
			Vector3f.sub(vertices[1].position, vertices[0].position, edge2);
			Vector2f deltaUV1 = new Vector2f();
			Vector2f.sub(vertices[2].texCoord, vertices[0].texCoord, deltaUV1);
			Vector2f deltaUV2 = new Vector2f();
			Vector2f.sub(vertices[1].texCoord, vertices[0].texCoord, deltaUV2);
			
			float f = (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
			if(f==0f)
				return this;
			f = 1f / f;

			Vector3f tan = new Vector3f();
			tan.x = f * (deltaUV1.x * edge2.x - deltaUV2.x * edge1.x);
			tan.y = f * (deltaUV1.x * edge2.y - deltaUV2.x * edge1.y);
			tan.z = f * (deltaUV1.x * edge2.z - deltaUV2.x * edge1.z);
			tan.normalise();
			// FIXME smoothing
			
			for(Vertex v : vertices)
				v.setTangent(tan);
			return this;
		}*/
	}
	
	public static class Edge extends Face {
		public Edge(Vertex v0, Vertex v1) {
			this.vertices = new Vertex[] {v0, v1};
		}
		@Override
		public int countTris() {
			return 1;
		}
		@Override
		public int verticesPerElement() {
			return 2;
		}
		@Override
		public void putIndices(ShortBuffer buf) {
			buf.put(vertices[0].index);
			buf.put(vertices[1].index);
		}
		@Override
		public int putIndices(short[] buf, int start) {
			buf[start++] = vertices[0].index;
			buf[start++] = vertices[1].index;
			return start;
		}
	}
	
	public static class Triangle extends Face {
		public Triangle(Vertex v0, Vertex v1, Vertex v2) {
			this.vertices = new Vertex[] {v0, v1, v2};
		}
		@Override
		public int countTris() {
			return 1;
		}
		@Override
		public int verticesPerElement() {
			return 3;
		}
		@Override
		public void putIndices(ShortBuffer buf) {
			buf.put(vertices[0].index);
			buf.put(vertices[1].index);
			buf.put(vertices[2].index);
		}
		@Override
		public int putIndices(short[] buf, int start) {
			buf[start++] = vertices[0].index;
			buf[start++] = vertices[1].index;
			buf[start++] = vertices[2].index;
			return start;
		}
	}
	
	public static class Quad extends Face {
		public Quad(Vertex v0, Vertex v1, Vertex v2, Vertex v3) {
			this.vertices = new Vertex[] {v0, v1, v2, v3};
		}
		@Override
		public int countTris() {
			return 2;
		}
		@Override
		public int verticesPerElement() {
			return 3;
		}
		@Override
		public void putIndices(ShortBuffer buf) {
			buf.put(vertices[0].index);
			buf.put(vertices[1].index);
			buf.put(vertices[2].index);
			buf.put(vertices[2].index);
			buf.put(vertices[3].index);
			buf.put(vertices[0].index);
		}
		@Override
		public int putIndices(short[] buf, int start) {
			buf[start++] = vertices[0].index;
			buf[start++] = vertices[1].index;
			buf[start++] = vertices[2].index;
			buf[start++] = vertices[2].index;
			buf[start++] = vertices[3].index;
			buf[start++] = vertices[0].index;
			return start;
		}
	}
	
	public List<Vertex> vertices = new ArrayList<>();
	public List<Face> faces = new ArrayList<>();
	
	public AdvancedMeshBuilder(VertexInfo info, Options options) {
		super(info, options);
	}

	public Vertex addVertex() {
		Vertex v = new Vertex();
		vertices.add(v);
		return v;
	}

	public Face add(Face f) {
		faces.add(f);
		return f;
	}

	public void updateIndices() {
		short index = 0;
		for(Vertex v : vertices)
			v.index = index++;
	}
	
	public int countTris() {
		int count = 0;
		for(Face f : faces)
			count += f.countTris();
		return count;
	}
	
	public int verticesPerElement() {
		int res = 0;
		for(Face f : faces) {
			int v = f.verticesPerElement();
			if(v!=res) {
				if(res>0)
					return 0;
				else
					res = v;
			}
		}
		return res;
	}
	
	@Override
	public StaticMesh create() {
		updateIndices();
		int vpe = verticesPerElement();
		int countIndices = countTris() * vpe;
		int countVertices = vertices.size();
		if(countIndices==0 || countVertices==0)
			return null;
		
		ShortBuffer indexBuffer = BufferUtils.createByteBuffer(countIndices * 2).asShortBuffer();
		for(Face f : faces)
			f.putIndices(indexBuffer);
		indexBuffer.flip();
		
		FloatBuffer vertexBuffer = BufferUtils.createByteBuffer(countVertices * info.getStride()).asFloatBuffer();
		for(Vertex v : vertices)
			v.put(vertexBuffer);
		vertexBuffer.flip();
		
		return new StaticMesh(info, vertexBuffer, indexBuffer, countIndices, vpe, false);
	}

}
