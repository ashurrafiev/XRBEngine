package com.xrbpowered.gl.examples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.xrbpowered.gl.ui.widgets.Button;
import com.xrbpowered.gl.ui.widgets.Label;
import com.xrbpowered.gl.ui.widgets.Widget;
import com.xrbpowered.gl.ui.widgets.WidgetBox;
import com.xrbpowered.gl.ui.widgets.WidgetPainter;
import com.xrbpowered.gl.ui.widgets.menu.MenuItem;
import com.xrbpowered.gl.ui.widgets.menu.MenuOptionItem;

public abstract class ExampleWidgetPainters {

	public static final Color UI_BUTTON_COLOR = Color.BLACK;
	public static final Color UI_BUTTON_COLOR_HOVER = new Color(0x999999);
	public static final Color UI_BUTTON_TEXT_COLOR = Color.WHITE;
	public static final Color UI_BUTTON_DISABLED_TEXT_COLOR = new Color(0x777777);
	public static final Color UI_LABEL_TITLE_COLOR = new Color(0xdddddd);
	public static final Color UI_LABEL_HINT_COLOR = new Color(0x999999);

	public static final Color UI_MENU_COLOR = new Color(0x44000000, true);
	public static final Color UI_MENU_COLOR_HOVER = new Color(0x44999999, true);

	public static final int MENU_STYLE_OPTION = 0;
	public static final int MENU_STYLE_ACTION = 1;
	
	public static final int LABEL_STYLE_MENU_TITLE = 0;
	
