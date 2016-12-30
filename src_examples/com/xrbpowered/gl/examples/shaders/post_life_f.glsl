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

uniform sampler2D prev;

in vec2 pass_TextureCoord;

out vec4 out_Color;

ivec2 size;
ivec2 pos;

int alive(vec4 color) {
	return color.b > 0 ? 1 : 0;
}

int countAdj(int x, int y) {
	return alive(texelFetch(prev, ivec2((pos.x+size.x+x) % size.x, (pos.y+size.y+y) % size.y), 0));
}

void main(void) {
	size = textureSize(prev, 0);
	pos = ivec2(int(pass_TextureCoord.x*size.x), int(pass_TextureCoord.y*size.y));
	
	int adj = 0;
	adj += countAdj(-1, -1);
	adj += countAdj(0, -1);
	adj += countAdj(1, -1);
	adj += countAdj(-1, 0);
	adj += countAdj(1, 0);
	adj += countAdj(-1, 1);
	adj += countAdj(0, 1);
	adj += countAdj(1, 1);
	vec4 color = texelFetch(prev, pos, 0);
	int here = alive(color);

	out_Color = color;
	if(here>0) {
		if(adj<2 || adj>3)
			out_Color = vec4(1, 1, 0, 1);
		else
			out_Color = clamp(color*0.99, 0.25, 1);
	}
	else {
		if(adj==3)
			out_Color = vec4(1, 1, 1, 1);
		else
			out_Color = floor(clamp((color - vec4(0, 0.03, 0, 0))*0.97, 0, 1)*256.0)/256.0;
	}
}