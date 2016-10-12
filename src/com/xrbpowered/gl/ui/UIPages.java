package com.xrbpowered.gl.ui;

import java.util.LinkedList;

public class UIPages {

	private LinkedList<UIPage> pages = new LinkedList<>();

	private void hideLast() {
		if(!pages.isEmpty())
			pages.getLast().setVisible(false);
	}

	public void start(UIPage defPage) {
		hideLast();
		pages.clear();
		pages.add(defPage);
		defPage.setVisible(true);
	}
	
	public void push(UIPage page) {
		hideLast();
		pages.add(page);
		page.setVisible(true);
	}
	
	public boolean pop() {
		if(pages.size()>1) {
			pages.removeLast().setVisible(false);
			pages.getLast().setVisible(true);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void closeAll() {
		hideLast();
		pages.clear();
	}
	
}
