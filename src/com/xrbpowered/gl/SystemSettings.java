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
package com.xrbpowered.gl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;

public class SystemSettings {

	public enum WindowMode {
		windowed, borderless, fullscreen
	}
	
	public String path = null;
	
	public int windowedWidth = 1600;
	public int windowedHeight = 900;
	public int fullscreenWidth = 1920;
	public int fullscreenHeight = 1080;
	public int fullscreenFreq = 60;
	public WindowMode windowMode = WindowMode.windowed;
	
	public int multisample = 4;
	public int pixelScale = 1;
	public int maxFps = 120;
	public boolean vsync = true;
	
	public int anisotropy = 4;
	public float fov = 70f;
	public float uiScale = 1f;
	
	public SystemSettings() {
	}
	
	public SystemSettings(SystemSettings old) {
		windowedWidth = old.windowedWidth;
		windowedHeight = old.windowedHeight;
		fullscreenWidth = old.fullscreenWidth;
		fullscreenHeight = old.fullscreenHeight;
		fullscreenFreq = old.fullscreenFreq;
		windowMode = old.windowMode;
		multisample = old.multisample;
		maxFps = old.maxFps;
		vsync = old.vsync;
		anisotropy = old.anisotropy;	
		fov = old.fov;
		uiScale = old.uiScale;
	}
	
	public int scale(int size) {
		return (int)(Math.ceil(size / (double)pixelScale));
	}
	
	public SystemSettings setPath(String path, boolean saveNow) {
		this.path = path;
		return this;
	}
	
	public boolean restartRequired(SystemSettings old) {
		return false;
	}

	public boolean confirmRequired(SystemSettings old) {
		return displayChanged(old) ||
				this.multisample!=old.multisample;
	}
	
	private boolean displayChanged(SystemSettings old) {
		return this.windowMode!=old.windowMode ||
				this.fullscreenWidth!=old.fullscreenWidth ||
				this.fullscreenHeight!=old.fullscreenHeight ||
				this.fullscreenFreq!=old.fullscreenFreq;
	}
	
	private DisplayMode switchMode(Client client, boolean resizeResources) throws LWJGLException {
		DisplayMode mode = Display.getDesktopDisplayMode();
		switch(windowMode) {
			case windowed:
				client.switchToWindowed(windowedWidth, windowedHeight, resizeResources);
				break;
			case borderless:
				client.switchToBorderless(resizeResources);
				break;
			case fullscreen:
				mode = client.switchToFullscreen(fullscreenWidth, fullscreenHeight, fullscreenFreq, resizeResources);
				fullscreenWidth = mode.getWidth();
				fullscreenHeight = mode.getHeight();
				fullscreenFreq = mode.getFrequency();
				break;
		}
		return mode;
	}

	public DisplayMode applyAll(Client client) throws LWJGLException {
		DisplayMode mode = switchMode(client, false);
		Display.setVSyncEnabled(vsync);
		return mode;
	}
	
	public void apply(Client client, SystemSettings old) throws LWJGLException {
		if(displayChanged(old))
			switchMode(client, true);
		else if(this.multisample!=old.multisample)
			client.createRenderTarget();
		
		if(this.vsync!=old.vsync)
			Display.setVSyncEnabled(vsync);
		
		// TODO anisotropy without restart
		// FIXME apply settings within Client
		// FIXME apply difference
		client.updateResources(this, old);
	}
	
	public void verifyAnisotropy() {
		anisotropy = Math.min(anisotropy, GL11.glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
	}
	
	public SystemSettings save() {
		if(path==null)
			return this;
		try {
			HashMap<String, Object> properties = new HashMap<String, Object>(1);
			properties.put(JsonGenerator.PRETTY_PRINTING, true);
			JsonGenerator out = Json.createGeneratorFactory(properties).createGenerator(new FileOutputStream(path));
			out.writeStartObject();
			out.write("windowedWidth", windowedWidth);
			out.write("windowedHeight", windowedHeight);
			out.write("fullscreenWidth", fullscreenWidth);
			out.write("fullscreenHeight", fullscreenHeight);
			out.write("fullscreenFreq", fullscreenFreq);
			out.write("windowMode", windowMode.name());
			out.write("multisample", multisample);
			out.write("maxFps", maxFps);
			out.write("vsync", vsync);
			out.write("anisotropy", anisotropy);
			out.write("fov", fov);
			out.write("uiScale", uiScale);
			out.writeEnd();
			out.close();
		}
		catch(Exception e) {
		}
		return this;
	}
	
	public static SystemSettings load(String path, SystemSettings fallback) {
		try {
			JsonReader in = Json.createReader(new FileInputStream(path));
			SystemSettings s = new SystemSettings().setPath(path, false);
			JsonObject json = in.readObject();
			in.close();
			s.windowedWidth = json.getInt("windowedWidth", s.windowedWidth);
			s.windowedHeight = json.getInt("windowedHeight", s.windowedHeight);
			s.fullscreenWidth = json.getInt("fullscreenWidth", s.fullscreenWidth);
			s.fullscreenHeight = json.getInt("fullscreenHeight", s.fullscreenHeight);
			s.fullscreenFreq = json.getInt("fullscreenFreq", s.fullscreenFreq);
			s.windowMode = WindowMode.valueOf(json.getString("windowMode", s.windowMode.name()));
			s.multisample = json.getInt("multisample", s.multisample);
			s.maxFps = json.getInt("maxFps", s.maxFps);
			s.vsync = json.getBoolean("vsync", s.vsync);
			s.anisotropy = json.getInt("anisotropy", s.anisotropy);
			s.fov = jsonGetFloat(json, "fov", s.fov);
			s.uiScale = jsonGetFloat(json, "uiScale", s.uiScale);
			return s;
		}
		catch(Exception e) {
			return fallback==null ? new SystemSettings().setPath(path, false) : fallback;
		}
	}
	
	private static float jsonGetFloat(JsonObject json, String name, float def) {
		try {
			return Float.parseFloat(json.getString(name));
		}
		catch(NumberFormatException|NullPointerException e) {
			return def;
		}
	}
	
}
