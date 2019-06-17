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