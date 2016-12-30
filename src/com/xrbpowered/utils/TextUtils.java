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
package com.xrbpowered.utils;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JLabel;

public class TextUtils {

	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int TOP = 0;
	public static final int BOTTOM = 2;
	
	public static int drawString(Graphics2D g2, String str, int x, int y, int halign, int valign) {
		FontMetrics fm = g2.getFontMetrics();
		int w = fm.stringWidth(str);
		int h = fm.getAscent() - fm.getDescent();
		Point a = new Point(w*halign/2, h*valign/2);
		g2.drawString(str, x - a.x, y + h - a.y);
		return y + h;
	}
	
	private static final JLabel htmlAssist = new JLabel();
	
	public static void drawFormattedString(Graphics2D g2, String htmlStr, int x, int y, int w, int h) {
		g2.translate(x, y);
		htmlAssist.setFont(g2.getFont());
		htmlAssist.setForeground(g2.getColor());
		htmlAssist.setVerticalAlignment(JLabel.TOP);
		htmlAssist.setBounds(0, 0, w, h);
		htmlAssist.invalidate();
		htmlAssist.setText(htmlStr);
		htmlAssist.paint(g2);
		g2.translate(-x, -y);
	}
	
	public static String htmlString(String str) {
		str = str.replaceAll("\\&", "&amp;");
		str = str.replaceAll("\\<", "&lt;");
		str = str.replaceAll("\\>", "&gt;");
		return "<html>"+str;
	}
	
}
