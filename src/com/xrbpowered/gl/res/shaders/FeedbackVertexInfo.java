package com.xrbpowered.gl.res.shaders;

import java.util.Arrays;

import org.lwjgl.opengl.GL30;

public class FeedbackVertexInfo extends VertexInfo {

	private String[] feedbackNames = null;
	
	public VertexInfo setFeedbackNames(String[] names) {
		this.feedbackNames = names;
		return this;
	}

	@Override
	public int bindAttribLocations(int programId) {
		int res = super.bindAttribLocations(programId);
		if(feedbackNames!=null) {
			System.out.println("glTransformFeedbackVaryings "+Arrays.toString(feedbackNames));
			GL30.glTransformFeedbackVaryings(programId, feedbackNames, GL30.GL_INTERLEAVED_ATTRIBS);
		}
		return res;
	}
}
