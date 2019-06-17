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
