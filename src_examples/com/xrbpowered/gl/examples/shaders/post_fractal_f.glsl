#version 150 core

uniform sampler2D palette;
uniform int maxi = 256;
uniform float aspect = 1;
uniform float zoom = 1;
uniform vec2 pivot = vec2(-1.15, -0.25);

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	float x0 = pivot.x+(pass_TextureCoord.x*2.0 - 1.0)*aspect*zoom;
	float y0 = pivot.y+(pass_TextureCoord.y*2.0 - 1.0)*zoom;
	
	float x = 0.0;
	float y = 0.0;
	float xtemp;
	int i = 0;
	while((x*x+y*y <100.0) && (i<maxi)) {
		xtemp = x*x - y*y + x0;
		y = 2*x*y + y0;
		x = xtemp;
		i++;
	}
	
	float c; // = float(i)/float(maxi);
	if(i<maxi) {
		float logzn = log(x*x+y*y) / 2.0;
		float nu = log(logzn/log(2.0)) / log(2.0);
		c = (float(i+1) - nu) / float(maxi);
	}
	else {
		c = 1.0;
	}
	out_Color = texture(palette, vec2(c, 0));
	// out_Color = vec4(c, c, c, 1);
}