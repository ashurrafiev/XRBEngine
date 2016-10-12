package com.xrbpowered.gl.examples;
import java.awt.Color;import java.awt.Graphics2D;import org.lwjgl.input.Keyboard;import org.lwjgl.input.Mouse;import org.lwjgl.opengl.EXTTextureFilterAnisotropic;import org.lwjgl.opengl.GL11;import com.xrbpowered.gl.Client;import com.xrbpowered.gl.res.StandardMeshBuilder;import com.xrbpowered.gl.res.StaticMesh;import com.xrbpowered.gl.res.buffers.RenderTarget;import com.xrbpowered.gl.res.shaders.StandardShader;import com.xrbpowered.gl.res.textures.BufferTexture;import com.xrbpowered.gl.res.textures.Texture;import com.xrbpowered.gl.scene.ActorPicker;import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLBasicTerrain extends ExampleClient {

	private StaticMesh terrain;
	private StaticMesh water;
	private StaticMesh obj;
	private Texture diffuse;
	private Texture diffuseAnis;	private Texture specular;
	private Texture normal;	private Texture waterNormal;	private Texture[] terrainTex;	private Texture[] terrainTexAnis;		private StaticMeshActor terrainActor;
	private StaticMeshActor waterActor;
	private StaticMeshActor objActor;
	
	private boolean rotating = false;
	private boolean anis = false;	private float time = 0f;	
	private void createTerrainMesh() {
		long t = System.currentTimeMillis();
		HeightMap map = new HeightMap(64);
		map.generatePerlin(5, 0f, 0.4f, 2.5f, true);
		// TODO multi-texture terrain
		terrain = StandardMeshBuilder.terrain(32f, map.hmap, 8);
		System.out.printf("Mesh generated in %d ms\n", (System.currentTimeMillis() - t));
		
		// TODO decals
	}
	
	@Override
	protected void setupResources() {		super.setupResources();
		specular = new Texture("ice2a.jpg");		
		diffuse = new Texture("ice1a2.jpg");		diffuseAnis = new Texture("ice1a2.jpg");		float anis = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);		System.out.printf("Max anisotropy: %.1f\n", anis);		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anis);				normal = new Texture("ice_n.jpg");		waterNormal = new Texture("water2_n.jpg");
				createTerrainMesh();
		obj = StandardMeshBuilder.sphere(2f, 16);
		water = StandardMeshBuilder.plane(32f, 16, 8);
		pickObjects = new StaticMesh[] {null, terrain, obj};
				terrainTex = new Texture[] {diffuse, specular, plainNormalTexture};		terrainTexAnis = new Texture[] {diffuseAnis, specular, plainNormalTexture};		terrainActor = StandardMeshBuilder.makeActor(scene, terrain, diffuse, specular, plainNormalTexture);
		terrainActor.updateTransform();
		
		waterActor = StandardMeshBuilder.makeActor(scene, water,
				BufferTexture.createPlainColor(4, 4, new Color(0x5577bb)),
				BufferTexture.createPlainColor(4, 4, new Color(0x999999)),				waterNormal);
		waterActor.updateTransform();	
		
		objActor = StandardMeshBuilder.makeActor(scene, obj, diffuse, specular, normal);
		objActor.position.setY(2f);
		objActor.updateTransform();				StandardShader.environment.ambientColor.set(0.0f, 0.1f, 0.25f);		StandardShader.environment.lightColor.set(0.9f, 0.85f, 0.8f);
	}		@Override	protected boolean updateDebugInfoBuffer(Graphics2D g2, int w, int h) {		uiDebugTitle = objectNames[pickObject];		uiDebugInfo = pickObjects==null || pickObjects[pickObject]==null ? "" : String.format("Triangles: %d", pickObjects[pickObject].countTris());		super.updateDebugInfoBuffer(g2, w, h);				g2.setColor(Color.BLACK);		String fps = String.format("Color: %08x", pickColor);		g2.drawString(fps, 120, 56);		g2.setColor(Color.WHITE);		g2.drawString(fps, 119, 55);		g2.setColor(new Color(pickColor));		g2.fillRect(220, 44, 15, 15);		return true;	}
	
	private static final String[] objectNames = {"", "Terrain", "Sphere"};
	private int pickColor = 0;
	private int pickObject = 0;
	private StaticMesh[] pickObjects;
//	private ByteBuffer pixels = ByteBuffer.allocateDirect(4); 
		@Override	protected void updateControllers(float dt) {		super.updateControllers(dt);				if(rotating) {			terrainActor.rotation.y += ((float)(Math.PI / 18f)) * dt;			waterActor.rotation.y = terrainActor.rotation.y;			terrainActor.updateTransform();			waterActor.updateTransform();		}	}		@Override	protected void keyDown(int key) {		switch (Keyboard.getEventKey()) {			case Keyboard.KEY_F3:				rotating = !rotating;				break;			case Keyboard.KEY_F4:				terrain.destroy();				createTerrainMesh();				terrainActor.setMesh(terrain);				break;			case Keyboard.KEY_F5:				anis = !anis;				terrainActor.setTextures(anis ? terrainTexAnis : terrainTex);				break;			default:				super.keyDown(key);		}	}		@Override	protected void drawObjects(RenderTarget target, float dt) {		ActorPicker.instance.startPicking(Mouse.getX(), Mouse.getY(), RenderTarget.primaryBuffer);		ActorPicker.instance.drawActor(terrainActor, 1);		ActorPicker.instance.drawActor(objActor, 2);		pickObject = ActorPicker.instance.finishPicking(target);		Client.checkError();				time += dt;		StandardShader.environment.time = 0f;		terrainActor.draw();		objActor.draw();				StandardShader.getInstance().alpha = 0.5f;		StandardShader.environment.time = time;		GL11.glEnable(GL11.GL_BLEND);		waterActor.draw();		StandardShader.getInstance().alpha = 1f;		GL11.glDisable(GL11.GL_BLEND);		//		GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);//		pickColor = pixels.asIntBuffer().get(0) >> 8;//		Client.checkError();	}
	
	@Override
	protected void destroyResources() {
		diffuse.destroy();
		diffuseAnis.destroy();		specular.destroy();		normal.destroy();		waterNormal.destroy();
		terrain.destroy();
	}

	public static void main(String[] args) {
		new GLBasicTerrain().init("GLExample").run();
	}

}
