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
 #version 150 core

out vec4 out_Color;

in float pass_Size;

void main(void) {
	float d = distance(gl_PointCoord, vec2(0.5, 0.5));
	float delta = fwidth(d);

	float a = 1 - smoothstep(0.5-delta, 0.5, d);
	if(pass_Size<8)
		a -= (8 - pass_Size) * 0.12;
	if(a<0.1) discard;
	if(d<0.48) {
		d = 1 - 4 * d * d;
		float d2 = max(1 - 7.5 * distance(gl_PointCoord, vec2(0.3, 0.3)), 0);
		a =  clamp(a - d + 0.5 * d2, 0, 1);
	}

//	float c = 1 - smoothstep(0.4-delta, 0.4, d);
//	out_Color = vec4(c, c, c, a);

	out_Color = vec4(1 - gl_PointCoord.x * 0.5, 1 - gl_PointCoord.y * 0.5, 1, a);
}
