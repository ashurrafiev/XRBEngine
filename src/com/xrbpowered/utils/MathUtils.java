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
package com.xrbpowered.utils;

public abstract class MathUtils {

	public static double snap(double s) {
		return snap(s, 0.0, 1.0);
	}

	public static double snap(double s, double min, double max) {
		if(s<min)
			return min;
		else if(s>max)
			return max;
		else
			return s;
	}

	public static int snap(int s, int min, int max) {
		if(s<min)
			return min;
		else if(s>max)
			return max;
		else
			return s;
	}
	
	public static double lerp(double x0, double x1, double s) {
		return x0 * (1.0-s) + x1 * s;
	}
	
	public static float lerp(float x0, float x1, float s) {
		return x0 * (1f-s) + x1 * s;
	}
	
	public static double cosInt(double s) {
		return (1.0 - Math.cos(s * Math.PI)) * 0.5;
	}
	
	public static float cosInt(float s) {
		return (1f - (float)Math.cos(s * Math.PI)) * 0.5f;
	}

}
