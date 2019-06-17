package com.xrbpowered.gl.res.textures;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.xrbpowered.gl.Client;
import com.xrbpowered.utils.assets.AssetManager;

public class ArrayTexture {

	private int width, height;
	private int layers;
	private IntBuffer intBuffer;
	
	protected int texId;
	
	public ArrayTexture(int w, int h, int layers) {
		this.width = w;
		this.height = h;
		this.layers = layers;
		
		texId = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texId);
		intBuffer = ByteBuffer.allocateDirect(4 * w * h * layers).order(ByteOrder.nativeOrder()).asIntBuffer();
	}
	
	public ArrayTexture append(String path) {
		try {
			BufferedImage img = AssetManager.defaultAssets.loadImage(path);
			if(img.getWidth()!=width || img.getHeight()!=height)
				throw new Exception("Texture size mismatch "+path);
			int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);
			intBuffer.put(pixels);
			return this;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	
	public ArrayTexture finish(boolean wrap, boolean filter) {
		intBuffer.flip();
		GL12.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL11.GL_RGBA, width, height, layers, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);
		intBuffer = null;
		Texture.setProperties(GL30.GL_TEXTURE_2D_ARRAY, wrap, filter, Client.settings.anisotropy);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
		Client.checkError();
		return this;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLayers() {
		return layers;
	}
	
	public void bind(int index) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texId);
	}
	
	public void destroy() {
		GL11.glDeleteTextures(texId);
	}

}
