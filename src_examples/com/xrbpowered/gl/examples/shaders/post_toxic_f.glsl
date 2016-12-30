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

uniform sampler2D colorBuf;

uniform float time = 0;

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	vec2 tex = vec2(
		pass_TextureCoord.x + 0.01*sin(pass_TextureCoord.x*16+(time-0.2)*2),
		pass_TextureCoord.y + 0.01*cos(pass_TextureCoord.y*9+(time-0.2)*2)
	);
	float r = texture(colorBuf, tex).r;
	tex = vec2(
		pass_TextureCoord.x + 0.01*sin(pass_TextureCoord.x*16+(time)*2),
		pass_TextureCoord.y + 0.01*cos(pass_TextureCoord.y*9+(time)*2)
	);
	float g = texture(colorBuf, tex).g;
	tex = vec2(
		pass_TextureCoord.x + 0.01*sin(pass_TextureCoord.x*16+(time+0.2)*2),
		pass_TextureCoord.y + 0.01*cos(pass_TextureCoord.y*9+(time+0.2)*2)
	);
	float b = texture(colorBuf, tex).b;
	out_Color = vec4(r, g, b, 1);
}