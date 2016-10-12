package com.xrbpowered.gl.scene;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.textures.Texture;

public abstract class StaticMeshActor extends Actor {

	private StaticMesh mesh = null;
	private ActorShader shader = null;
	private Texture[] textures = null;
	
	public StaticMeshActor(Scene scene) {
		super(scene);
		setup();
	}
	
	protected abstract void setup();
	
	public void setMesh(StaticMesh mesh) {
		this.mesh = mesh;
	}
	
	public StaticMesh getMesh() {
		return mesh;
	}
	
	public void setShader(ActorShader shader) {
		this.shader = shader;
	}
	
	public void setTextures(Texture[] textures) {
		this.textures = textures;
	}
	
	public void changeTexture(int index, Texture texture) {
		this.textures[index] = texture;
	}
	
	public void draw() {
		if(shader==null || mesh==null)
			return;
		shader.setActor(this);
		shader.use();
		if(textures!=null) {
			for(int i=0; i<textures.length; i++)
				textures[i].bind(i);
		}
		mesh.draw();
		shader.unuse();
	}
}
