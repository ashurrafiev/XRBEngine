package com.xrbpowered.gl.res;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.Scene;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class StandardMeshBuilder {
	
	public static class Vertex {
		public Vector3f position = new Vector3f();
		public Vector3f normal = new Vector3f();
		public Vector3f tangent = new Vector3f(1, 0, 0);
		public Vector2f texCoord = new Vector2f();
		public short index = -1;
		
		public Vertex() {
		}
		
		public Vertex(float x, float y, float z) {
			this.position.set(x, y, z);
		}
		
		public Vertex(float x, float y, float z, float u, float v) {
			this.position.set(x, y, z);
			this.texCoord.set(u, v);
		}
		
		public Vertex setPosition(float x, float y, float z) {
			this.position.set(x, y, z);
			return this;
		}
		
		public Vertex setPosition(Vector3f pos) {
			this.position.set(pos);
			return this;
		}
		
		public Vertex setNormal(float x, float y, float z) {
			this.normal.set(x, y, z);
			return this;
		}

		public Vertex setNormal(Vector3f norm) {
			this.normal.set(norm);
			return this;
		}

		public Vertex setTangent(Vector3f tan) {
			this.tangent.set(tan);
			return this;
		}
		
		public Vertex setTexCoord(Vector2f uv) {
			this.texCoord.set(uv);
			return this;
		}
		
		public Vertex setTexCoord(float u, float v) {
			this.texCoord.set(u, v);
			return this;
		}
		
		public void put(FloatBuffer buf) {
			buf.put(position.x);
			buf.put(position.y);
			buf.put(position.z);
			
			buf.put(normal.x);
			buf.put(normal.y);
			buf.put(normal.z);
						
			buf.put(tangent.x);
			buf.put(tangent.y);
			buf.put(tangent.z);
			
			buf.put(texCoord.x);
			buf.put(texCoord.y);
		}
		
		public int put(float[] buf, int start) {
			buf[start+0] = position.x;
			buf[start+1] = position.y;
			buf[start+2] = position.z;
			
			buf[start+3] = normal.x;
			buf[start+4] = normal.y;
			buf[start+5] = normal.z;

			buf[start+6] = tangent.x;
			buf[start+7] = tangent.y;
			buf[start+8] = tangent.z;

			buf[start+9] = texCoord.x;
			buf[start+10] = texCoord.y;
			return start+11;
		}
	}
	
	public static abstract class Face {
		public Vertex[] vertices = null;
		public long smoothGroupMask = 0;
		public abstract int countTris();
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
		
		public Face calcTangents() {
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
	
	public Vertex add(Vertex v) {
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
	
	public StaticMesh create(boolean calcNormals) {
		updateIndices();
		int countIndices = countTris() * 3;
		int countVertices = vertices.size();
		if(countIndices==0 || countVertices==0)
			return null;
		
		ShortBuffer indexBuffer = BufferUtils.createByteBuffer(countIndices * 2).asShortBuffer();
		for(Face f : faces)
			f.putIndices(indexBuffer);
		indexBuffer.flip();
		
		FloatBuffer vertexBuffer = BufferUtils.createByteBuffer(countVertices * StandardShader.standardVertexInfo.getStride() * 4).asFloatBuffer();
		for(Vertex v : vertices)
			v.put(vertexBuffer);
		vertexBuffer.flip();
		
		return new StaticMesh(StandardShader.standardVertexInfo, vertexBuffer, indexBuffer, countIndices);
	}
	
	public static StaticMesh cube(float size) {
		float d = size / 2f;
		Vector3f norm = new Vector3f();
		Vector3f tan = new Vector3f();
		StandardMeshBuilder b = new StandardMeshBuilder();
		
		norm.set(0, 0, -1);
		tan.set(-1, 0, 0); // FIXME cube tangents
		b.add(new Quad(
				b.add(new Vertex(-d, -d, -d, 1, 1)),
				b.add(new Vertex(-d, d, -d, 1, 0)),
				b.add(new Vertex(d, d, -d, 0, 0)),
				b.add(new Vertex(d, -d, -d, 0, 1))
			).setNormals(norm)).setTangents(tan);
		norm.set(0, 0, 1);
		tan.set(1, 0, 0);
		b.add(new Quad(
				b.add(new Vertex(d, -d, d, 1, 1)),
				b.add(new Vertex(d, d, d, 1, 0)),
				b.add(new Vertex(-d, d, d, 0, 0)),
				b.add(new Vertex(-d, -d, d, 0, 1))
			).setNormals(norm)).setTangents(tan);
		norm.set(0, 1, 0);
		tan.set(1, 0, 0);
		b.add(new Quad(
				b.add(new Vertex(-d, d, -d, 0, 0)),
				b.add(new Vertex(-d, d, d, 0, 1)),
				b.add(new Vertex(d, d, d, 1, 1)),
				b.add(new Vertex(d, d, -d, 1, 0))
			).setNormals(norm)).setTangents(tan);
		norm.set(0, -1, 0);
		tan.set(-1, 0, 0);
		b.add(new Quad(
				b.add(new Vertex(-d, -d, d, 0, 0)),
				b.add(new Vertex(-d, -d, -d, 0, 1)),
				b.add(new Vertex(d, -d, -d, 1, 1)),
				b.add(new Vertex(d, -d, d, 1, 0))
			).setNormals(norm)).setTangents(tan);
		norm.set(-1, 0, 0);
		tan.set(0, 1, 0);
		b.add(new Quad(
				b.add(new Vertex(-d, -d, d, 1, 1)),
				b.add(new Vertex(-d, d, d, 1, 0)),
				b.add(new Vertex(-d, d, -d, 0, 0)),
				b.add(new Vertex(-d, -d, -d, 0, 1))
			).setNormals(norm)).setTangents(tan);
		norm.set(1, 0, 0);
		tan.set(0, 1, 0);
		b.add(new Quad(
				b.add(new Vertex(d, -d, -d, 1, 1)),
				b.add(new Vertex(d, d, -d, 1, 0)),
				b.add(new Vertex(d, d, d, 0, 0)),
				b.add(new Vertex(d, -d, d, 0, 1))
			).setNormals(norm)).setTangents(tan);
		
		return b.create(false);
	}
	
	public static StaticMesh plane(float size, int segm, int tileTex) {
		int i, j;
		float d = size / segm;
		
		int skip = StandardShader.standardVertexInfo.getStride() / 4;
		float[] vertexData = new float[(segm+1) * (segm+1) * skip];
		Vector3f v = new Vector3f();
		int offs = 0;
		for(i=0; i<=segm; i++) {
			for(j=0; j<=segm; j++) {
				v.x = -size/2f + i*d;
				v.y = 0;
				v.z = -size/2f + j*d;
				vertexData[offs+0] = v.x;
				vertexData[offs+1] = v.y;
				vertexData[offs+2] = v.z;
				vertexData[offs+3] = 0;
				vertexData[offs+4] = 1;
				vertexData[offs+5] = 0;
				vertexData[offs+6] = 1;
				vertexData[offs+7] = 0;
				vertexData[offs+8] = 0;
				vertexData[offs+9] = i * tileTex / (float) segm;
				vertexData[offs+10] = j * tileTex / (float) segm;
				offs += skip;
			}
		}
		
		short[] indexData = new short[segm * segm * 6];
		offs = 0;
		for(i=0; i<segm; i++) {
			for(j=0; j<segm; j++) {
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+0));
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+0));
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+0));
			}
		}
		
		return new StaticMesh(StandardShader.standardVertexInfo, vertexData, indexData);
	}
	
	public static StaticMesh terrain(float size, float[][] hmap, int tileTex) {
		int i, j;
		int segm = hmap.length - 1;
		float d = size / segm;
		
		int skip = StandardShader.standardVertexInfo.getStride() / 4;
		float[] vertexData = new float[(segm+1) * (segm+1) * skip];
		Vector3f v = new Vector3f();
		Vector3f n = new Vector3f();
		int offs = 0;
		for(i=0; i<=segm; i++) {
			for(j=0; j<=segm; j++) {
				v.x = -size/2f + i*d;
				v.y = hmap[i][j];
				v.z = -size/2f + j*d;
				vertexData[offs+0] = v.x;
				vertexData[offs+1] = v.y;
				vertexData[offs+2] = v.z;
				
				v.set(0, 0, 0);
				if(i>0) {
					n.set(hmap[i][j] - hmap[i-1][j], d, 0);
					n.normalise();
					Vector3f.add(v, n, v);
				}
				if(i<segm) {
					n.set(hmap[i+1][j] - hmap[i][j], d, 0);
					n.normalise();
					Vector3f.add(v, n, v);
				}
				if(j>0) {
					n.set(0, d, hmap[i][j] - hmap[i][j-1]);
					n.normalise();
					Vector3f.add(v, n, v);
				}
				if(j<segm) {
					n.set(0, d, hmap[i][j+1] - hmap[i][j]);
					n.normalise();
					Vector3f.add(v, n, v);
				}
				v.normalise();
				
				vertexData[offs+3] = -v.x;
				vertexData[offs+4] = v.y;
				vertexData[offs+5] = -v.z;
				vertexData[offs+6] = 1;
				vertexData[offs+7] = 0;
				vertexData[offs+8] = 0;
				vertexData[offs+9] = i * tileTex / (float) segm;
				vertexData[offs+10] = j * tileTex / (float) segm;
				offs += skip;
			}
		}
		
		short[] indexData = new short[segm * segm * 6];
		offs = 0;
		for(i=0; i<segm; i++) {
			for(j=0; j<segm; j++) {
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+0));
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+0));
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+0));
			}
		}
		
		return new StaticMesh(StandardShader.standardVertexInfo, vertexData, indexData);
	}
	
	public static StaticMesh sphere(float r, int segm) {
		int i, j;
		
		float[] sin = new float[segm*2+1];
		float[] cos = new float[segm*2+1];
		
		int skip = StandardShader.standardVertexInfo.getStride() / 4;
		float[] vertexData = new float[(segm+1) * (segm*2+1) * skip];
		
		float ai;
		float da = (float) Math.PI / (float) segm;
		for(i=0, ai = 0; i<=segm*2; i++, ai += da) {
			sin[i] = (float) Math.sin(ai);
			cos[i] = (float) Math.cos(ai);
		}
		
		Vector3f v = new Vector3f();
		int offs = 0;
		for(i=0; i<=segm*2; i++) {
			for(j=0; j<=segm; j++) {
				float r0 = r * sin[j];
				v.y = -r * cos[j];
				v.x = r0 * cos[i];
				v.z = r0 * sin[i];
				vertexData[offs+0] = v.x;
				vertexData[offs+1] = v.y;
				vertexData[offs+2] = v.z;
				vertexData[offs+3] = v.x/r;
				vertexData[offs+4] = v.y/r;
				vertexData[offs+5] = v.z/r;
				vertexData[offs+6] = (r0>0f) ? -v.z/r0 : v.y/r;
				vertexData[offs+7] = 0;
				vertexData[offs+8] = (r0>0f) ? v.x/r0 : v.y/r;
				vertexData[offs+9] = i / (float) segm;
				vertexData[offs+10] = j / (float) segm;
				offs += skip;
			}
		}
		
		short[] indexData = new short[segm * segm * 2 * 6];
		offs = 0;
		for(i=0; i<segm*2; i++) {
			for(j=0; j<segm; j++) {
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+0));
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+1));
				indexData[offs++] = (short)((i+1) * (segm+1) + (j+0));
				indexData[offs++] = (short)((i+0) * (segm+1) + (j+0));
			}
		}
		
		return new StaticMesh(StandardShader.standardVertexInfo, vertexData, indexData);
	}
	
	public static StaticMeshActor makeActor(Scene scene, final StaticMesh mesh, final Texture diffuse, final Texture specular, final Texture normal) {
		return makeActor(scene, mesh, StandardShader.getInstance(), diffuse, specular, normal);
	}
	
	public static StaticMeshActor makeActor(Scene scene, final StaticMesh mesh, final ActorShader shader, final Texture diffuse, final Texture specular, final Texture normal) {
		return new StaticMeshActor(scene) {
			@Override
			protected void setup() {
				setMesh(mesh);
				setShader(shader);
				setTextures(new Texture[] {diffuse, specular, normal});
			}
		};
	}
}
