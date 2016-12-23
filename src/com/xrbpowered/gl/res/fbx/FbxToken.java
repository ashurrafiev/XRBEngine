package com.xrbpowered.gl.res.fbx;

public class FbxToken {

	public enum Type {
		CHAR,
		BOOL,
		INT,
		LONG,
		FLOAT,
		STRING,
		CONST,
		KEY
	}
	
	public final Type type;
	public final Object value;
	
	public FbxToken(Type type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public FbxToken(char ch) {
		this.type = Type.CHAR;
		value = ch;
	}
	
	public String asString() {
		return (String)value;
	}

	@Override
	public boolean equals(Object obj) {
		return equals((FbxToken) obj);
	}
	
	public boolean equals(FbxToken t) {
		return t!=null && type==t.type &&
			(type!=Type.CHAR || value.equals(t.value));
	}
	
}
