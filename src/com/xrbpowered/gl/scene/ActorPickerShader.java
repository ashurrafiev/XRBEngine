package com.xrbpowered.gl.scene;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.shaders.VertexInfo;

public class ActorPickerShader extends ActorShader {
	
	private ActorPickerShader() {
		super(new VertexInfo().addFloatAttrib("in_Position", 3), "pick_v.glsl", "pick_f.glsl");
	}
	
	private int objIdLocation;
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		objIdLocation = GL20.glGetUniformLocation(pId, "objId");
	}
	
	public void updateUniforms(Actor actor, int objId) {
		setActor(actor);
		updateUniforms();
		GL20.glUniform3f(objIdLocation, (float)((objId>>16)&0xff) / 255f, (float)((objId>>8)&0xff) / 255f, (float)(objId&0xff) / 255f);
	}
	
	private static ActorPickerShader instance = null;
	
	public static ActorPickerShader getInstance() {
		if(instance==null)
			instance = new ActorPickerShader();
		return instance;
	}
	
	public static void destroyInstance() {
		if(instance!=null) {
			instance.destroy();
			instance = null;
		}
	}
}
