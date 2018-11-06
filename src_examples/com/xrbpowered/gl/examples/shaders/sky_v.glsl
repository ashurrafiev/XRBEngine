#version 150 core

in vec3 in_Position;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec3 pass_TexCoord;

void main()
{
	gl_Position = projectionMatrix * viewMatrix * vec4(in_Position * 50, 1.0);  
	pass_TexCoord = in_Position;
}
