package com.xrbpowered.gl.scene;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.Client;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;

public class Screenshot {

	public BufferedImage image;
	
	public Screenshot(Client client, int w, int h) {
		RenderTarget target = new OffscreenBuffers(w, h, true);
		target.use();
		client.render(target);
		
		ByteBuffer pixels = ByteBuffer.allocateDirect(w*h*4);
		GL11.glReadPixels(0, 0, w, h, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		pixels.rewind();
	    int[] rgb = new int[pixels.remaining()];
	    for(int i=0; pixels.hasRemaining(); i++) {
	    	rgb[i] = (int)pixels.get() & 0xff;
	    }
	    
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		raster.setPixels(0, 0, w, h, rgb);
	}
	
	public Screenshot(Client client) {
		this(client, Display.getWidth(), Display.getHeight());
	}
	
	public boolean save(String filePath, String fileName) {
		try {
			File f = new File(filePath, fileName);
			ImageIO.write(image, "PNG", f);
			System.out.println("Screenshot saved to: "+f.getAbsolutePath());
			return true;
		}
		catch(IOException e) {
			return false;
		}
	}

	public boolean save(String filePath) {
		return save(filePath, String.format("%d.png", System.currentTimeMillis()));
	}

}
