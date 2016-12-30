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
package com.xrbpowered.gl.examples;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.xrbpowered.gl.Client;
import com.xrbpowered.gl.InputHandler;
import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.SystemSettings;
import com.xrbpowered.gl.SystemSettings.WindowMode;
import com.xrbpowered.gl.res.shaders.PostProcessRenderer;
import com.xrbpowered.gl.ui.UIManager;
import com.xrbpowered.gl.ui.UIPage;
import com.xrbpowered.gl.ui.UIPages;
import com.xrbpowered.gl.ui.widgets.Label;
import com.xrbpowered.gl.ui.widgets.WidgetBox;
import com.xrbpowered.gl.ui.widgets.WidgetManager;
import com.xrbpowered.gl.ui.widgets.menu.MenuItem;
import com.xrbpowered.gl.ui.widgets.menu.MenuOptionItem;
import com.xrbpowered.gl.ui.widgets.menu.WidgetMenuBuilder;

public class ExampleMenu implements InputHandler {
	
	protected static int WIDTH = 400;
	protected static int CAPTION_WIDTH = 200;
	
	protected static int RESOLUTION_MIN_WIDTH = 1024;
	protected static int RESOLUTION_MIN_HEIGHT = 600;
	
	public final ExampleClient client;
	public final UIManager ui;
	
	protected PostProcessRenderer background;

	protected final WidgetManager widgets;
	protected final UIPages pages = new UIPages();
	
	protected UIPage pMain, pSettings, pVideoSettings, pHelp;
	protected SystemSettings settings;
	
	public ExampleMenu(ExampleClient client) {
		this(client, new UIManager());
	}
	
	public ExampleMenu(ExampleClient client, UIManager ui) {
		this.client = client;
		this.background = createBackground(client);
		this.ui = ui;
		this.widgets = new WidgetManager(ui);
		
		WidgetMenuBuilder mb = createMenuBuilder();
		pVideoSettings = createVideoSettingsPage(mb);
		pSettings = createSettingsPage(mb);
		pHelp = createHelpPage(mb);
		pMain = createMainPage(mb);

		layout();
		pages.start(pMain);
	}
	
	protected WidgetMenuBuilder createMenuBuilder() {
		ExampleWidgetPainters.init();
		return new WidgetMenuBuilder(widgets, pages) {
			@Override
			protected WidgetBox createRoot() {
				return ExampleWidgetPainters.createRootWidget(true);
			}
		};
	}
	
