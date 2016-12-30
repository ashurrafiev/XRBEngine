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

public abstract class TweenUtils {

	public static double tween(long t, long start, long duration) {
		return (t - start) / (double) duration;
	}

	public static double easeOut(double s) {
		return s*s;
	}

	public static double easeIn(double s) {
		s = 1.0-s;
		return 1.0-s*s;
	}

	public static double easeInOut(double s) {
		s *= 2.0;
		if(s<1) return 0.5*s*s;
		s -= 1.0;
		return -0.5*(s*(s-2.0)-1.0);
	}
	
	public static double wave(long t, long period) {
		return Math.sin(Math.PI * 2.0 * (double)t / (double)period)*0.5+0.5;
	}

}
