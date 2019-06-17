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