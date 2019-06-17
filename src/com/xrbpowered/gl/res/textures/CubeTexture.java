package com.xrbpowered.gl.res.textures;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import com.xrbpowered.gl.Client;
import com.xrbpowered.utils.assets.AssetManager;

public class CubeTexture extends Texture {

	public static final String[] FACE_NAMES = {"right", "left", "top", "bottom", "front", "back"};
	
	public CubeTexture(String pathFormat) {
		try {
			texId = GL11.glGenTextures();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
			
			IntBuffer buf = null;
			
			for(int i=0; i<6; i++) {
//				BufferedImage img = load(new FileInputStream(String.format("assets/"+pathFormat, FACE_NAMES[i])));
				BufferedImage img = AssetManager.defaultAssets.loadImage(String.format(pathFormat, FACE_NAMES[i]));
				buf = getPixels(img, buf);
				put(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, img.getWidth(), img.getHeight(), buf);
			}
			
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
			
			Client.checkError();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
