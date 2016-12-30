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
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class RecursiveDescentParser<T, V> {

	protected Tokeniser<T> tokeniser;
	protected T token;
	protected T expectedToken = null;
	
	public RecursiveDescentParser(Tokeniser<T> tokeniser) {
		this.tokeniser = tokeniser;
	}
	
	protected void next() {
		token = tokeniser.getNextToken();
	}
	
	protected void error(String msg) {
		System.err.println("Line "+tokeniser.getLineIndex()+": "+msg);
	}
	
	protected boolean accept(T t) {
		expectedToken = t;
		if(t.equals(token)) {
			next();
			expectedToken = null;
			return true; 
		}
		else
			return false;
	}
	
	@SafeVarargs
	protected final int choice(T... ts) {
		expectedToken = null;
		for(int i=0; i<ts.length; i++)
			if(ts[i].equals(token)) {
				next();
				return i;
			}
		return -1;
	}
	
	protected abstract V top();
	
	public V parse(String string) {
		tokeniser.start(string);
		next();
		return top();
	}
	
	public V parse(File file) throws FileNotFoundException, IOException {
		tokeniser.start(file);
		next();
		return top();
	}
	
}
