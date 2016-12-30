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
package com.xrbpowered.utils.assets;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.xrbpowered.utils.IOUtils;

public abstract class AssetManager {

	protected AssetManager fallback = null;
	
	public AssetManager(AssetManager fallbackAssets) {
		this.fallback = fallbackAssets;
	}
	
	public InputStream openStream(String path) throws IOException {
		try {
			return open(path);
		}
		catch(IOException e) {
			if(fallback!=null)
				return fallback.openStream(path);
			else
				throw e;
		}
	}
	
	protected abstract InputStream open(String path) throws IOException;

	public byte[] loadBytes(String path) throws IOException {
		return IOUtils.loadBytes(openStream(path));
	}
	
	public String loadString(String path) throws IOException {
		return IOUtils.loadString(openStream(path));
	}
	
	public BufferedImage loadImage(String path) throws IOException {
		return IOUtils.loadImage(openStream(path));
	}
	
	public Font loadFont(String path) throws IOException {
		return IOUtils.loadFont(openStream(path));
	}
	
	public static AssetManager defaultAssets = new FileAssetManager(null, null);
	
}
