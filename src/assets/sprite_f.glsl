#version 150 core

uniform sampler2D tex;

uniform float colorFunc = 0;

in vec2 pass_TexCoord;
in vec4 pass_Color;

out vec4 out_Color;

void main(void) {
	ivec2 texSize = textureSize(tex, 0);
	out_Color = texture(tex, vec2(
			pass_TexCoord.x / float(texSize.x),
			pass_TexCoord.y / float(texSize.y)
		)) * (pass_Color * (1.0 - colorFunc) + colorFunc) + pass_Color * colorFunc;
}