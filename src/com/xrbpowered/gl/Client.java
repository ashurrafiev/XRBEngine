package com.xrbpowered.gl;

import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.NVXGpuMemoryInfo;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import com.xrbpowered.gl.SystemSettings.WindowMode;
import com.xrbpowered.gl.res.buffers.MultisampleBuffers;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.ui.UIManager;
import com.xrbpowered.utils.JNIUtils;

public abstract class Client {

	public static SystemSettings settings = SystemSettings.load("settings.json", null);
	
	protected DisplayMode displayMode;
	protected Renderer activeRenderer = null;
	protected InputHandler activeInput = null;
	protected UIManager activeUI = null;
	private RenderTarget target = null;
	
	public Client init(String windowTitle) {
		JNIUtils.addLibraryPath("lib/native");
		try {
			setupOpenGL(windowTitle);
			setupResources();
			printInfo();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return this;
	}
	
	protected void setupOpenGL(String windowTitle) throws LWJGLException {
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAtrributes = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);

		Display.setTitle(windowTitle);
		if(settings.windowMode==WindowMode.fullscreen) {
			Display.create(pixelFormat, contextAtrributes);
			displayMode = settings.applyAll(this);
		}
		else {
			displayMode = settings.applyAll(this);
			Display.create(pixelFormat, contextAtrributes);
		}
		settings.verifyAnisotropy();
		createRenderTarget();
	}

	public void run() {
		long prevTime = Sys.getTime();
		long time;
		while(!Display.isCloseRequested()) {
			if(Display.wasResized()) {
				createRenderTarget();
				resizeResources();
			}

			time = Sys.getTime();
			float dt = (time - prevTime) / (float) Sys.getTimerResolution();
			if(activeInput!=null)
				activeInput.processInput(dt);
			target.use();
			if(activeRenderer!=null)
				activeRenderer.redraw(target, dt);
			if(target.fbo!=0)
				RenderTarget.blit(target, RenderTarget.primaryBuffer, false);
			if(activeUI!=null) {
				RenderTarget.primaryBuffer.use();
				activeUI.draw(Display.getWidth(), Display.getHeight());
			}
			prevTime = time;

			Display.update(false);

			if(settings.vsync)
				Display.sync(displayMode.getFrequency()*2);
			else if(settings.maxFps>0)
				Display.sync(settings.maxFps);
			
			while(Mouse.next());
			while(Keyboard.next());
			Display.processMessages();
		}
		exit();
	}
	
	public void exit() {
		destroyResources();
		Display.destroy();
		System.exit(0);
	}
	
	public RenderTarget createRenderTarget() {
		if(target!=null && target.fbo!=0)
			target.destroy();
		if(settings.multisample>1)
			target = new MultisampleBuffers(Display.getWidth(), Display.getHeight(), settings.multisample);
		else
			target = RenderTarget.primaryBuffer;
		return target;
	}
	
	protected abstract void setupResources();
	protected abstract void destroyResources();
	
	protected void resizeResources() {		
	}
	
	public void updateResources(SystemSettings settings, SystemSettings old) {
	}
	
