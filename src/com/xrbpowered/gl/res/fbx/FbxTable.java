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
