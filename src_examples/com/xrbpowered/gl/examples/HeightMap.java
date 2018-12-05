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
package com.xrbpowered.gl.examples;
import java.util.Random;

import com.xrbpowered.utils.MathUtils;
import com.xrbpowered.utils.RandomUtils;

public class HeightMap {
	
	public final int size;
	public Random random = new Random();
	public long seed;
	public long basex, basey;
	public float[][] hmap;

	public HeightMap(int size) {
		this(size, System.currentTimeMillis());
	}

	public HeightMap(int size, long seed) {
		this.size = size;
		this.seed = seed;
		this.hmap= new float[size+1][size+1];
		clear(0f);
	}
	
	public void setBaseXY(long x, long y) {
		this.basex = x;
		this.basey = y;
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
		generateNoise(hmap, false, step, mean, amp);
	}

	public void generateNoise(float[][] hmap, boolean wrap, int step, float mean, float amp) {
		for(int x=0; x<size+1; x+=step)
			for(int y=0; y<size+1; y+=step) {
				random.setSeed(RandomUtils.seedXY(seed+step, x+basex, y+basey));
				if(wrap && (x==size || y==size))
					hmap[x][y] = hmap[x==size ? 0 : x][y==size ? 0 : y];
				else
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
	
	public void generatePerlin(boolean wrap, float mean, float amp, float damp, boolean cos) {
		int w = 4;
		clear(mean);
		for(int s=size; s>2; s>>=1) {
			float[][] h = new float[size+1][size+1];
			generateNoise(h, wrap, w, 0f, amp);
			if(w>1) {
				interpolate(h, w, cos);
			}
			add(h);
			w *= 2;
			amp *= damp;
		}
	}

	public void generatePerlin(float mean, float amp, float damp, boolean cos) {
		generatePerlin(false, mean, amp, damp, cos);
	}

	public void makeSteps(float step) {
		for(int x=0; x<size+1; x++)
			for(int y=0; y<size+1; y++) {
				hmap[x][y] = (float)Math.floor(hmap[x][y]/step)*step;
			}
	}
	
	public void amplify(float power, float multiply, float add) {
		for(int x=0; x<size+1; x++)
			for(int y=0; y<size+1; y++) {
				hmap[x][y] = (float)Math.pow(Math.abs(hmap[x][y]), power)*Math.signum(hmap[x][y])*multiply+add;
			}
	}
	
	public static HeightMap blend(HeightMap m0, HeightMap m1, HeightMap s) {
		int size = s.size;
		if(m0.size!=size || m1.size!=size)
			throw new IllegalArgumentException("Map sizes do not match");
		HeightMap map = new HeightMap(size);
		for(int x=0; x<size+1; x++)
			for(int y=0; y<size+1; y++) {
				map.hmap[x][y] = MathUtils.lerp(m0.hmap[x][y], m1.hmap[x][y], s.hmap[x][y]);
			}
		return map;
	}
	
	public static HeightMap scale(HeightMap m, int step, boolean cos) {
		HeightMap map = new HeightMap(m.size*step);
		for(int x=0; x<m.size+1; x++)
			for(int y=0; y<m.size+1; y++) {
				map.hmap[x*step][y*step] = m.hmap[x][y];
			}
		map.interpolate(map.hmap, step, cos);
		return map;
	}
	
}
