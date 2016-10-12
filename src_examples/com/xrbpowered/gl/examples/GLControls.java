package com.xrbpowered.gl.examples;

import org.lwjgl.input.Keyboard;

import com.xrbpowered.gl.ui.UIPages;
import com.xrbpowered.gl.ui.widgets.WidgetBox;
import com.xrbpowered.gl.ui.widgets.WidgetManager;
import com.xrbpowered.gl.ui.widgets.WidgetPane;
import com.xrbpowered.gl.ui.widgets.menu.MenuItem;
import com.xrbpowered.gl.ui.widgets.menu.MenuOptionItem;
import com.xrbpowered.gl.ui.widgets.menu.WidgetMenuBuilder;

public class GLControls extends ExampleClient {

	private WidgetManager widgets = new WidgetManager(ui);
	private UIPages pages = new UIPages();
	
	private WidgetPane pMenu, pSettings;
	
	public GLControls() {
		settings.multisample = 0;
		init("GLControls");
		run();
	}

	@Override
	protected void setupResources() {
		super.setupResources();
		Keyboard.enableRepeatEvents(true);
		ExampleWidgetPainters.init();
		
		WidgetMenuBuilder mb = new WidgetMenuBuilder(widgets, pages) {
			@Override
			protected WidgetBox createRoot() {
				return ExampleWidgetPainters.createRootWidget(false);
			}
		};
		mb.margin = 10;

		mb.startPage(360, 180);
		final MenuOptionItem optResolution = new MenuOptionItem(mb.getPageRoot(), "Resolution", new String[] {"1280x720", "1600x900", "1920x1080", "2560x1440", "3840x2160"}, 4, 0);
		optResolution.disableOverriveValue = "Desktop";
		optResolution.setEnabled(false);
		final MenuOptionItem optRefreshRate = new MenuOptionItem(mb.getPageRoot(), "Refresh rate", new String[] {"30Hz", "60Hz"}, 1, 0);
		optRefreshRate.disableOverriveValue = "Desktop";
		optRefreshRate.setEnabled(false);
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Window mode", new String[] {"Windowed", "Borderless", "Fullscreen"}, 0, 0) {
			@Override
			public void onChangeValue(int index) {
				optResolution.setEnabled(index==2);
				optRefreshRate.setEnabled(index==2);
			}
		});
		mb.addMenuItem(optResolution);
		mb.addMenuItem(optRefreshRate);
		final MenuOptionItem optFpsLimit = new MenuOptionItem(mb.getPageRoot(), "FPS limit", new String[] {"Off", "60", "120", "240", "480"}, 0, 0);
		optFpsLimit.disableOverriveValue = "V-Sync";
		optFpsLimit.setEnabled(false);
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "V-Sync", new String[] {"Yes", "No"}, 0, 0) {
			@Override
			public void onChangeValue(int index) {
				optFpsLimit.setEnabled(index==1);
			}
		});
		mb.addMenuItem(optFpsLimit);
		mb.addBlank(10);
		
//		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Quality", new String[] {"Low", "Medium", "High", "Custom"}, 2, 0));
//		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "Advanced", ExampleWidgetPainters.MENU_STYLE_OPTION));
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Anti-aliasing", new String[] {"Off", "2x", "4x", "8x", "16x"}, 2, 0));
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Anisotropic filtering", new String[] {"Off", "2x", "4x", "8x", "16x"}, 2, 0));
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Refraction pass", new String[] {"Off", "Refraction only", "Refraction+Blur"}, 2, 0));
		
		Integer[] fovOptions = new Integer[11];
		for(int i=0; i<fovOptions.length; i++)
			fovOptions[i] = 60 + i*5;
		mb.addBlank(10);
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "FOV", fovOptions, 2, 0));
//		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Interface scale", new String[] {"1x", "1.5x", "2x"}, 0, 0));
		mb.addMenuItem(new MenuOptionItem(mb.getPageRoot(), "Debug info", new String[] {"Off", "Show FPS", "Verbose", "Verbose+Timeline"}, 2, 0));
		mb.addBlank(20);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "Apply", ExampleWidgetPainters.MENU_STYLE_ACTION));
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "Defaults", ExampleWidgetPainters.MENU_STYLE_ACTION));
		mb.addCancelItem("Back", ExampleWidgetPainters.MENU_STYLE_ACTION);
		pSettings = mb.finishPage();

		mb.startPage(360, 180);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "Resume", ExampleWidgetPainters.MENU_STYLE_ACTION));
		mb.addPageItem("Settings", pSettings, ExampleWidgetPainters.MENU_STYLE_ACTION);
		mb.addMenuItem(new MenuItem(mb.getPageRoot(), "Exit", ExampleWidgetPainters.MENU_STYLE_ACTION) {
			@Override
			public void onMouseDown(int x, int y, int button) {
				exit();
			}
		});
		pMenu = mb.finishPage();

		centerPanes();
		pages.start(pMenu);
	}

	@Override
	public void processInput(float dt) {
		widgets.processInput(dt);
		super.processInput(dt);
	}
	
	@Override
	protected void updateControllers(float dt) {
	}
	
	private void centerPanes() {
		pMenu.center();
		pSettings.center();
	}
	
	@Override
	protected void resizeResources() {
		super.resizeResources();
		centerPanes();
	}
	
	public static void main(String[] args) {
		new GLControls();
	}

}
