package com.xrbpowered.gl.res.textures;

import java.util.HashMap;

public abstract class AbstractTextureCache<T> {
	
	protected HashMap<T, Texture> cache = new HashMap<>();
	
	public Texture get(T key) {
		Texture t = cache.get(key);
		if(t==null) {
			t = createForKey(key);
			cache.put(key, t);
		}
		return t;
	}
	
	protected abstract Texture createForKey(T key);
	
	public void destroy() {
		for(Texture t : cache.values()) {
			t.destroy();
		}
		cache.clear();
	}

}
