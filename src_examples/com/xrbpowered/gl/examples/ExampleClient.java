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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.Client;
import com.xrbpowered.gl.InputHandler;
import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.SystemSettings;
import com.xrbpowered.gl.SystemSettings.WindowMode;
import com.xrbpowered.gl.res.buffers.OffscreenBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.res.shaders.PostProcessRenderer;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.ActorPickerShader;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.DirectionalLightActor;
import com.xrbpowered.gl.scene.Projection;
import com.xrbpowered.gl.scene.Scene;
import com.xrbpowered.gl.ui.UIManager;
import com.xrbpowered.gl.ui.UIPane;
import com.xrbpowered.gl.ui.UIShader;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.CPAssetManager;
import com.xrbpowered.utils.assets.FileAssetManager;

public class ExampleClient extends Client implements Renderer, InputHandler {

	public static Color CLEAR_COLOR = new Color(0.4f, 0.6f, 0.9f);
	
	public static Font SMALL_FONT = new Font("Verdana", Font.PLAIN, 12);
	public static Font LARGE_FONT = SMALL_FONT.deriveFont(Font.BOLD, 17f);
	public static int UI_PANE_X = 20;
	public static int UI_PANE_Y = 20;
	public static int UI_PANE_WIDTH = 256;
	public static int UI_PANE_HEIGHT = 64;
	public static int UI_PANE_GRAPH_Y = 10;
	public static int UI_PANE_GRAPH_HEIGHT = 80;
	public static int MAX_DTLOG = 236;
	public static Color UI_PANE_BG_COLOR = new Color(0x99000000, true);
	public static Color UI_PANE_TITLE_COLOR = new Color(0xffffdd00);
	public static Color UI_PANE_INFO_COLOR = Color.WHITE;
	public static Color UI_PANE_GRAPH_COLOR = new Color(0x55999999, true);
	
	protected UIManager ui = new UIManager();
	protected UIPane uiDebugPane;
	protected UIPane uiGraphPane;
	public static String uiDebugTitle = null;
	public static String uiDebugInfo = null;
	
	protected ExampleMenu menu;
	
	public Texture plainNormalTexture;
	public Texture plainSpecularTexture;
	public Texture noSpecularTexture;
	
	protected Scene scene;
	protected DirectionalLightActor lightActor;
	
	protected Controller controller;
	protected Controller lightController;
	protected Controller activeController;
	
	protected float fpsUpdateTime = 0f;
	protected int framesCount = 0;
	protected LinkedList<Float>dtlog = new LinkedList<>();
	
	protected boolean wireframe = false;

	protected OffscreenBuffers offscreenBuffers = null;
	
	public ExampleClient() {
		this(settings.windowedWidth, settings.windowedHeight);
	}
	
	public ExampleClient(int width, int height) {
		settings.windowedWidth = width;
		settings.windowedHeight = height;
		AssetManager.defaultAssets = new FileAssetManager("example_assets", new CPAssetManager(
				"com/xrbpowered/gl/examples/shaders",
				new CPAssetManager("assets", AssetManager.defaultAssets)));
	}

