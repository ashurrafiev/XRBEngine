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
package com.xrbpowered.gl.scene;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Controller {

	public float moveSpeed = 3.0f;
	public float rotateSpeed = (float)(Math.PI/2f);
	
	public boolean forceForward = false;
	public boolean canStrafe = true;
	
	private boolean mouseLook = false;
	private boolean lookController = false;
	
	private Actor actor = null;
	
	public Controller setActor(Actor actor) {
		this.actor = actor;
		return this;
	}
	
	public Controller setMouseLook(boolean enable) {
		if(mouseLook==enable)
			return this;
		this.mouseLook = enable;
		Mouse.setGrabbed(mouseLook);
		if(mouseLook) {
			Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
		}
		return this;
	}
	
	public Controller setLookController(boolean look) {
		this.lookController = look;
		return this;
	}
	
	private static final Vector4f v = new Vector4f(0, 0, 0, 1);
	
	protected void applyVelocity(Vector3f position, Vector4f v) {
		actor.position.x += v.x;
		actor.position.y += v.y;
		actor.position.z += v.z;
	}
	
	public void update(float dt) {
		if(actor==null)
			return;
//		ExampleClient.uiPaneInfo = String.format("Mouse: %d, %d", Mouse.getX(), Mouse.getY());
		float moveDelta = moveSpeed * dt;
		float rotateDelta = rotateSpeed * dt;

		v.set(0, 0, 0, 1);
		if(Keyboard.isKeyDown(Keyboard.KEY_W) || forceForward)
			v.z += moveDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_S) && !forceForward)
			v.z -= moveDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_A) && canStrafe)
			v.x += moveDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_D) && canStrafe)
			v.x -= moveDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && canStrafe)
			v.y += moveDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && canStrafe)
			v.y -= moveDelta;
		
		if(lookController) {
			v.negate();
			Matrix4f rotate = new Matrix4f();
			Actor.rotateYawPitchRoll(actor.rotation, rotate);
			Matrix4f.transform(rotate, v, v);
		}
		applyVelocity(actor.position, v);

		v.set(0, 0, 0, 1);
		if(Keyboard.isKeyDown(Keyboard.KEY_UP))
			v.x -= rotateDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			v.x += rotateDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			v.y -= rotateDelta;
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			v.y += rotateDelta;

		if(mouseLook) {
//			ExampleClient.uiPaneInfo = String.format("Mouse: %d, %d", Mouse.getX(), Mouse.getY());
			v.y += (Mouse.getX() - Display.getWidth()/2) * 0.002f * rotateSpeed;
			v.x -= (Mouse.getY() - Display.getHeight()/2) * 0.002f * rotateSpeed;
			Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
		}
		
		if(lookController)
			v.negate();
		actor.rotation.x += v.x;
		actor.rotation.y += v.y;
		
		actor.updateTransform();
	}
	
}
