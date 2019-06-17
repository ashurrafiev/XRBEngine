package com.xrbpowered.gl.collider;

import javax.swing.JPanel;

import org.lwjgl.util.vector.Vector2f;

public class Collider extends JPanel {

	public static final float EPSILON = 0.01f; // floating point comparison requires threshold, 1% is enough in this case
	
	private static float cross(Vector2f a, Vector2f b) {
		return a.x*b.y - a.y*b.x;
	}
	
	private static Vector2f intersect(Vector2f q, Vector2f s, Vector2f p, Vector2f r, Vector2f n) {
		if(Vector2f.dot(n, s)>=0f)
			return null;
		float rxs = cross(r, s);
		if(rxs==0f)
			return null;
		
		Vector2f qp = Vector2f.sub(q, p, null);
		float t = cross(qp, s) / rxs;
		float u = cross(qp, r) / rxs;
		if(t<-EPSILON || t>1f+EPSILON || u<-EPSILON || u>1f+EPSILON)
			return null;
		
		return new Vector2f(u*s.x, u*s.y);
	}
	
	private static Vector2f slide(Vector2f us, Vector2f s, Vector2f rn) {
		float dot = (s.x-us.x)*rn.x + (s.y-us.y)*rn.y; 
		return new Vector2f(dot*rn.x, dot*rn.y);
	}
	
	public static Vector2f calculateDestination(Iterable<ColliderEdge> edges, Vector2f q, Vector2f s, Vector2f dest, boolean slide) {
		if(dest==null)
			dest = new Vector2f();
		dest.x = q.x+s.x;
		dest.y = q.y+s.y;
		if(s.length()==0f)
			return dest;
		float min = 0f;
		Vector2f minus = null;
		ColliderEdge mine = null;
		for(ColliderEdge e : edges) {
			Vector2f us = intersect(q, s, e.pivot, e.delta, e.normal);
			if(us!=null) {
				float len = us.length();
				if(minus==null || len<min) {
					min = len;
					minus = us;
					mine = e;
				}
			}
		}
		if(minus!=null) {
			if(slide) {
				Vector2f s0 = slide(minus, s, mine.tangent);
				return calculateDestination(edges, Vector2f.add(q, minus, null), s0, dest, false);
			}
			else {
				dest.x = q.x+minus.x;
				dest.y = q.y+minus.y;
				return dest;
			}
		}
		return dest;
	}

}
