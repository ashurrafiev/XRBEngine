#version 150 core

in vec4 in_Position;
in float in_Size;

out float pass_Size;

void main(void) {
	gl_Position = in_Position;
	gl_PointSize = in_Size;
	pass_Size = in_Size;
}