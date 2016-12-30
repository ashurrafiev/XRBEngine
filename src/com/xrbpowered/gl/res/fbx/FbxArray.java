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

public class FbxArray {

	private FbxTable table = null;
	
	private ArrayList<FbxValue> array = new ArrayList<>();
	private FbxType type = null;
	
	public void addValue(FbxValue value) {
		array.add(value);
		if(type==null)
			type = value.type;
		else if(type!=value.type)
			type = FbxType.MIXED;
	}
	
	public FbxType getType() {
		return type;
	}
	
	public FbxValue get(int index) {
		return array.get(index);
	}
	
	public int size() {
		return array.size();
	}
	
	public String[] getAll(String[] a) {
		int n = array.size();
		if(a==null || a.length<n)
			a = new String[n];
		for(int i=0; i<n; i++) {
			FbxValue v = array.get(i);
			a[i] = v.toString();
		}
		return a;
	}

	public boolean[] getAll(boolean[] a) {
		int n = array.size();
		if(a==null || a.length<n)
			a = new boolean[n];
		for(int i=0; i<n; i++) {
			FbxValue v = array.get(i);
			a[i] = v.toBool();
		}
		return a;
	}

	public int[] getAll(int[] a) {
		int n = array.size();
		if(a==null || a.length<n)
			a = new int[n];
		for(int i=0; i<n; i++) {
			FbxValue v = array.get(i);
			a[i] = v.toInt();
		}
		return a;
	}

	public long[] getAll(long[] a) {
		int n = array.size();
		if(a==null || a.length<n)
			a = new long[n];
		for(int i=0; i<n; i++) {
			FbxValue v = array.get(i);
			a[i] = v.toLong();
		}
		return a;
	}

	public float[] getAll(float[] a) {
		int n = array.size();
		if(a==null || a.length<n)
			a = new float[n];
		for(int i=0; i<n; i++) {
			FbxValue v = array.get(i);
			a[i] = v.toFloat();
		}
		return a;
	}

	public void addTable(FbxTable table) {
		this.table = table;
	}
	
	public FbxTable getTable() {
		return table;
	}
	
}
