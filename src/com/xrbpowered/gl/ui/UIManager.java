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
package com.xrbpowered.gl.ui;

import java.util.ArrayList;
import java.util.List;

import com.xrbpowered.gl.res.shaders.VertexInfo;

public class UIManager {

	public static VertexInfo uiVertexInfo = new VertexInfo()
			.addAttrib("in_Position", 2)
			.addAttrib("in_TexCoord", 2);
	
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
