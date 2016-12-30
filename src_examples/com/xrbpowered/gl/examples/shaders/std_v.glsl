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

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform mat3 normalMatrix;

in vec3 in_Position;
in vec3 in_Normal;
in vec3 in_Tangent;
in vec2 in_TexCoord;

out vec4 pass_Position;
out vec4 pass_Color;
out vec3 pass_Normal;
out mat3 pass_TBN;
out vec2 pass_TexCoord;

void main(void) {
	pass_Position = viewMatrix * modelMatrix * vec4(in_Position, 1);
	gl_Position = projectionMatrix * pass_Position;
	
//	vec3 norm = normalize(normalMatrix * in_Normal);
	vec3 norm = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Normal, 0)));
	pass_Normal = norm;
//	vec3 tan = normalize(normalMatrix * in_Tangent);
	vec3 tan = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Tangent, 0)));
	tan = normalize(tan - dot(tan, norm) * norm);
	pass_TBN = mat3(tan, cross(tan, norm), norm);
	
	pass_TexCoord = in_TexCoord;
}