	@Override
	protected void setupResources() {
		activeRenderer = this;
		activeInput = this;
		activeUI = ui;
		
		Client.checkError();
		UIShader.getInstance();
//		StandardShader.getInstance();
		
		uiDebugPane = new UIPane(ui, new BufferTexture(UI_PANE_WIDTH, UI_PANE_HEIGHT, false, false, true) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				return updateDebugInfoBuffer(g2, getWidth(), getHeight());
			}
		}).setAnchor(UI_PANE_X, UI_PANE_Y);
		uiGraphPane = new  UIPane(ui, new BufferTexture(UI_PANE_WIDTH, UI_PANE_GRAPH_HEIGHT, false, false, true) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				return updateGraphBuffer(g2, getWidth(), getHeight());
			}
		}).setAnchor(UI_PANE_X, UI_PANE_Y+UI_PANE_HEIGHT+UI_PANE_GRAPH_Y);
		uiGraphPane.setVisible(false);
		
		plainNormalTexture = BufferTexture.createPlainColor(4, 4, new Color(0.5f, 0.5f, 1.0f));
		plainSpecularTexture = BufferTexture.createPlainColor(4, 4, Color.WHITE);//new Color(0.95f, 0.95f, 0.95f));
		noSpecularTexture = BufferTexture.createPlainColor(4, 4, Color.BLACK);
		
		scene = new Scene();
		scene.activeCamera = new CameraActor(scene).setProjection(projectionMatrix());
		scene.activeCamera.position = new Vector3f(0, 0, 2);
		scene.activeCamera.updateTransform();
		
		StandardShader.environment.ambientColor.set(0.1f, 0.1f, 0.1f);
		StandardShader.environment.lightColor.set(0.9f, 0.9f, 0.9f);
		lightActor = new DirectionalLightActor(scene);
		lightActor.rotation.x = (float) Math.PI / 3f;
		lightActor.updateTransform();
		
		lightController = new Controller().setActor(lightActor);
		controller = new Controller().setActor(scene.activeCamera).setLookController(true);
		activeController = controller;
		
		createMenu();
	}
	
	protected String getHelpString() {
		return null;
	}
	
	protected static String formatHelpOnKeys(String[] actions) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><table width=\"100%\">");
		for(String a : actions) {
			String[] s = a.split("\\|", 2);
			sb.append("<tr><td align=\"right\" valign=\"top\"><font color=\"#ffffff\">");
			sb.append(s[0].replaceAll("\\s", "&nbsp;"));
			sb.append("</font></td><td valign=\"top\">");
			sb.append(s[1]);
			sb.append("</td></tr>");
		}
		sb.append("</table></html>");
		return sb.toString();
	}
	
	protected ExampleMenu createMenu() {
		menu = new ExampleMenu(this) { // FIXME simple background
			@Override
			protected PostProcessRenderer createBackground(Renderer parent) {
				return new BlurBackground(parent);
			}
			@Override
			public void start() {
				super.start();
				((BlurBackground) getBackground()).startTween(1f, 0.5f);
			}
			@Override
			protected String getHelpString() {
				return ExampleClient.this.getHelpString();
			}
		};
		return menu;
	}
	
	public Matrix4f projectionMatrix() {
		return Projection.perspective(settings.fov, getAspectRatio(), 0.1f, 100.0f);
	}
	
	public Scene getScene() {
		return scene;
	}
	
	protected boolean updateDebugInfoBuffer(Graphics2D g2, int w, int h) {
		BufferTexture.clearBuffer(g2, w, h);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(UI_PANE_BG_COLOR);
		g2.fillRoundRect(0, 0, w-1, h-1, 10, 10);
		g2.setColor(UI_PANE_INFO_COLOR);
		g2.drawRoundRect(0, 0, w-1, h-1, 10, 10);

		if(uiDebugTitle!=null) {
			g2.setFont(LARGE_FONT);
			g2.setColor(UI_PANE_TITLE_COLOR);
			g2.drawString(uiDebugTitle, 9, 22);
		}
		if(uiDebugInfo!=null) {
			g2.setFont(SMALL_FONT);
			g2.setColor(UI_PANE_INFO_COLOR);
			g2.drawString(uiDebugInfo, 9, 37);
		}
		if(fpsUpdateTime>0f) {
			g2.setFont(SMALL_FONT);
			g2.setColor(UI_PANE_INFO_COLOR);
			g2.drawString(formatFps(), 9, 55);
		}
		return true;
	}
	
	protected boolean updateGraphBuffer(Graphics2D g2, int w, int h) {
		BufferTexture.clearBuffer(g2, w, h);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(UI_PANE_BG_COLOR);
		g2.fillRoundRect(0, 0, w-1, h-1, 10, 10);
		g2.setColor(UI_PANE_INFO_COLOR);
		g2.drawRoundRect(0, 0, w-1, h-1, 10, 10);
		drawFpsGraph(g2, 10, 25, w-20, h-35);
		return true;
	}
	
	protected void drawFpsGraph(Graphics2D g2, int x, int y, int w, int h) {
		float maxv = 0f;
		for(float v : dtlog) {
			if(v>maxv)
				maxv = v;
		}
		g2.setColor(Color.WHITE);
		g2.setFont(SMALL_FONT);
		g2.drawString(String.format("%.0fms", maxv*1000f), x, y-5);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(UI_PANE_GRAPH_COLOR);
		int px = 0;
		for(float v : dtlog) {
			int py = h - (int)Math.round(v*h/maxv);
			if(px>0) {
				g2.drawLine(x+px, y+h, x+px, y+py);
			}
			px++;
		}
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawLine(x, y, x+w, y);
		g2.drawLine(x, y+h, x+w, y+h);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		px = 0;
		int prevy = 0;
		for(float v : dtlog) {
			int py = h - (int)Math.round(v*h/maxv);
			if(px>0) {
				g2.drawLine(x+px-1, y+prevy, x+px, y+py);
			}
			prevy = py;
			px++;
		}
	}
	
	protected String formatFps() {
		return String.format("FPS%s: %.1f",settings.vsync ? " (vsync)" : (settings.maxFps>0 ? " (lim)" :""), framesCount / fpsUpdateTime);
	}
	
	@Override
	protected void resizeResources() {
		scene.activeCamera.setProjection(projectionMatrix());
		UIShader.getInstance().resize();
		menu.resizeBackground();
	}
	
	@Override
	public void updateResources(SystemSettings settings, SystemSettings old) {
//		if(settings.fov!=old.fov)
		scene.activeCamera.setProjection(projectionMatrix());
	}
	
	public void showMenu() {
		activeRenderer = menu.getBackground();
		activeInput = menu;
		activeUI = menu.ui;
		menu.start();
	}
	
	public void hideMenu() {
		activeRenderer = this;
		activeInput = this;
		activeUI = ui;
	}
	
	@Override
	public void processInput(float dt) {
		updateControllers(dt);
		while(Keyboard.next()) {
			if(!Keyboard.getEventKeyState())
				continue;
			keyDown(Keyboard.getEventKey());
		}
	}
	
	@Override
	public void redraw(RenderTarget target, float dt) {
		if(dtlog.size()==MAX_DTLOG)
			dtlog.removeFirst();
		dtlog.add(dt);
		if(uiGraphPane.isVisible())
			uiGraphPane.repaint();
		framesCount++;
		fpsUpdateTime += dt;
		if(fpsUpdateTime >= 0.25f) {
			uiDebugPane.repaint();
			fpsUpdateTime = 0f;
			framesCount = 0;
		}
		
		RenderTarget drawTarget = target;
		if(offscreenBuffers!=null) {
			offscreenBuffers.use();
			drawTarget = offscreenBuffers;
		}
		
		GL11.glClearColor(CLEAR_COLOR.getRed() / 255f, CLEAR_COLOR.getGreen() / 255f, CLEAR_COLOR.getBlue() / 255f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		if(wireframe)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		drawObjects(drawTarget, dt);
		if(wireframe)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

		if(offscreenBuffers!=null) {
			target.use();
			drawOffscreenBuffers(offscreenBuffers, target, dt);
		}
	}
	
	protected void updateControllers(float dt) {
		Controller c = (Mouse.isButtonDown(1)) ? lightController : controller;
		if(activeController!=c) {
			activeController.setMouseLook(false);
			activeController = c;
		}
		activeController.setMouseLook(Mouse.isButtonDown(0) || Mouse.isButtonDown(1));
		activeController.update(dt);
	}
	
	protected void drawObjects(RenderTarget target, float dt) {
	}
	
	protected void drawOffscreenBuffers(OffscreenBuffers source, RenderTarget target, float dt) {
	}
	
	protected void keyDown(int key) {
		switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_ESCAPE:
				activeController.setMouseLook(false);
				showMenu();
				break;
			case Keyboard.KEY_F1:
				settings.maxFps = 120 - settings.maxFps;
				settings.vsync = settings.maxFps>0;
				Display.setVSyncEnabled(settings.vsync);
				break;
			case Keyboard.KEY_F2:
				wireframe = !wireframe;
				break;
			case Keyboard.KEY_F10:
				if(uiGraphPane.isVisible()) {
					uiGraphPane.setVisible(false);
					uiDebugPane.setVisible(false);
				}
				else if(uiDebugPane.isVisible())
					uiGraphPane.setVisible(true);
				else
					uiDebugPane.setVisible(true);
				break;
			case Keyboard.KEY_F11:
				try {
					SystemSettings old = settings;
					settings = new SystemSettings(old);
					settings.windowMode = settings.windowMode==WindowMode.windowed ? WindowMode.borderless : WindowMode.windowed;
					settings.apply(this, old);
				}
				catch (LWJGLException e) {
					e.printStackTrace();
					System.exit(1);
				}
				break;
		}
	}
	
	@Override
	protected void destroyResources() {
		if(offscreenBuffers!=null)
			offscreenBuffers.destroy();
		uiDebugPane.destroy();
		plainNormalTexture.destroy();
		plainSpecularTexture.destroy();
		noSpecularTexture.destroy();
		StandardShader.destroyInstance();
		UIShader.destroyInstance();
		ActorPickerShader.destroyInstance();
	}
	
}
