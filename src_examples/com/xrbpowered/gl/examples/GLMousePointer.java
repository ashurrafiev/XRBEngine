package com.xrbpowered.gl.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.builder.FastMeshBuilder;
import com.xrbpowered.gl.res.shaders.ActorShader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLMousePointer extends ExampleClient {

	public GLMousePointer() {
		init("GL Mouse Pointer").run();
	}
	
	public static class SceneUIShader extends ActorShader {
		public SceneUIShader() {
			super(StandardShader.standardVertexInfo, "std_v.glsl", "ui_f.glsl");
		}
		@Override
		protected void storeUniformLocations() {
			super.storeUniformLocations();
			initSamplers(new String[] {"tex"});
		}
		@Override
		public void updateUniforms() {
			super.updateUniforms();
			GL20.glUniform1f(GL20.glGetUniformLocation(pId, "alpha"), 1f);
		}
		@Override
		public void use() {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			super.use();
		}
		@Override
		public void unuse() {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			super.unuse();
		}
	}
	
	public static class BlankShader extends ActorShader {
		public BlankShader() {
			super(StandardShader.standardVertexInfo, "std_v.glsl", "blank_f.glsl");
		}
		public void setColor(Vector4f color) {
			GL20.glUseProgram(pId);
			uniform(GL20.glGetUniformLocation(pId, "color"), color);
		}
	}
	
	private SceneUIShader sceneUIShader;
	private BlankShader blankShader;
	
	private StaticMeshActor planeActor;
	private StaticMesh plane;
	private Texture grid;
	private StaticMesh pointer;
	private StaticMeshActor pointerActor;
	private Vector4f pointerColor = new Vector4f(1, 0, 0, 1);
	
	@Override
	protected void setupResources() {
		super.setupResources();
		
		sceneUIShader = new SceneUIShader();
		blankShader = new BlankShader();
		
		grid = new BufferTexture(512, 512, true, true, false) {
			@Override
			protected boolean updateBuffer(Graphics2D g2, int width, int height) {
				g2.setBackground(new Color(0x00ffffff, true));
				g2.clearRect(0, 0, width, height);
				BasicStroke thickStroke = new BasicStroke(4f);
				BasicStroke midStroke = new BasicStroke(2f);
				BasicStroke thinStroke = new BasicStroke(1f);
				
				Color awhite = new Color(0x33ffffff, true);
				for(int d=0; d<=width; d+=32) {
					g2.setStroke(d%512==0 ? thickStroke : d%256==0 ? midStroke : thinStroke);
					g2.setColor(d%64==0 ? Color.WHITE : awhite);
					g2.drawLine(d, 0, d, height);
					g2.drawLine(0, d, width, d);
				}
				return true;
			}
		};
		
		plane = FastMeshBuilder.plane(8f, 1, 8, StandardShader.standardVertexInfo, null);
		planeActor = StaticMeshActor.make(scene, plane, sceneUIShader, grid, plainSpecularTexture, plainNormalTexture);
		
		pointer = FastMeshBuilder.sphere(0.025f, 8, StandardShader.standardVertexInfo, null);
		pointerActor = StaticMeshActor.make(scene, pointer, blankShader, plainSpecularTexture, plainSpecularTexture, plainNormalTexture);

		scene.activeCamera.position.y = 0.25f;
		scene.activeCamera.updateTransform();
	}

	@Override
	protected void destroyResources() {
		grid.destroy();
		plane.destroy();
		sceneUIShader.destroy();
	}

	private Vector3f dir = new Vector3f();
	
	@Override
	protected void drawObjects(RenderTarget target) {
		planeActor.draw();

		if(!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
			Vector3f cp = scene.activeCamera.position;
			scene.activeCamera.getDir(dir, Mouse.getX(), Mouse.getY(), Display.getWidth(), Display.getHeight());
			if(dir.y!=0 && cp.y!=0) {
				float px = cp.x - cp.y*dir.x/dir.y;
				float pz = cp.z - cp.y*dir.z/dir.y;
				pointerActor.position.x = px;
				pointerActor.position.z = pz;
				pointerActor.updateTransform();
				
				blankShader.setColor(pointerColor);
				pointerActor.draw();
			}
		}
	}
	
	public static void main(String[] args) {
		new GLMousePointer();
	}
}
