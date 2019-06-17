#version 150 core

uniform sampler2D colorBuf;

in vec2 pass_TextureCoord;

out vec4 out_Color;

float hdr(float x) {
	return max(0, sqrt(x)-1);
}

void main(void) {
	vec4 col = texture(colorBuf, pass_TextureCoord);
	out_Color = vec4(hdr(col.x), hdr(col.y), hdr(col.z), 1);
}