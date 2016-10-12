package com.xrbpowered.gl.ui;

import java.util.ArrayList;
import java.util.List;

import com.xrbpowered.gl.res.shaders.VertexInfo;

public class UIManager {

	public static VertexInfo uiVertexInfo = new VertexInfo()
			.addFloatAttrib("in_Position", 2)
			.addFloatAttrib("in_TexCoord", 2);
	
	List<UIPane> panes = new ArrayList<>();
	
	public void reset() {
		while(panes.size()>0)
			panes.get(panes.size()-1).destroy();
	}
	
	public void draw(int w, int h) {
		if(panes.size()==0)
			return;
		UIShader.getInstance().use();
		for(UIPane pane : panes)
			pane.draw();
		UIShader.getInstance().unuse();
	}
	
}
