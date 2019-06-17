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
