#version 150 core

uniform vec2 pivot = vec2(0, 0);
uniform vec2 scale = vec2(256, 256);
uniform int seed = 0;

const float PI = 3.141592653589793;

in vec2 pass_TextureCoord;

out vec4 out_Color;

int nextSeed(int seed, int add) {
	seed *= seed * 134775813 + 1;
	seed += add;
	return seed;
}

int seedXY(int seed, int x, int y) {
	seed = nextSeed(seed, x);
	seed = nextSeed(seed, y);
	seed = nextSeed(seed, x);
	seed = nextSeed(seed, y);
	return seed;
}

float fseedXY(int seed, int x, int y) {
	return float((seedXY(seed, x, y) >> 16) & 0xffff) / float(1<<16);
}

float cosInt(float s) {
	return (1.0 - cos(s * PI)) * 0.5;
}

float smoothXY(int seed, float x, float y) {
	float v00 = fseedXY(seed, int(x), int(y));
	float v01 = fseedXY(seed, int(x), int(y+1));
	float v10 = fseedXY(seed, int(x+1), int(y));
	float v11 = fseedXY(seed, int(x+1), int(y+1));
	float sx = fract(x); // cosInt(fract(x));
	float sy = fract(y); // cosInt(fract(y));
	return mix(mix(v00, v01, sy), mix(v10, v11, sy), sx);
}

float perlin(int seed, vec2 pivot, vec2 scale) {
	float x0 = pivot.x+(pass_TextureCoord.x*2.0 - 1.0)*scale.x+float(1<<16);
	float y0 = pivot.y+(pass_TextureCoord.y*2.0 - 1.0)*scale.y+float(1<<16);

	float c = 0;
	for(int i=0; i<10; i++) {
		c = c*0.75 + smoothXY(seed, x0, y0);
		x0 *= 0.5;
		y0 *= 0.5;
	}
	c *= 1 / float(1<<3);
	return c;
}

void main(void) {
	float c = 0;
	c += perlin(seed, vec2(pivot.x, pivot.y), scale);
	c += perlin(seed, vec2(pivot.y, -pivot.x), scale);
	c += perlin(seed, vec2(-pivot.x, -pivot.y), scale);
	c += perlin(seed, vec2(-pivot.y, pivot.x), scale);
	c = 2.0*c - 1.25;
	out_Color = vec4(c, c, c, 1);
}