	public void switchToWindowed(int width, int height, boolean resizeResources) throws LWJGLException {
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
		Display.setFullscreen(false);
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setResizable(true);
		if(resizeResources) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			createRenderTarget();
			resizeResources();
		}
	}

	public void switchToBorderless(boolean resizeResources) throws LWJGLException {
		DisplayMode desktop = Display.getDesktopDisplayMode();
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		Display.setFullscreen(false);
		Display.setDisplayMode(new DisplayMode(desktop.getWidth(), desktop.getHeight()));
		Display.setResizable(false);
		if(resizeResources) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			createRenderTarget();
			resizeResources();
		}
	}
	
	public DisplayMode switchToFullscreen(int width, int height, int freq, boolean resizeResources) throws LWJGLException {
		DisplayMode mode = findFullscreenMode(width, height, freq);
		Display.setDisplayModeAndFullscreen(mode);
		if(resizeResources) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			createRenderTarget();
			resizeResources();
		}
		return mode;
	}
	
	public static DisplayMode findFullscreenMode(int width, int height, int freq) throws LWJGLException {
		DisplayMode[] modes = Display.getAvailableDisplayModes();
		DisplayMode match = null;
		for(DisplayMode m : modes) {
			if(m.getBitsPerPixel()!=32 || !m.isFullscreenCapable())
				continue;
			if(match==null)
				match = m;
			else {
				if(Math.abs(m.getHeight()-height)<Math.abs(match.getHeight()-height) ||
						Math.abs(m.getWidth()-width)<Math.abs(match.getWidth()-width) ||
						freq<=0 && m.getFrequency()>match.getFrequency() ||
						freq>0 && Math.abs(m.getFrequency()-freq)<Math.abs(match.getFrequency()-freq))
					match = m;
			}
		}
		return match;
	}
	
	public static void printInfo() {
		System.out.println("\n--------------------------------\nSYSTEM INFO\n--------------------------------");
		System.out.println("Device: " + GL11.glGetString(GL11.GL_RENDERER));
		System.out.println("Device vendor: " + GL11.glGetString(GL11.GL_VENDOR));
		System.out.println("OpenGL version (requested 3.3): " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("Available fullscreen modes:");
		DisplayMode desktop = Display.getDesktopDisplayMode();
		try {
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
				if(m.getBitsPerPixel()!=32 || m.getFrequency()<60 || !m.isFullscreenCapable())
					continue;
				boolean aspect = (m.getWidth()/(float)m.getHeight() == desktop.getWidth()/(float)desktop.getHeight());
				if(!aspect)
					continue;
				System.out.printf("%dx%d%s@%dHz\n", m.getWidth(), m.getHeight(),
						 !aspect ? "(aspect!)" : "",
						m.getFrequency());
			}
			System.out.println();
			}
		catch(LWJGLException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Max texture size: %d\n", GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE));
		System.out.printf("Max MSAA samples: %d\n", GL11.glGetInteger(GL30.GL_MAX_SAMPLES));
		System.out.printf("Max anisotropy: %d\n", GL11.glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
		System.out.printf("Max texture array layers: %d\n", GL11.glGetInteger(GL30.GL_MAX_ARRAY_TEXTURE_LAYERS));
		System.out.printf("Max vertex attribs: %d\n", GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS));
		System.out.printf("Max uniform components: %d\n", GL11.glGetInteger(GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS));
		System.out.printf("Available video memory (NVIDIA only): %.1f%%\n", getAvailMemoryNVidia()*100f);
		System.out.println("--------------------------------");
		System.out.println();
		
		GL11.glGetError(); // clear errors
	}
	
	public float getAspectRatio() {
		return Display.getWidth() / (float) Display.getHeight();
	}
	
	public static float getAvailMemoryNVidia() {
		try {
			return (float)GL11.glGetInteger(NVXGpuMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX)
					/ (float)GL11.glGetInteger(NVXGpuMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX);
		}
		catch(Exception e) {
			return -1f;
		}
	}
	
	public static boolean isGLExtensionSupported(String name) {
		int count = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS);
		for(int i = 0; i < count; i++) {
			String ext = GL30.glGetStringi(GL11.GL_EXTENSIONS, i);
			if(name.equals(ext))
				return true;
		}
		return false;
	}
	
	public static void checkError(boolean crash) {
		int err = GL11.glGetError();
		if(err!=GL11.GL_NO_ERROR) {
			(new RuntimeException(GLU.gluErrorString(err))).printStackTrace();
			if(crash) {
				printInfo();
				System.exit(1);
			}
		}
	}
	
	public static void checkError() {
		checkError(true);
	}
	
	private static long time = 0L;
	
	public static void timestamp(String s) {
		long t = System.nanoTime();
		if(time>0L && s!=null) {
			System.out.printf("%5.1f ms\t - %s\n", (t - time)/1000000.0, s);
		}
		time = t;
	}
}
