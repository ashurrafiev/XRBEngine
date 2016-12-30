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
package com.xrbpowered.utils.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Tokeniser<T> {	
	
	public static class UnknownTokenException extends RuntimeException {
	}
	
	private String source = null;
	private int end = 0;
	private int index = 0;
	private int lineIndex = 1;
	private String lastSymbol = null;

	private Pattern[] patterns;
	private Matcher[] matchers;
	
	public Tokeniser(Pattern[] patterns) {
		this.patterns = patterns;
		matchers = new Matcher[patterns.length];
	}
	
	protected abstract T evaluateToken(int match, String raw);
	
	public void start(File file) throws FileNotFoundException, IOException {
		FileInputStream f = new FileInputStream(file);
		byte[] buf = new byte[f.available()];
		f.read(buf);
		f.close();
		start(new String(buf));
	}
	
	public void start(String string) {
		this.source = string;
		index = 0;
		lineIndex = 1;
		end = source.length();
		for(int i=0; i<patterns.length; i++) {
			matchers[i] = patterns[i].matcher(source);
		}
	}
	
	public int getIndex() {
		return index;
	}
	
	public void rollBackTo(int index) {
		this.index = index;
	}

	private void incLineIndex(String raw) {
		lineIndex += raw.split("\\n", raw.length()).length-1;
	}
	
	public int getLineIndex() {
		return lineIndex;
	}
	
	public String getLastSymbol() {
		return lastSymbol;
	}
	
	public T getNextToken() throws UnknownTokenException {
		if(index>=source.length())
			return null;
		int match = -1;
		for(int i=0; i<matchers.length; i++) {
			matchers[i].region(index, end);
			if(matchers[i].lookingAt()) {
				match = i;
				break;
			}
		}
		if(match>=0) {
			lastSymbol = matchers[match].group();
			incLineIndex(lastSymbol);
			T t = evaluateToken(match, lastSymbol);
			index = matchers[match].end();
			if(t==null)
				return getNextToken();
			else
				return t;
		}
		else
			throw(new UnknownTokenException());
	}
}
