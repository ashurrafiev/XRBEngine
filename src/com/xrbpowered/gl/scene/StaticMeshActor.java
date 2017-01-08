/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2016 Ashur Rafiev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.xrbpowered.gl.scene;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.textures.Texture;

public class StaticMeshActor extends Actor {

	private StaticMesh mesh = null;
	private ActorShader shader = null;
	private Texture[] textures = null;
	
	public StaticMeshActor(Scene scene) {
		super(scene);
	}
	
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
	
	public static StaticMeshActor make(Scene scene, final StaticMesh mesh, final ActorShader shader, final Texture diffuse, final Texture specular, final Texture normal) {
		StaticMeshActor actor =  new StaticMeshActor(scene);
		actor.setMesh(mesh);
		actor.setShader(shader);
		actor.setTextures(new Texture[] {diffuse, specular, normal});
		return actor;
	}
}
