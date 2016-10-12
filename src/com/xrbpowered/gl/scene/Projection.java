package com.xrbpowered.gl.scene;

import org.lwjgl.util.vector.Matrix4f;

public class Projection {
	
	public static Matrix4f perspective(float fov, float aspectRatio, float near, float far) {
		Matrix4f matrix = new Matrix4f();
		matrix.setZero();

		float t = (float)Math.tan(Math.toRadians(fov) / 2.0);
		matrix.m00 = 1f / (aspectRatio * t);
		matrix.m11 = 1f / t;
		matrix.m22 = (far + near) / (near - far);
		matrix.m23 = -1;
		matrix.m32 = (2f * near * far) / (near - far);

		return matrix;
	}
	
	public static Matrix4f perspective(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		matrix.m00 = 2f * near / (right - left);
		matrix.m11 = 2f * near / (top - bottom);
		matrix.m22 = -(far + near) / (far - near);
		matrix.m23 = -1f;
		matrix.m32 = -2f * far * near / (far - near);
		matrix.m20 = (right + left) / (right - left);
		matrix.m21 = (top + bottom) / (top - bottom);
		matrix.m33 = 0f;

		return matrix;
	}

	public static Matrix4f orthogonal(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		matrix.m00 = 2f / (right - left);
		matrix.m11 = 2f / (top - bottom);
		matrix.m22 = -2f / (far - near);
		matrix.m32 = (far + near) / (far - near);
		matrix.m30 = (right + left) / (right - left);
		matrix.m31 = (top + bottom) / (top - bottom);

		return matrix;
	}
}
