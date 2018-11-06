#version 150 core

uniform samplerCube texSkyBox;

in vec3 pass_TexCoord;

out vec4 out_Color;

void main()
{    
    out_Color = texture(texSkyBox, pass_TexCoord);
}
