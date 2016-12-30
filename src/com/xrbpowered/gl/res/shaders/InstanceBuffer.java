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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import com.xrbpowered.gl.Client;

public class InstanceBuffer {

	private FloatBuffer instanceBuffer = null;
	
	private int divisor;
	private int attribId;
	private int[] elemCount;
	private int stride;
	private int iboId;
	
	public InstanceBuffer(int divisor, int count, int attribId, int[] elemCount) {
		this.divisor = divisor;
		this.attribId = attribId;
		this.elemCount = elemCount;
		stride = 0;
		for(int i=0; i<elemCount.length; i++) {
			stride += 4 * elemCount[i];
		}
		instanceBuffer = BufferUtils.createByteBuffer(count * stride).asFloatBuffer();
		
		iboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, count * stride, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		Client.checkError();
	}
	
	public InstanceBuffer(int divisor, int count, int attribId, int elemCount) {
		this(divisor, count, attribId, new int[] {elemCount});
	}
	
	public static int bindAttribLocations(Shader shader, int startIndex, String[] names) {
		for(int i=0; i<names.length; i++)
			GL20.glBindAttribLocation(shader.pId, i+startIndex, names[i]);
		return startIndex+names.length;
	}
	
	public void updateInstanceData(float[] instanceData, int count) {
		instanceBuffer.clear();
		instanceBuffer.put(instanceData, 0, count * stride / 4);
		instanceBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, instanceBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		Client.checkError();
	}
	
	public void enable() {
		int offs = 0;
		for(int i=0; i<elemCount.length; i++) {
			GL20.glEnableVertexAttribArray(attribId+i);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iboId);
			GL20.glVertexAttribPointer(attribId+i, elemCount[i], GL11.GL_FLOAT, false, stride, offs);
			offs += 4 * elemCount[i];
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL33.glVertexAttribDivisor(attribId+i, divisor);
//			GL20.glGetVertexAttrib(attribId, GL33.GL_VERTEX_ATTRIB_ARRAY_DIVISOR, testParam);
			Client.checkError();
		}
	}
	
	public void disable() {
		for(int i=0; i<elemCount.length; i++)
			GL20.glDisableVertexAttribArray(attribId+i);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void destroy() {
		// TODO destroy InstanceBuffer
	}
	
}