	private static final WidgetPainter<WidgetBox> rootPainter = new WidgetPainter<WidgetBox>() {
		@Override
		public void paint(Graphics2D g2, WidgetBox root) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(ExampleClient.UI_PANE_BG_COLOR);
			g2.fillRoundRect(0, 0, root.getWidth()-1, root.getHeight()-1, 10, 10);
			g2.setColor(ExampleClient.UI_PANE_INFO_COLOR);
			g2.drawRoundRect(0, 0, root.getWidth()-1, root.getHeight()-1, 10, 10);
		}
		@Override
		public int getDefaultWidth(WidgetBox root) {
			return 0;
		}
		@Override
		public int getDefaultHeight(WidgetBox root) {
			return 0;
		}
	};
	
	private static final WidgetPainter<WidgetBox> simpleRootPainter = new WidgetPainter<WidgetBox>() {
		@Override
		public void paint(Graphics2D g2, WidgetBox root) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		@Override
		public int getDefaultWidth(WidgetBox root) {
			return 0;
		}
		@Override
		public int getDefaultHeight(WidgetBox root) {
			return 0;
		}
	};
	
	public static int drawStringCentered(Graphics2D g2, String str, int x, int y) {
		FontMetrics fm = g2.getFontMetrics();
		g2.drawString(str, x - fm.stringWidth(str)/2, y + fm.getAscent()/2 - fm.getDescent()/2);
		return y + fm.getHeight();
	}

	public static int drawStringVCentered(Graphics2D g2, String str, int x, int y) {
		FontMetrics fm = g2.getFontMetrics();
		g2.drawString(str, x, y + fm.getAscent()/2 - fm.getDescent()/2);
		return y + fm.getHeight();
	}

	public static void init() {
		Button.painter = new WidgetPainter<Button>() {
			@Override
			public void paint(Graphics2D g2, Button w) {
				g2.setColor(w.isHover() ? UI_BUTTON_COLOR_HOVER : UI_BUTTON_COLOR);
				g2.fillRoundRect(0, 0, w.getWidth()-1, w.getHeight()-1, 10, 10);
				g2.setColor(w.isEnabled() ? UI_BUTTON_TEXT_COLOR : UI_BUTTON_DISABLED_TEXT_COLOR);
				g2.setFont(ExampleClient.LARGE_FONT);
				drawStringCentered(g2, w.getCaption(), w.getWidth()/2, w.getHeight()/2);
			}
			@Override
			public int getDefaultWidth(Button w) {
				return 260;
			}
			@Override
			public int getDefaultHeight(Button w) {
				return 32;
			}
		};
		
		Label.painter = new WidgetPainter<Label>() {
			@Override
			public void paint(Graphics2D g2, Label w) {
				int dx = 0;
				switch(w.getStyle()) {
					case LABEL_STYLE_MENU_TITLE:
						g2.setColor(UI_LABEL_TITLE_COLOR);
						g2.setFont(ExampleClient.LARGE_FONT);
						dx = 10;
						break;
					default:
						g2.setColor(UI_LABEL_HINT_COLOR);
						g2.setFont(ExampleClient.SMALL_FONT);
						break;
				}
				drawStringVCentered(g2, w.getCaption(), dx, w.getHeight()/2);
			}
			@Override
			public int getDefaultWidth(Label w) {
				return 260;
			}
			@Override
			public int getDefaultHeight(Label w) {
				switch(w.getStyle()) {
					case LABEL_STYLE_MENU_TITLE:
						return 20;
					default:
						return 15;
				}
			}
		};
		
		MenuItem.painter = new WidgetPainter<MenuItem>() {
			@Override
			public void paint(Graphics2D g2, MenuItem w) {
				boolean hover = w.isHover();
				if(w.style==MENU_STYLE_ACTION) {
					g2.setColor(hover ? UI_BUTTON_COLOR_HOVER : UI_BUTTON_COLOR);
					g2.fillRect(0, 0, w.getWidth(), w.getHeight());
				}
				else {
					g2.setColor(hover ? UI_MENU_COLOR_HOVER : UI_MENU_COLOR);
					g2.fillRect(0, 0, w.getWidth(), w.getHeight());
				}
				g2.setColor(w.isEnabled() ? UI_BUTTON_TEXT_COLOR : UI_BUTTON_DISABLED_TEXT_COLOR);
				if(w.style==MENU_STYLE_ACTION) {
					g2.setFont(ExampleClient.LARGE_FONT);
					drawStringCentered(g2, w.caption, w.getWidth()/2, w.getHeight()/2);
				}
				else {
					g2.setFont(ExampleClient.SMALL_FONT);
					drawStringVCentered(g2, w.caption, 10, w.getHeight()/2);
				}
			}
			@Override
			public int getDefaultWidth(MenuItem w) {
				return 200;
			}
			@Override
			public int getDefaultHeight(MenuItem w) {
				return w.style==MENU_STYLE_ACTION ? 32 : 24;
			}
		};
		
		MenuOptionItem.painter = new WidgetPainter<MenuOptionItem>() {
			@Override
			public void paint(Graphics2D g2, MenuOptionItem w) {
				boolean hover = w.isHover();
				g2.setFont(ExampleClient.SMALL_FONT);
				if(hover) {
					g2.setColor(UI_MENU_COLOR_HOVER);
					g2.fillRect(0, 0, w.captionWidth-2, w.getHeight());
					g2.fillRect(w.captionWidth, 0, w.getWidth()-w.captionWidth, w.getHeight());
					g2.setColor(w.left.isHover() ? UI_BUTTON_COLOR_HOVER : UI_BUTTON_COLOR);
					g2.fillRect(w.left.getX(), 0, w.left.getWidth(), w.left.getHeight());
					g2.setColor(w.right.isHover() ? UI_BUTTON_COLOR_HOVER : UI_BUTTON_COLOR);
					g2.fillRect(w.right.getX(), 0, w.right.getWidth(), w.right.getHeight());
					g2.setColor(UI_BUTTON_TEXT_COLOR);
					g2.setStroke(new BasicStroke(2f));
					g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
					int x0, y0;
					x0 = w.left.getX()+w.left.getWidth()/2;
					y0 = w.getHeight()/2;
					g2.drawPolyline(new int[] {x0+1, x0-3, x0+1}, new int[] {y0-5, y0, y0+5}, 3);
					x0 = w.right.getX()+w.right.getWidth()/2;
					g2.drawPolyline(new int[] {x0-1, x0+3, x0-1}, new int[] {y0-5, y0, y0+5}, 3);
				}
				else if(w.isEnabled()) {
					g2.setColor(UI_MENU_COLOR);
					g2.fillRect(0, 0, w.captionWidth-2, w.getHeight());
					g2.fillRect(w.captionWidth, 0, w.getWidth()-w.captionWidth, w.getHeight());
				}
				g2.setColor(w.isEnabled() ? UI_BUTTON_TEXT_COLOR : UI_BUTTON_DISABLED_TEXT_COLOR);
				drawStringVCentered(g2, w.caption, 10, w.getHeight()/2);
				drawStringCentered(g2, w.getValueName(), w.valueBox.getX()+w.valueBox.getWidth()/2, w.getHeight()/2);
			}
			@Override
			public int getDefaultWidth(MenuOptionItem w) {
				return 200;
			}
			@Override
			public int getDefaultHeight(MenuOptionItem w) {
				return 24;
			}
		};
	}
	
	public static WidgetBox createRootWidget(final boolean simple) {
		return new WidgetBox(null) {
			@SuppressWarnings("unchecked")
			@Override
			protected <T extends Widget> WidgetPainter<T> getPainter() {
				return (WidgetPainter<T>)(simple ? simpleRootPainter : rootPainter);
			}
		};
	}

}
