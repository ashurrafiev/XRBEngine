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

uniform sampler2D palette;
uniform int maxi = 256;
uniform float aspect = 1;
uniform float zoom = 1;
uniform vec2 pivot = vec2(-1.15, -0.25);

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	float x0 = pivot.x+(pass_TextureCoord.x*2.0 - 1.0)*aspect*zoom;
	float y0 = pivot.y+(pass_TextureCoord.y*2.0 - 1.0)*zoom;
	
	float x = 0.0;
	float y = 0.0;
	float xtemp;
	int i = 0;
	while((x*x+y*y <100.0) && (i<maxi)) {
		xtemp = x*x - y*y + x0;
		y = 2*x*y + y0;
		x = xtemp;
		i++;
	}
	
	float c; // = float(i)/float(maxi);
	if(i<maxi) {
		float logzn = log(x*x+y*y) / 2.0;
		float nu = log(logzn/log(2.0)) / log(2.0);
		c = (float(i+1) - nu) / float(maxi);
	}
	else {
		c = 1.0;
	}
	out_Color = texture(palette, vec2(c, 0));
	// out_Color = vec4(c, c, c, 1);
}