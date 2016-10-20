package com.xrbpowered.gl.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.xrbpowered.gl.res.StandardMeshBuilder.Vertex;
import com.xrbpowered.utils.assets.AssetManager;

public class ObjMeshLoader {

	private ArrayList<Vector3f> v = new ArrayList<>();
	private ArrayList<Vector2f> vt = new ArrayList<>();
	private ArrayList<Vector3f> vn = new ArrayList<>();

	private StandardMeshBuilder builder = new StandardMeshBuilder();
	private HashMap<String, Vertex> indexMap = new HashMap<>();
	
	public final float scale;

	private ObjMeshLoader(float scale) {
		this.scale = scale;
	}
	
	private static Vector2f vec2(String[] s) {
		return new Vector2f(Float.parseFloat(s[1]), 1f-Float.parseFloat(s[2]));
	}

	private static Vector3f vec3(String[] s) {
		return new Vector3f(Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]));
	}
	
	private Vertex getVertex(String sv) {
		Vertex v = indexMap.get(sv);
		if(v==null) {
			v = new Vertex(); 
			String[] s = sv.split("\\/", 4);
			v.setPosition(this.v.get(Integer.parseInt(s[0])-1));
			if(s.length>1 && !s[1].isEmpty())
				v.setTexCoord(this.vt.get(Integer.parseInt(s[1])-1));
			if(s.length>2 && !s[2].isEmpty()) {
				v.setNormal(this.vn.get(Integer.parseInt(s[2])-1));
				if(v.normal.x==1f || v.normal.x==-1f)
					v.setTangent(new Vector3f(0, 1, 0));
			}
			// FIXME tangents
			builder.add(v);
		}
		return v;
	}
	
	private StaticMesh load(Scanner in) {
		while(in.hasNextLine()) {
			String[] s = in.nextLine().split("\\s+", 6);
			if("v".equals(s[0]))
				v.add((Vector3f) vec3(s).scale(scale));
			else if("vn".equals(s[0]))
				vn.add(vec3(s));
			else if("vt".equals(s[0]))
				vt.add(vec2(s));
			else if("f".equals(s[0])) {
				if(s.length==4)
					builder.add(new StandardMeshBuilder.Triangle(getVertex(s[1]), getVertex(s[2]), getVertex(s[3])) /*.calcTangents()*/ );
				else if(s.length==5)
					builder.add(new StandardMeshBuilder.Quad(getVertex(s[1]), getVertex(s[2]), getVertex(s[3]), getVertex(s[4])) /*.calcTangents()*/ );
				else if(s.length==3)
					builder.add(new StandardMeshBuilder.Edge(getVertex(s[1]), getVertex(s[2])));
				else throw new RuntimeException("Unknown face type, can do only tris and quads.");
			}
			else if("o".equals(s[0]))
				break;
		}
		return builder.create(false);
	}
	
	private static void skip(Scanner in) {
		while(in.hasNextLine()) {
			String[] s = in.nextLine().split("\\s+", 2);
			if("o".equals(s[0]))
				break;
		}
	}
	
	public static StaticMesh[] loadAll(String path, float scale) {
		try {
			ArrayList<StaticMesh> meshes = new ArrayList<>();
			// Scanner in = new Scanner(f);
			Scanner in = new Scanner(AssetManager.defaultAssets.openStream(path));
			skip(in);
			for(;;) {
				StaticMesh m = new ObjMeshLoader(scale).load(in);
				if(m==null)
					return meshes.toArray(null);
				else
					meshes.add(m);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static StaticMesh loadObj(String path, int objIndex, float scale) {
		try {
			// Scanner in = new Scanner(f);
			Scanner in = new Scanner(AssetManager.defaultAssets.openStream(path));
			for(int i=0; i<objIndex+1; i++)
				skip(in);
			return new ObjMeshLoader(scale).load(in);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
