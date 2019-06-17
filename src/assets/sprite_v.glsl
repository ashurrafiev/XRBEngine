#version 150 core

uniform vec2 screenSize;
uniform mat4 viewMatrix;

in vec2 in_Position;

in vec2 ins_Position;
in vec2 ins_Size;
in vec2 ins_RotationScale;
in vec2 ins_TexCoord;
in vec4 ins_Color;

out vec2 pass_TexCoord;
out vec4 pass_Color;

mat2 rotationMatrix(float a) {
	mat2 m = mat2(1);
	m[0][0] = cos(a);
	m[0][1] = sin(a);
	m[1][0] = -m[0][1];
	m[1][1] = m[0][0];
	return m;
}

void main(void) {
	vec2 pos = rotationMatrix(ins_RotationScale.x) * ins_RotationScale.y * in_Position * ins_Size + ins_Position;
	vec4 vpos = viewMatrix * vec4(pos.x - screenSize.x/2.0, -pos.y + screenSize.y/2.0, 0.0, 1.0);
	
	gl_Position = vec4(
		vpos.x * 2.0 / screenSize.x,
		vpos.y * 2.0 / screenSize.y,
		0.0, 1.0);
	
	pass_TexCoord = in_Position * ins_Size + ins_TexCoord;
	pass_Color = ins_Color;
}