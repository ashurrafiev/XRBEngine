package com.xrbpowered.gl.res.fbx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FbxTable {
	
	private HashMap<String, List<FbxArray>> map = new HashMap<>();
	
	public void addEntry(String name, FbxArray value) {
		List<FbxArray> list = map.get(name);
		if(list==null) {
			list = new ArrayList<>(1);
			map.put(name, list);
		}
		list.add(value);
	}
	
	public FbxArray get(String name) {
		return map.get(name).get(0);
	}

	public List<FbxArray> getAll(String name) {
		return Collections.unmodifiableList(map.get(name));
	}
	
	public int count(String name) {
		List<FbxArray> list = map.get(name);
		if(list==null)
			return 0;
		else
			return list.size();
	}
	
	public boolean contains(String name) {
		return count(name)>0;
	}
	
}
