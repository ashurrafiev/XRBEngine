package com.xrbpowered.gl.examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.sprites.SpriteLayer;
import com.xrbpowered.gl.res.sprites.SpriteShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.FileAssetManager;

public class GLSprites extends ExampleClient {

	public static final int MAX_SPRITES = 10000;
	public static final int PARTICLE_SIZE = 64;
	public static final float SPAWN_TIME = 0.0005f;
	
	private static Random random = new Random();
	private static int viewWidth, viewHeight;
	
	private static class Particle {
		public float x, y, a;
		public float dx, dy, da;
		public float scale;
		public float t;
		public Vector4f color;
		
		public Particle(float x, float y, float dir) {
			this.t = 0f;
			this.x = x;
			this.y = y;
			float speed = random.nextFloat()*100f+10f;
			this.dx = speed * (float)(Math.cos(dir*Math.PI/180.0));
			this.dy = -speed * (float)(Math.sin(dir*Math.PI/180.0));
			this.a = random.nextFloat() * (float)(Math.PI * 2.0);
			this.da = (random.nextFloat() - 0.5f) * (float)(Math.PI * 2.0);
			this.scale = random.nextFloat()*0.8f + 0.2f;
			float c = random.nextFloat();
			this.color = new Vector4f(1f, (float)Math.sqrt(c)*0.5f+0.5f, c*c, 1f);
		}
		
		public boolean update(int index, SpriteLayer sprites, float dt) {
			t += dt;
			x += dx*dt;
			y += dy*dt;
			dy += 50f*dt;
			a += da*dt;
			if(x>-PARTICLE_SIZE && x<viewWidth+PARTICLE_SIZE && y>-PARTICLE_SIZE && y<viewHeight+PARTICLE_SIZE) {
				color.w = (t<1f) ? t : 1f; 
				sprites.setSprite(index, x, y, PARTICLE_SIZE, PARTICLE_SIZE, a, scale * color.w, PARTICLE_SIZE/2, PARTICLE_SIZE/2, color);
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	private ArrayList<Particle> particles = new ArrayList<>();	
	private SpriteLayer sprites;
	
	public GLSprites() {
		CLEAR_COLOR = new Color(0.1f, 0.1f, 0.3f);
		settings.multisample = 0;
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		init("Sprites").run();
	}
	
	@Override
	protected String getHelpString() {
		return formatHelpOnKeys(new String[] {
				"<b>F1</b>|Toggle FPS limit and VSync",
		});
	}
	
	@Override
	protected void setupResources() {
		super.setupResources();
		sprites = new SpriteLayer(MAX_SPRITES, new BufferTexture(PARTICLE_SIZE, PARTICLE_SIZE, false, true, false) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				BufferTexture.clearBuffer(g2, PARTICLE_SIZE, PARTICLE_SIZE);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.WHITE);

				int n = 8;
				int[] x = new int[n*2];
				int[] y = new int[n*2];
				for(int i=0; i<n; i++) {
					x[i*2] = (int)(32.0+30.0*Math.cos((double)i * Math.PI * 2.0 / (double)n));
					y[i*2] = (int)(32.0-30.0*Math.sin((double)i * Math.PI * 2.0 / (double)n));
					x[i*2+1] = (int)(32.0+10.0*Math.cos(((double)i + 0.5) * Math.PI * 2.0 / (double)n));
					y[i*2+1] = (int)(32.0-10.0*Math.sin(((double)i + 0.5) * Math.PI * 2.0 / (double)n));
				}
				g2.fillPolygon(x, y, n*2);
				
				return false;
			}
		});
		viewWidth = Display.getWidth();
		viewHeight = Display.getHeight();
		SpriteShader.getInstance().resize();
	}
	
	private boolean addParticle() {
		if(particles.size()<MAX_SPRITES) {
			Particle p = new Particle(random.nextFloat()*viewWidth, random.nextFloat()*viewHeight, random.nextFloat()*360f);
			particles.add(p);
			p.update(particles.size()-1, sprites, 0f);
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void destroyResources() {
		super.destroyResources();
		sprites.destroy();
	}
	
	@Override
	protected void resizeResources() {
		super.resizeResources();
		viewWidth = Display.getWidth();
		viewHeight = Display.getHeight();
		SpriteShader.getInstance().resize();
	}
	
	private float nextSprite = 0f;
	
	@Override
	protected void drawObjects(RenderTarget target, float dt) {
		for(int i=0; i<particles.size(); i++) {
			if(!particles.get(i).update(i, sprites, dt)) {
				particles.remove(i--);
			}
		}
		nextSprite += dt;
		while(nextSprite>=SPAWN_TIME && addParticle()) {
			nextSprite -= SPAWN_TIME;
		}
		sprites.update(particles.size());
		sprites.draw(particles.size());
		uiDebugTitle = particles.size() + " quad sprites";
	}
	
	public static void main(String[] args) {
		new GLSprites();
	}


}
