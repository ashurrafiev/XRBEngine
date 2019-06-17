package com.xrbpowered.gl.scene;

import java.util.ArrayList;

import com.xrbpowered.gl.res.shaders.Shader;

public class ShaderEnvironment<T extends Shader> {

	protected ArrayList<T> shaders = new ArrayList<>();
	
	public void addShader(T shader) {
		shaders.add(shader);
	}
	
	public void removeShader(T shader) {
		shaders.remove(shader);
	}

}
