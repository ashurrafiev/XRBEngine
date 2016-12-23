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
