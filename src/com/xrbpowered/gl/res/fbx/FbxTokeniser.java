package com.xrbpowered.gl.res.fbx;

import java.util.regex.Pattern;

import com.xrbpowered.utils.parser.Tokeniser;

public class FbxTokeniser extends Tokeniser<FbxToken> {

	public FbxTokeniser() {
		super(new Pattern[] {
				Pattern.compile("\\s+", Pattern.MULTILINE+Pattern.DOTALL), // 0: whitespace
				Pattern.compile("\\;.*?$", Pattern.MULTILINE+Pattern.DOTALL), // 1: comment
				Pattern.compile("\\-?\\d+\\.\\d+"), // 2: float number
				Pattern.compile("\\-?\\d+"), // 3: int number
				Pattern.compile("0x[A-Fa-f0-9]+"), // 4: hex number
				Pattern.compile("\\\".*?\\\""), // 5: string
				Pattern.compile("[A-Za-z0-9_\\.]+\\:"), // 6: key
				Pattern.compile("[A-Za-z0-9_\\.]+"), // 7: constant
				Pattern.compile(".") // 8: symbol
			});
	}

	@Override
	protected FbxToken evaluateToken(int match, String raw) {
//		System.out.println("\t\t"+match+" : "+raw);
		switch(match) {
			case 0:
			case 1:
				return null;
			case 2:
				return new FbxToken(FbxToken.Type.FLOAT, Float.parseFloat(raw));
			case 3:
			{
				long n = Long.parseLong(raw);
				if(n>=Integer.MIN_VALUE && n<=Integer.MAX_VALUE)
					return new FbxToken(FbxToken.Type.INT, (int)n);
				else
					return new FbxToken(FbxToken.Type.LONG, n);
			}
			case 4:
				return new FbxToken(FbxToken.Type.INT, Integer.parseInt(raw.substring(2), 16));
			case 5:
				return new FbxToken(FbxToken.Type.STRING, raw.substring(1, raw.length()-1));
			case 6:
				return new FbxToken(FbxToken.Type.KEY, raw.substring(0, raw.length()-1));
			case 7:
				if(raw.equals("Y"))
					return new FbxToken(FbxToken.Type.BOOL, true);
				else if(raw.equals("N"))
					return new FbxToken(FbxToken.Type.BOOL, false);
				else
					return new FbxToken(FbxToken.Type.CONST, raw);
			default:
				return new FbxToken(raw.charAt(0));
		}
	}

}
