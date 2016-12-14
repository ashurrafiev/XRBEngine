package com.xrbpowered.gl.examples;
import java.awt.Color;import java.awt.Graphics2D;import org.lwjgl.input.Keyboard;import org.lwjgl.input.Mouse;import org.lwjgl.opengl.EXTTextureFilterAnisotropic;import org.lwjgl.opengl.GL11;import com.xrbpowered.gl.Client;import com.xrbpowered.gl.res.StandardMeshBuilder;import com.xrbpowered.gl.res.StaticMesh;import com.xrbpowered.gl.res.buffers.RenderTarget;import com.xrbpowered.gl.res.shaders.StandardShader;import com.xrbpowered.gl.res.textures.Texture;import com.xrbpowered.gl.scene.ActorPicker;import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLBasicTerrain extends ExampleClient {

	private StaticMesh terrain;
	private StaticMesh obj;
	private Texture diffuse;
	private Texture specular;
	private Texture normal;	private StaticMeshActor terrainActor;
	private StaticMeshActor objActor;
	
	private boolean rotating = false;
	private void createTerrainMesh() {
		long t = System.currentTimeMillis();
		HeightMap map = new HeightMap(64);
		map.generatePerlin(5, 0f, 0.4f, 2.5f, true);
		// TODO multi-texture terrain
		terrain = StandardMeshBuilder.terrain(32f, map.hmap, 8);
		System.out.printf("Mesh generated in %d ms\n", (System.currentTimeMillis() - t));
		
		// TODO decals
	}		@Override	protected String getHelpString() {		return formatHelpOnKeys(new String[] {				"<b>W</b> / <b>A</b> / <b>S</b> / <b>D</b>|Move around",				"<b>SPACE</b> / <b>LSHIFT</b>|Fly up/down",				"Hold <b>LMB</b>|Mouse look",				"Drag <b>RMB</b>|Move light source",				"<b>F1</b>|Toggle FPS limit and VSync",				"<b>F2</b>|Toggle wireframe",				"<b>F3</b>|Toggle terrain rotation",				"<b>F4</b>|Generate new terrain",		});	}
	
	@Override
	protected void setupResources() {		super.setupResources();
		specular = new Texture("ice2a.jpg");		
		diffuse = new Texture("ice1a2.jpg");		float anis = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);		System.out.printf("Max anisotropy: %.1f\n", anis);		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anis);				normal = new Texture("ice_n.jpg");				createTerrainMesh();
		obj = StandardMeshBuilder.sphere(2f, 16);
		pickObjects = new StaticMesh[] {null, terrain, obj};
				terrainActor = StandardMeshBuilder.makeActor(scene, terrain, diffuse, specular, plainNormalTexture);
		terrainActor.updateTransform();
		
		objActor = StandardMeshBuilder.makeActor(scene, obj, diffuse, specular, normal);
		objActor.position.setY(2f);
		objActor.updateTransform();				StandardShader.environment.ambientColor.set(0.0f, 0.1f, 0.25f);		StandardShader.environment.lightColor.set(0.9f, 0.85f, 0.8f);
	}		@Override	protected boolean updateDebugInfoBuffer(Graphics2D g2, int w, int h) {		uiDebugTitle = objectNames[pickObject];		uiDebugInfo = pickObjects==null || pickObjects[pickObject]==null ? "" : String.format("Triangles: %d", pickObjects[pickObject].countTris());		super.updateDebugInfoBuffer(g2, w, h);				g2.setColor(Color.BLACK);		String fps = String.format("Color: %08x", pickColor);		g2.drawString(fps, 120, 56);		g2.setColor(Color.WHITE);		g2.drawString(fps, 119, 55);		g2.setColor(new Color(pickColor));		g2.fillRect(220, 44, 15, 15);		return true;	}
	
	private static final String[] objectNames = {"", "Terrain", "Sphere"};
	private int pickColor = 0;
	private int pickObject = 0;
	private StaticMesh[] pickObjects;
//	private ByteBuffer pixels = ByteBuffer.allocateDirect(4); 
		@Override	protected void updateControllers(float dt) {		super.updateControllers(dt);				if(rotating) {			terrainActor.rotation.y += ((float)(Math.PI / 18f)) * dt;			terrainActor.updateTransform();		}	}		@Override	protected void keyDown(int key) {		switch (Keyboard.getEventKey()) {			case Keyboard.KEY_F3:				rotating = !rotating;				break;			case Keyboard.KEY_F4:				terrain.destroy();				createTerrainMesh();				terrainActor.setMesh(terrain);				break;			default:				super.keyDown(key);		}	}		@Override	protected void drawObjects(RenderTarget target, float dt) {		ActorPicker.instance.startPicking(Mouse.getX(), Mouse.getY(), RenderTarget.primaryBuffer);		ActorPicker.instance.drawActor(terrainActor, 1);		ActorPicker.instance.drawActor(objActor, 2);		pickObject = ActorPicker.instance.finishPicking(target);		Client.checkError();				terrainActor.draw();		objActor.draw();		//		GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);//		pickColor = pixels.asIntBuffer().get(0) >> 8;//		Client.checkError();	}
	
	@Override
	protected void destroyResources() {
		diffuse.destroy();
		specular.destroy();		normal.destroy();		terrain.destroy();
	}

	public static void main(String[] args) {
		new GLBasicTerrain().init("GLExample").run();
	}

}