	protected void addDisplayModeSettings(WidgetMenuBuilder mb) {
		LinkedHashSet<String> displayRes = new LinkedHashSet<>();
		LinkedList<String> displayFreq = new LinkedList<>();
		int resIndex = 0;
		try {
			DisplayMode desktop = Display.getDesktopDisplayMode();
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			Arrays.sort(modes, new Comparator<DisplayMode>() {
				@Override
				public int compare(DisplayMode o1, DisplayMode o2) {
					if(o1.getWidth()!=o2.getWidth())
						return Integer.compare(o1.getWidth(), o2.getWidth());
					else
						return Integer.compare(o1.getHeight(), o2.getHeight());
				}
			});
			for(DisplayMode m : modes) {
				if(m.getBitsPerPixel()!=32 || !m.isFullscreenCapable() || m.getWidth()<RESOLUTION_MIN_WIDTH || m.getHeight()<RESOLUTION_MIN_HEIGHT)
					continue;
				boolean aspect = (m.getWidth()/(float)m.getHeight() == desktop.getWidth()/(float)desktop.getHeight());
				if(!aspect)
					continue;
				displayRes.add(String.format("%dx%d", m.getWidth(), m.getHeight()));
				if(settings.fullscreenWidth==m.getWidth() && settings.fullscreenHeight==m.getHeight())
					resIndex = displayRes.size()-1;
				String freq = m.getFrequency()+"Hz";
				if(!displayFreq.contains(freq))
					displayFreq.add(freq);
			}
		}
		catch(LWJGLException e) {
			e.printStackTrace();
		}
		Collections.sort(displayFreq);
		int freqIndex = displayFreq.indexOf(settings.fullscreenFreq+"Hz");
		
		final MenuOptionItem optResolution = new MenuOptionItem(mb.getPageRoot(), "Resolution", displayRes.toArray(), resIndex, 0) {
			@Override
			public void onChangeValue(int index) {
				String[] s = getValueName().split("x");
				settings.fullscreenWidth = Integer.parseInt(s[0]);
				settings.fullscreenHeight = Integer.parseInt(s[1]);
			}
		};
		optResolution.disableOverriveValue = "Desktop";
		optResolution.setEnabled(false);
		
		final MenuOptionItem optRefreshRate = new MenuOptionItem(mb.getPageRoot(), "Refresh rate", displayFreq.toArray(), freqIndex, 0) {
			@Override
			public void onChangeValue(int index) {
				String s = getValueName();
				settings.fullscreenWidth = Integer.parseInt(s.substring(0, s.length()-2));
			}
		};
		optRefreshRate.disableOverriveValue = "Desktop";
		optRefreshRate.setEnabled(false);
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Window mode",
				displayFreq.isEmpty() || displayRes.isEmpty() ? new String[] {"Windowed", "Borderless"} : new String[] {"Windowed", "Borderless", "Fullscreen"}, 0, 0) {
			@Override
			public void onChangeValue(int index) {
				optResolution.setEnabled(index==2);
				optRefreshRate.setEnabled(index==2);
				settings.windowMode = WindowMode.values()[index];
			}
		});
		mb.addMenuItem(optResolution);
		mb.addMenuItem(optRefreshRate);
	}
	
	protected void addQualitySettings(WidgetMenuBuilder mb) {
		int max = GL11.glGetInteger(GL30.GL_MAX_SAMPLES);
		int index = 0;
		LinkedList<String> options = new LinkedList<>();
		options.add("Off");
		for(int i=1, d=2; d<=max; i++, d<<=1) {
			options.add(d+"x");
			if(d==settings.multisample)
				index = i;
		}
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Anti-aliasing", options.toArray(), index, 0) {
			@Override
			public void onChangeValue(int index) {
				String s = getValueName();
				settings.multisample = s.equals("Off") ? 0 : Integer.parseInt(s.substring(0, s.length()-1));
			}
		});

		max = GL11.glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
		index = 0;
		options.clear();
		options.add("Off");
		for(int i=1, d=2; d<=max; i++, d<<=1) {
			options.add(d+"x");
			if(d==settings.anisotropy)
				index = i;
		}
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Anisotropic filtering", options.toArray(), index, 0) {
			@Override
			public void onChangeValue(int index) {
				String s = getValueName();
				settings.anisotropy = s.equals("Off") ? 1 : Integer.parseInt(s.substring(0, s.length()-1));
			}
		});
	}
	
	protected void linkQualitySubmenu(WidgetMenuBuilder mb, UIPage submenu) {
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Quality", new String[] {"Low", "Medium", "High", "Custom"}, 2, 0));
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "Customize", ExampleWidgetPainters.MENU_STYLE_OPTION));
	}
	
	protected boolean applySettings() {
		SystemSettings saved = Client.settings;
		try {
			Client.settings = settings;
			settings.apply(client, saved);
			settings = new SystemSettings(Client.settings);
			System.out.println("Settings applied!");
			// Client.settings.save();
			return true;
		}
		catch(LWJGLException e) {
			e.printStackTrace();
			Client.settings = saved;
			return false;
		}
	}
	
	protected UIPage createQualitySubmenuPage(WidgetMenuBuilder mb) {
		return null;
	}
	
	protected UIPage createVideoSettingsPage(WidgetMenuBuilder mb) {
		settings = new SystemSettings(Client.settings);
		mb.startPage(WIDTH, CAPTION_WIDTH);
		mb.addWidget(new Label(mb.getPageRoot(), "VIDEO SETTINGS", ExampleWidgetPainters.LABEL_STYLE_MENU_TITLE));
		mb.addBlank(20);
		
		addDisplayModeSettings(mb);
		
		final MenuOptionItem optFpsLimit = new MenuOptionItem(mb.getPageRoot(), "FPS limit", new String[] {"Off", "60", "120", "240", "480"}, 0, 0) {
			@Override
			public void onChangeValue(int index) {
				String s = getValueName();
				settings.maxFps = s.equals("Off") ? 0 : Integer.parseInt(s);
			}
		};
		optFpsLimit.disableOverriveValue = "V-Sync";
		optFpsLimit.setEnabled(false);
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "V-Sync", new String[] {"Yes", "No"}, 0, 0) {
			@Override
			public void onChangeValue(int index) {
				optFpsLimit.setEnabled(index==1);
				settings.vsync = (index==0);
			}
		});
		mb.addMenuItem(optFpsLimit);
		
		mb.addBlank(10);
		UIPage qualitySubmenu = createQualitySubmenuPage(mb);
		if(qualitySubmenu==null)
			addQualitySettings(mb);
		else
			linkQualitySubmenu(mb, qualitySubmenu);
		
		Integer[] fovOptions = new Integer[11];
		for(int i=0; i<fovOptions.length; i++)
			fovOptions[i] = 60 + i*5;
		mb.addBlank(10);
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "FOV", fovOptions, 2, 0) {
			@Override
			public void onChangeValue(int index) {
				settings.fov = Integer.parseInt(getValueName());
			}
		});
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Debug info", new String[] {"Off", "Show FPS", "Verbose", "Verbose+Timeline"}, 2, 0));
		mb.addBlank(20);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "APPLY", ExampleWidgetPainters.MENU_STYLE_ACTION) {
			@Override
			public void onMouseDown(int x, int y, int button) {
				applySettings();
			}
		});
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "DEFAULTS", ExampleWidgetPainters.MENU_STYLE_ACTION)).setEnabled(false);
		mb.addCancelItem("BACK", ExampleWidgetPainters.MENU_STYLE_ACTION);
		
		return mb.finishPage();
	}
	
	protected UIPage createSettingsPage(WidgetMenuBuilder mb) {
		mb.startPage(WIDTH, CAPTION_WIDTH);
		mb.addPageItem("VIDEO", pVideoSettings, ExampleWidgetPainters.MENU_STYLE_ACTION);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "AUDIO", ExampleWidgetPainters.MENU_STYLE_ACTION)).setEnabled(false);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "CONTROLS", ExampleWidgetPainters.MENU_STYLE_ACTION)).setEnabled(false);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "GAMEPLAY", ExampleWidgetPainters.MENU_STYLE_ACTION)).setEnabled(false);
		mb.addCancelItem("BACK", ExampleWidgetPainters.MENU_STYLE_ACTION);
		return mb.finishPage();
	}

	protected String getHelpString() {
		return null;
	}
	
	protected UIPage createHelpPage(WidgetMenuBuilder mb) {
		String str = getHelpString();
		if(str==null)
			return null;
		mb.startPage(WIDTH, CAPTION_WIDTH);
		mb.addWidget(new Label(mb.getPageRoot(), "HELP", ExampleWidgetPainters.LABEL_STYLE_MENU_TITLE));
		mb.addBlank(20);
		mb.addWidget(new Label(mb.getPageRoot(), str, ExampleWidgetPainters.LABEL_STYLE_HTML).setSize(mb.getWidth(), 200));
		mb.addBlank(20);
		mb.addCancelItem("BACK", ExampleWidgetPainters.MENU_STYLE_ACTION);
		return mb.finishPage();
	}
	
	protected UIPage createMainPage(WidgetMenuBuilder mb) {
		mb.startPage(WIDTH, CAPTION_WIDTH);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "RESUME", ExampleWidgetPainters.MENU_STYLE_ACTION) {
			@Override
			public void onMouseDown(int x, int y, int button) {
				client.hideMenu();
			}
		});
		MenuItem help = mb.addPageItem("HELP", pHelp, ExampleWidgetPainters.MENU_STYLE_ACTION);
		if(pHelp==null)
			help.setEnabled(false);
		mb.addPageItem("SETTINGS", pSettings, ExampleWidgetPainters.MENU_STYLE_ACTION);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "EXIT", ExampleWidgetPainters.MENU_STYLE_ACTION) {
			@Override
			public void onMouseDown(int x, int y, int button) {
				client.exit();
			}
		});
		
		return mb.finishPage();
	}
	
	protected void layout() {
		int screenWidth = Display.getWidth();
		int screenHeight = Display.getHeight();
		pMain.layout(screenWidth, screenHeight);
		pVideoSettings.layout(screenWidth, screenHeight);
		pSettings.layout(screenWidth, screenHeight);
		if(pHelp!=null)
			pHelp.layout(screenWidth, screenHeight);
	}
	
	protected PostProcessRenderer createBackground(Renderer parent) {
		return null;
	}
	
	public PostProcessRenderer getBackground() {
		return background;
	}
	
	public void resizeBackground() {
		if(background!=null) {
			background.resizeBuffers();
			background.requestUpdate();
		}
		layout();
	}
	
	public void start() {
		if(background!=null) {
			background.requestUpdate();
		}
	}
	
	@Override
	public void processInput(float dt) {
		widgets.processInput(dt);
	}
	
}
