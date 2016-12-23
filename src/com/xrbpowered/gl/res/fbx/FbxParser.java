package com.xrbpowered.gl.res.fbx;

import java.io.File;
import java.io.IOException;

import com.xrbpowered.utils.parser.RecursiveDescentParser;

public class FbxParser extends RecursiveDescentParser<FbxToken, FbxTable> {

	private static final FbxToken OPEN_BRACE = new FbxToken('{');
	private static final FbxToken CLOSE_BRACE = new FbxToken('}');
//	private static final FbxToken COLON = new FbxToken(':');
	private static final FbxToken COMMA = new FbxToken(',');
	
	public FbxParser() {
		super(new FbxTokeniser());
	}
	
	@Override
	protected void error(String msg) {
		super.error(msg);
		System.exit(1);
	}

	private void value(FbxArray array) {
		boolean req = false;
		for(;;) {
			switch(token.type) {
				case CONST:
				case STRING:
					array.addValue(new FbxValue(FbxType.STRING, token.value));
					break;
				case BOOL:
					array.addValue(new FbxValue(FbxType.BOOL, token.value));
					break;
				case INT:
					array.addValue(new FbxValue(FbxType.INT, token.value));
					break;
				case LONG:
					array.addValue(new FbxValue(FbxType.LONG, token.value));
					break;
				case FLOAT:
					array.addValue(new FbxValue(FbxType.FLOAT, token.value));
					break;
				default:
					if(req) {
						error("Expected value");
						return;
					}
					return;
			}
			next();
			if(!token.equals(COMMA))
				return;
			next();
			req = true;
		}
	}
	
	private void entry(FbxTable table) {
		if(token.type!=FbxToken.Type.KEY) {
			error("Expected key");
			next();
			return;
		}
		String name = token.asString();
		next();
/*		if(!token.equals(COLON)) {
			error("Expected colon \':\'");
			next();
			return;
		}
		next();*/
		
		FbxArray value = new FbxArray();
		value(value);
		
		if(token.equals(OPEN_BRACE)) {
			next();
			value.addTable(innerTable(new FbxTable()));
			next();
		}
		
		table.addEntry(name, value);
	}
	
	private FbxTable innerTable(FbxTable table) {
		while(token!=null && !token.equals(CLOSE_BRACE)) {
			entry(table);
		}
		return table;
	}
	
	@Override
	protected FbxTable top() {
		return innerTable(new FbxTable());
	}
	
	public static void main(String[] args) {
		try {
			new FbxParser().parse(new File("../Greenhouse/assets_src/greenhouse/wall_frame.fbx"));
			System.out.println("Done");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
