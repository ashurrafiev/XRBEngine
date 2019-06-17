#version 150 core

in vec2 in_Position;
in vec2 in_TextureCoord;

out vec2 pass_TextureCoord;

void main(void) {
	gl_Position = vec4(in_Position, 0, 1);
	pass_TextureCoord = in_TextureCoord;
}