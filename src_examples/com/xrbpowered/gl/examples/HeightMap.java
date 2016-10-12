package com.xrbpowered.gl.examples;
import java.util.Random;

import com.xrbpowered.utils.MathUtils;

public class HeightMap {
	
	public final int size;
	public Random random = new Random();
	public float[][] hmap;
	
	public HeightMap(int size) {
		this.size = size;
		this.hmap= new float[size+1][size+1];
		clear(0f);
	}

	public void clear(float h) {
		for(int x=0; x<size+1; x++)
			for(int y=0; y<size+1; y++) {
				hmap[x][y] = h;
			}
	}

	public void add(float[][] h) {
		for(int x=0; x<size+1; x++)
			for(int y=0; y<size+1; y++) {
				hmap[x][y] += h[x][y];
			}
	}

	public void generateNoise(float[][] hmap, int step, float mean, float amp) {
		for(int x=0; x<size+1; x+=step)
			for(int y=0; y<size+1; y+=step) {
				hmap[x][y] = (float)random.nextDouble()*amp - amp*0.5f + mean;
			}
	}

	public void interpolate(float[][] hmap, int step, boolean cos) {
		for(int x=0; x<size; x+=step)
			for(int y=0; y<size; y+=step) {
				for(int x1=0; x1<=step; x1++)
					for(int y1=0; y1<=step; y1++) {
						float sx = x1 / (float) step;
						float sy = y1 / (float) step;
						if(cos) {
							sx = MathUtils.cosInt(sx);
							sy = MathUtils.cosInt(sy);
						}
						hmap[x+x1][y+y1] = MathUtils.lerp(
								MathUtils.lerp(hmap[x][y], hmap[x][y+step], sy),
								MathUtils.lerp(hmap[x+step][y], hmap[x+step][y+step], sy),
								sx);
					}
			}
	}
	
	public void generatePerlin(int steps, float mean, float amp, float damp, boolean cos) {
		int w = 4;
		clear(mean);
		for(int s=0; s<steps; s++) {
			float[][] h = new float[size+1][size+1];
			generateNoise(h, w, 0f, amp);
			if(w>1) {
				interpolate(h, w, cos);
			}
			add(h);
			w *= 2;
			amp *= damp;
		}
	}

	public void makeSteps(float step) {
		for(int x=0; x<size+1; x++)
			for(int y=0; y<size+1; y++) {
				hmap[x][y] = (float)Math.floor(hmap[x][y]/step)*step;
			}
	}
	
}
