/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Ashur Rafiev
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

uniform vec2 screenSize;
uniform mat4 viewMatrix;

in vec2 in_Position;

in vec2 ins_Position;
in vec2 ins_Size;
in vec2 ins_RotationScale;
in vec2 ins_TexCoord;
in vec4 ins_Color;

out vec2 pass_TexCoord;
out vec4 pass_Color;

mat2 rotationMatrix(float a) {
	mat2 m = mat2(1);
	m[0][0] = cos(a);
	m[0][1] = sin(a);
	m[1][0] = -m[0][1];
	m[1][1] = m[0][0];
	return m;
}

void main(void) {
	vec2 pos = rotationMatrix(ins_RotationScale.x) * ins_RotationScale.y * in_Position * ins_Size + ins_Position;
	vec4 vpos = viewMatrix * vec4(pos.x - screenSize.x/2.0, -pos.y + screenSize.y/2.0, 0.0, 1.0);
	
	gl_Position = vec4(
		vpos.x * 2.0 / screenSize.x,
		vpos.y * 2.0 / screenSize.y,
		0.0, 1.0);
	
	pass_TexCoord = in_Position * ins_Size + ins_TexCoord;
	pass_Color = ins_Color;
}