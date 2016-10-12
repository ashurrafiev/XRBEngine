package com.xrbpowered.gl.res;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import com.xrbpowered.gl.res.shaders.VertexInfo;

public class StaticMesh {

	private FloatBuffer vertexBuffer = null;

	private int vaoId;
	private int vboId;
	private int vboiId = GL11.GL_INVALID_VALUE;
	private int countElements = 0;
	private int countAttribs;
	private int drawMode = GL11.GL_TRIANGLES;
	
	public StaticMesh(VertexInfo info, float[] vertexData, short[] indexData) {
		ByteBuffer vertexBuffer = BufferUtils.createByteBuffer(vertexData.length * 4);
		vertexBuffer.asFloatBuffer().put(vertexData).flip();

		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

		this.countAttribs = info.getAttributeCount();
		info.initAttribPointers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		if(indexData!=null) {
			countElements = indexData.length;
			ByteBuffer indexBuffer = BufferUtils.createByteBuffer(countElements * 2);
			indexBuffer.asShortBuffer().put(indexData).flip();
	
			vboiId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		else {
			countElements = vertexData.length * 4 / info.getStride();
		}
	}
	
	public StaticMesh(VertexInfo info, float[] vertexData, int drawMode, int countElements, boolean dynamic) {
		vertexBuffer = BufferUtils.createByteBuffer(vertexData.length * 4).asFloatBuffer();
		vertexBuffer.put(vertexData).flip();

		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, dynamic ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);

		this.countAttribs = info.getAttributeCount();
		info.initAttribPointers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		
		this.drawMode = drawMode;
		this.countElements = countElements;
	}
	
	public StaticMesh(VertexInfo info, FloatBuffer vertexBuffer, ShortBuffer indexBuffer, int countIndices) {
		this.countElements = countIndices;
		
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

		this.countAttribs = info.getAttributeCount();
		info.initAttribPointers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);	
	}
	
	public void bindVAO() {
		GL30.glBindVertexArray(vaoId);
	}
	
	public void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	public void updateVertexData(float[] vertexData) {
		vertexBuffer.clear();
		vertexBuffer.put(vertexData);
		vertexBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void updateCountElements(int count) {
		this.countElements = count;
	}
	
	public int countTris() {
		return countElements / 3;
	}
	
	public void enableDraw(int[] attribMask) {
		GL30.glBindVertexArray(vaoId);
		if(attribMask==null) {
			for(int i=0; i<countAttribs; i++)
				GL20.glEnableVertexAttribArray(i);
		}
		else {
			for(int i=0; i<attribMask.length; i++)
				GL20.glEnableVertexAttribArray(attribMask[i]);
		}
	}
	
	public void disableDraw() {
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		for(int i=0; i<countAttribs; i++)
			GL20.glDisableVertexAttribArray(i);
		GL30.glBindVertexArray(0);
	}
	
	public void drawCallInstanced(int count) {
		if(vboiId!=GL11.GL_INVALID_VALUE) {
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
			GL31.glDrawElementsInstanced(drawMode, countElements, GL11.GL_UNSIGNED_SHORT, 0, count);
		}
		else {
			GL31.glDrawArraysInstanced(drawMode, 0, countElements, count);
		}
	}
	
	public void drawCall() {
		if(vboiId!=GL11.GL_INVALID_VALUE) {
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
			GL11.glDrawElements(drawMode, countElements, GL11.GL_UNSIGNED_SHORT, 0);
		}
		else {
			GL11.glDrawArrays(drawMode, 0, countElements);
		}
	}
	
	public void draw(int[] attribMask) {
		enableDraw(attribMask);
		drawCall();
		disableDraw();
	}
	
	public void draw() {
		draw(null);
	}
	
	public void destroy() {
		GL30.glBindVertexArray(vaoId);
		for(int i=0; i<countAttribs; i++)
			GL20.glDisableVertexAttribArray(i);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		if(vboiId!=GL11.GL_INVALID_VALUE)
			GL15.glDeleteBuffers(vboiId);
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}
	
}
