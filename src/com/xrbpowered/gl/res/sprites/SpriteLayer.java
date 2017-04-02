package com.xrbpowered.gl.res.sprites;

import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.shaders.InstanceBuffer;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.res.textures.Texture;

public class SpriteLayer {

	public static VertexInfo spriteVertexInfo = new VertexInfo().addAttrib("in_Position", 2);

	private static StaticMesh quad = null;
	
	public final int maxCount;
	protected Texture texture = null;
	
	private float[] instanceData;
	private InstanceBuffer instBuffer;
	
	public SpriteLayer(int maxCount, Texture texture) {
		this.maxCount = maxCount;
		this.texture = texture;
		
		instanceData = new float[maxCount * SpriteShader.INS_STRIDE];
		instBuffer = SpriteShader.createInstanceBuffer(maxCount);
		
		if(quad==null) {
			quad = new StaticMesh(spriteVertexInfo, new float[] {
					-0.5f, -0.5f,
					0.5f, -0.5f,
					0.5f, 0.5f,
					-0.5f, 0.5f
			}, new short[] {
					0, 3, 2, 2, 1, 0
			});
		}
	}
	
	public void setSprite(int index, float x, float y, float w, float h, float rotate, float scale, float tx, float ty, Vector4f color) {
		int offs = index * SpriteShader.INS_STRIDE;
		instanceData[offs] = x;
		instanceData[offs+1] = y;
		instanceData[offs+2] = w;
		instanceData[offs+3] = h;
		instanceData[offs+4] = rotate;
		instanceData[offs+5] = scale;
		instanceData[offs+6] = tx;
		instanceData[offs+7] = ty;
		if(color!=null) {
			instanceData[offs+8] = color.x;
			instanceData[offs+9] = color.y;
			instanceData[offs+10] = color.z;
			instanceData[offs+11] = color.w;
		}
		else {
			instanceData[offs+8] = 1f;
			instanceData[offs+9] = 1f;
			instanceData[offs+10] = 1f;
			instanceData[offs+11] = 1f;
		}
	}
	
	public void update(int count) {
		instBuffer.updateInstanceData(instanceData, count);
	}
	
	public void draw(int count) {
		SpriteShader.getInstance().use();
		texture.bind(0);
		quad.enableDraw(null);
		instBuffer.enable();
		quad.drawCallInstanced(count);
		instBuffer.disable();
		quad.disableDraw();
	}
	
	public void destroy() {
		if(quad!=null) {
			quad.destroy();
			quad = null;
		}
		SpriteShader.destroyInstance();
	}
	
}
