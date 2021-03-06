package com.xrbpowered.gl.res.fbx;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.StaticMesh;
import com.xrbpowered.gl.res.builder.AdvancedMeshBuilder;
import com.xrbpowered.gl.res.builder.MeshBuilder;
import com.xrbpowered.gl.res.builder.AdvancedMeshBuilder.Vertex;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.utils.assets.AssetManager;

public class FbxMeshLoader {
	private static final float NORM_EPSILON = 0.01f;
	
	private AdvancedMeshBuilder builder;
	private final float scale;

	private FbxMeshLoader(AdvancedMeshBuilder builder, float scale) {
		this.builder = builder;
		this.scale = scale;
	}
	
	private void setVertices(FbxTable table) {
		float[] v = table.get("Vertices").getAll((float[]) null);
		for(int i=0; i<v.length; i+=3) {
			builder.addVertex().setPosition(v[i+0]*scale, v[i+2]*scale, -v[i+1]*scale);
//			builder.add(new Vertex(v[i+0]*scale, v[i+2]*scale, -v[i+1]*scale));
		}
	}
	
	private int[] setIndices(FbxTable table) {
		int[] vi = table.get("PolygonVertexIndex").getAll((int[]) null);
		Vertex[] poly = new Vertex[4];
		int pi = 0;
		for(int i=0; i<vi.length; i++) {
			int index = vi[i];
			if(index<0) {
				index = (-index)-1;
				poly[pi++] = builder.vertices.get(index);
				if(pi==3)
					builder.add(new AdvancedMeshBuilder.Triangle(poly[0], poly[1], poly[2]));
				else if(pi==4)
					builder.add(new AdvancedMeshBuilder.Quad(poly[0], poly[1], poly[2], poly[3]));
				else throw new RuntimeException("Unknown face type, can do only tris and quads.");
				pi = 0;
			}
			else {
				poly[pi++] = builder.vertices.get(index);
			}
		}
		return vi;
	}
	
	private void setNormals(FbxTable table) {
		FbxTable normals = table.get("LayerElementNormal").getTable();
		if(normals.get("MappingInformationType").get(0).toString().equals("ByVertice") && normals.get("ReferenceInformationType").get(0).toString().equals("Direct")) {
			float[] vn = normals.get("Normals").getAll((float[]) null);
			for(int i=0; i<vn.length/3; i++) {
				Vertex v = builder.vertices.get(i);
				Vector3f norm = new Vector3f(vn[i*3+0], vn[i*3+2], -vn[i*3+1]);
				v.setNormal(norm);
				if(Math.abs(Math.abs(norm.x)-1f)<NORM_EPSILON)
					v.setTangent(new Vector3f(0, 1, 0));
				else
					v.setTangent(new Vector3f(1, 0, 0));
			}
		}
		else throw new RuntimeException(String.format("Specified MappingInformationType/ReferenceInformationType not impelemted yet: %s / %s",
				normals.get("MappingInformationType").get(0).toString(), normals.get("ReferenceInformationType").get(0).toString()));
	}
	
	private void setUvs(FbxTable table, int[] vi) {
		if(!table.contains("LayerElementUV"))
			return;
		FbxTable uvs = table.get("LayerElementUV").getTable();
		if(uvs.get("MappingInformationType").get(0).toString().equals("ByPolygonVertex") && uvs.get("ReferenceInformationType").get(0).toString().equals("IndexToDirect")) {
			float[] vt = uvs.get("UV").getAll((float[]) null);
			int[]  vti = uvs.get("UVIndex").getAll((int[]) null);
			for(int i=0; i<vti.length; i++) {
				int index = vi[i];
				if(index<0)
					index = (-index)-1;
				builder.vertices.get(index).setTexCoord(vt[vti[i]*2+0], 1f-vt[vti[i]*2+1]);
				// FIXME some vertices may need duplication?
			}
		}
		else throw new RuntimeException(String.format("Specified MappingInformationType/ReferenceInformationType not impelemted yet: %s / %s",
				uvs.get("MappingInformationType").get(0).toString(), uvs.get("ReferenceInformationType").get(0).toString()));
	}
	
	private StaticMesh load(FbxTable table) {
		setVertices(table);
		int[] indices = setIndices(table);
		setNormals(table);
		setUvs(table, indices);
		return builder.create();
	}

	public static StaticMesh loadFbx(String path, String meshName, float scale, VertexInfo info, MeshBuilder.Options options) {
		try {
			FbxTable fbx = new FbxParser().parse(AssetManager.defaultAssets.loadString(path));
			FbxArray objects = fbx.get("Objects");
			if(objects==null)
				throw new RuntimeException("No objects");
			List<FbxArray> models = objects.getTable().getAll("Model");
			if(models==null)
				throw new RuntimeException("No models");
			for(FbxArray mdl : models) {
				if((meshName==null || mdl.get(0).toString().equals(meshName)) && mdl.get(1).toString().equals("Mesh")) {
					return new FbxMeshLoader(new AdvancedMeshBuilder(info, options), scale).load(mdl.getTable());
				}
			}
			throw new RuntimeException("Mesh not found");
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
