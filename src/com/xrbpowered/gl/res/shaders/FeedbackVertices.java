package com.xrbpowered.gl.res.shaders;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.xrbpowered.gl.Client;

public class FeedbackVertices {

	private FloatBuffer vertexBuffer = null;
	private FloatBuffer feedbackBuffer = null;
	private IntBuffer indexBuffer = null;

	private Shader transformShader;
	private Shader renderShader;
	
	private int vaoId;
	private int vboId;
	private int vboFeedbackId;
	private int vboiId = GL11.GL_INVALID_VALUE;
	private int countElements = 0;

	public FeedbackVertices(Shader transformShader, Shader renderShader, int count, boolean createIndices) {
		this.transformShader = transformShader;
		this.renderShader = renderShader;
		this.countElements = count;
		vertexBuffer = BufferUtils.createByteBuffer(count * transformShader.info.getStride()).asFloatBuffer();
		feedbackBuffer = BufferUtils.createByteBuffer(count * renderShader.info.getStride()).asFloatBuffer();
		
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, count * transformShader.info.getStride(), GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		vboFeedbackId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboFeedbackId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, count * renderShader.info.getStride(), GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
		
		if(createIndices) {
			indexBuffer = BufferUtils.createByteBuffer(count * 4).asIntBuffer();
			
			vboiId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, count * 4, GL15.GL_DYNAMIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		Client.checkError();
	}
	
	public void updateVertexData(float[] vertexData) {
		vertexBuffer.clear();
		vertexBuffer.put(vertexData);
		vertexBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void updateIndexData(int[] indexData) {
		indexBuffer.clear();
		indexBuffer.put(indexData);
		indexBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboiId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, indexBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void updateIndexData(Integer[] indexData) {
		indexBuffer.clear();
		for(int i=0; i<indexData.length; i++)
			indexBuffer.put(indexData[i]);
		indexBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboiId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, indexBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void transform() {
		GL30.glBindVertexArray(vaoId);
		GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		
		transformShader.use();
		transformShader.info.initAttribPointers();
		transformShader.info.enableAttribs();
		
		GL30.glBindBufferBase(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, vboFeedbackId);
		GL30.glBeginTransformFeedback(GL11.GL_POINTS);
		GL11.glDrawArrays(GL11.GL_POINTS, 0, countElements);
		GL30.glEndTransformFeedback();

		transformShader.info.disableAttribs();
		transformShader.unuse();
		GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
	}
	
	public void getFeedbackData(float[] dst) {
		feedbackBuffer.clear();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboFeedbackId);
		GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, feedbackBuffer);
		feedbackBuffer.get(dst);
	}
	
	public FloatBuffer getFeedbackData() {
		feedbackBuffer.clear();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboFeedbackId);
		GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, feedbackBuffer);
		return feedbackBuffer;
	}

	public void draw() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboFeedbackId);
		renderShader.use();
		renderShader.info.enableAttribs();
		renderShader.info.initAttribPointers();
		
		if(vboiId!=GL11.GL_INVALID_VALUE) {
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
			GL11.glDrawElements(GL11.GL_POINTS, countElements, GL11.GL_UNSIGNED_INT, 0);
		}
		else {
			GL11.glDrawArrays(GL11.GL_POINTS, 0, countElements);
		}
		
		renderShader.info.disableAttribs();
		renderShader.unuse();
		GL30.glBindVertexArray(0);
	}
}
