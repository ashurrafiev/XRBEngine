#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float screenHeight = 1;

in vec3 in_Position;
in float in_Size;

out vec4 out_Position;
out float out_Size;

void main(void) {
	out_Position = projectionMatrix * viewMatrix * vec4(in_Position, 1);
//	gl_Position = out_Position;
	out_Size = in_Size * screenHeight / out_Position.w;
}