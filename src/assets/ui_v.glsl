#version 150 core

uniform vec2 screenSize;
uniform vec2 anchor;

in vec2 in_Position;
in vec2 in_TexCoord;

out vec2 pass_TexCoord;

void main(void) {
//	gl_Position = vec4(in_Position, 0.0, 1.0);
	gl_Position = vec4(
		(in_Position.x + anchor.x) * 2.0 / screenSize.x - 1.0,
		1.0 - (in_Position.y + anchor.y) * 2.0 / screenSize.y,
		0.0, 1.0);
	
	pass_TexCoord = in_TexCoord;
}