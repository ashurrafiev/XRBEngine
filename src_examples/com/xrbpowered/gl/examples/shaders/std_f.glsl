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

uniform mat4 viewMatrix;

uniform sampler2D texDiffuse;
uniform sampler2D texSpecular;
uniform sampler2D texNormal;
uniform float alpha;
uniform float time;

uniform vec3 lightDirection;
uniform vec4 lightColor;
uniform float specPower;
uniform vec4 ambientColor;

struct PointLight {
	vec3 position;
	vec3 att;
	vec4 color;
};
uniform PointLight pointLights[32];
uniform int numPointLights = 0;

uniform float fogNear = 10;
uniform float fogFar = 50;
uniform vec4 fogColor = vec4(0.4, 0.6, 0.9, 0);

in vec4 pass_Position;
in vec4 pass_Color;
in vec3 pass_Normal;
in mat3 pass_TBN;
in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	vec4 t_diffuse = texture(texDiffuse, pass_TexCoord);
	if(t_diffuse.a<0.5)
		discard;
	vec4 t_spec = texture(texSpecular, pass_TexCoord);

	vec4 t_norm = texture(texNormal, pass_TexCoord);
	vec3 normal = 2 * t_norm.xyz - vec3(1, 1, 1);
	normal.z = gl_FrontFacing ? normal.z : -normal.z;
	normal = normalize(pass_TBN * normal);
//	vec3 normal = gl_FrontFacing ? pass_Normal : -pass_Normal;

	float viewDist = length(pass_Position.xyz);
	vec3 viewDir = normalize(-pass_Position.xyz);
	
	vec3 lightDir = normalize((viewMatrix * vec4(-lightDirection, 0)).xyz);
	float diffuse = max(dot(normal, lightDir), 0);
	vec4 diffuseColor = lightColor * diffuse;
	float spec = pow(max(dot(viewDir, normalize(reflect(-lightDir, normal))), 0), specPower);
	vec4 specColor = lightColor * spec;

	for(int i=0; i<numPointLights; i++) {
		vec3 lightVec = ((viewMatrix * vec4(pointLights[i].position, 1)) - pass_Position).xyz;
		float d = length(lightVec);
		float att = 1 / (pointLights[i].att.x + d*pointLights[i].att.y + d*d*pointLights[i].att.z);
		if(att>0.005) {
			lightDir = normalize(lightVec);
			diffuse = max(dot(normal, lightDir), 0);
			diffuseColor += pointLights[i].color * diffuse * att;
			spec = pow(max(dot(viewDir, normalize(reflect(-lightDir, normal))), 0), specPower);
			specColor += pointLights[i].color * spec * att;
		}
	}
	
	out_Color = (diffuseColor + ambientColor) * t_diffuse + specColor * t_spec;
//	out_Color.a = alpha + spec * specColor.r;
	out_Color.a = t_diffuse.a + specColor.r * t_spec.r;
	
	if(fogFar>0 && fogFar>fogNear) {
		out_Color = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	}
}

// https://wiki.blender.org/index.php/User:Mont29/Foundation/FBX_File_Structure
