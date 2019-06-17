#version 150 core

uniform sampler2D colorBuf;

uniform float time = 0;

uniform vec4 mulColor = vec4(1, 1, 1, 1);
uniform vec4 addColor = vec4(0, 0, 0, 1);

uniform float range = 20;
uniform int numSamples = 10;

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	float si = 1 / float(numSamples);
	ivec2 size = textureSize(colorBuf, 0);
	vec2 ss = vec2(0.5 / float(size.x), 0.5 / float(size.y));
	vec4 sum = vec4(0, 0, 0, 0);
	float norm = 0;
	for(int ix=-numSamples; ix<=numSamples; ix++)
		for(int iy=-numSamples; iy<=numSamples; iy++) {
			vec2 offs = vec2(float(ix), float(iy)) *si;
			float dist = 1 - min(1, length(offs));
			if(dist>0) {
				offs *= range * ss;
				sum += dist * texture(colorBuf, pass_TextureCoord + offs);
				norm += dist;
			}
		}
	out_Color = sum / norm;
	out_Color = out_Color * mulColor + addColor;
}