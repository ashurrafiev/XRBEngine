package com.xrbpowered.gl.res.textures;

public class TextureCache extends AbstractTextureCache<String> {

	public Texture get(String key, boolean wrap, boolean filter) {
		Texture t = cache.get(key);
		if(t==null) {
			t = new Texture(key, wrap, filter);
			cache.put(key, t);
		}
		return t;
	}

	@Override
	protected Texture createForKey(String key) {
		return new Texture(key);
	}

}
