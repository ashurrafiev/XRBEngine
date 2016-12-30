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

import java.awt.Color;

public abstract class ColorUtils {

	public static Color alpha(Color color, double s) {
		s = MathUtils.snap(s);
		return new Color(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				(int)Math.round(s*255.0)
			);
	}

	public static Color blend(Color color0, Color color1, double s) {
		s = MathUtils.snap(s);
		return new Color(
				(int)Math.round(MathUtils.lerp(color0.getRed(), color1.getRed(), s)),
				(int)Math.round(MathUtils.lerp(color0.getGreen(), color1.getGreen(), s)),
				(int)Math.round(MathUtils.lerp(color0.getBlue(), color1.getBlue(), s))
			);
	}

}
