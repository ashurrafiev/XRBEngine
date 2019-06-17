#version 150 core

out vec4 out_Color;

in float pass_Size;

void main(void) {
	float d = distance(gl_PointCoord, vec2(0.5, 0.5));
	float delta = fwidth(d);

	float a = 1 - smoothstep(0.5-delta, 0.5, d);
	if(pass_Size<8)
		a -= (8 - pass_Size) * 0.12;
	if(a<0.1) discard;
	if(d<0.48) {
		d = 1 - 4 * d * d;
		float d2 = max(1 - 7.5 * distance(gl_PointCoord, vec2(0.3, 0.3)), 0);
		a =  clamp(a - d + 0.5 * d2, 0, 1);
	}

//	float c = 1 - smoothstep(0.4-delta, 0.4, d);
//	out_Color = vec4(c, c, c, a);

	out_Color = vec4(1 - gl_PointCoord.x * 0.5, 1 - gl_PointCoord.y * 0.5, 1, a);
}
