package com.xrbpowered.gl.res.fbx;

public class FbxValue {

	public final FbxType type;
	public final Object value;
	
	public FbxValue(FbxType type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	public boolean toBool() {
		return (Boolean) value;
	}
	
	public int toInt() {
		return (Integer) value;
	}
	
	public long toLong() {
		if(type==FbxType.INT)
			return (Integer) value;
		else
			return (Long) value; 
	}
	
	public float toFloat() {
		return (Float) value;
	}
	
}
