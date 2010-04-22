package org.atcs.moonweasel.physics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.atcs.moonweasel.physics.ConvexHull.Projection;
import org.atcs.moonweasel.util.Edge;
import org.atcs.moonweasel.util.Vector;

public class ConvexHullMaker
{	
	public static Vector[] makeConvexHull(Vector[] points, Projection p) {
		Set<Vector> remainingPoints = projectOntoPlane(points, p);
		List<Edge> edges = new LinkedList<Edge>();  
		Vector left = remainingPoints.iterator().next();
		Vector highest = left;
		Vector lowest = left;
		
		for (Vector v : remainingPoints) {
			if (v.x < left.x) {
				left = v;
			}
			
			if (v.y > highest.y) {
				highest = v;
			} else if (v.y < lowest.y) {
				lowest = v;
			}
		}
		edges.add(new Edge(highest, left));
		edges.add(new Edge(left, lowest));
		edges.add(new Edge(lowest, highest));
		
		hull(remainingPoints, edges);
		
		return toPolygon(edges);
	}
	
	private static void hull(Set<Vector> remainingPoints, List<Edge> edges) {
		Iterator<Vector> iter = remainingPoints.iterator();
		Vector point;
		int index;
		List<Edge> visisbleEdges = new LinkedList<Edge>();
		while (iter.hasNext()) {
			point = iter.next();
			iter.remove();
			
			index = findIndexOfNonvisibleEdge(point, edges);
			if (index < 0) {
				// Inside hull.
				continue;
			}
			
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get((index + i) % edges.size()).visibleFromPoint(point)) {
					visisbleEdges.add(edges.get((index + i) % edges.size()));
				}
			}
			Vector startConnector = visisbleEdges.get(0).getStartPoint();
			Vector endConnector = visisbleEdges.get(visisbleEdges.size() - 1).getEndPoint();
			edges.removeAll(visisbleEdges);
			edges.add(index, new Edge(startConnector, point));
			edges.add(index + 1, new Edge(point, endConnector));
			visisbleEdges.clear();
		}
	}
	
	private static Set<Vector> projectOntoPlane(Vector[] points, Projection p) {
		Set<Vector> projected = new HashSet<Vector>(points.length);
		for (Vector v : points) {
			projected.add(p.project(v));
		}
		
		return projected;
	}
	
	private static int findIndexOfNonvisibleEdge(Vector v, List<Edge> hull) {
		int index = 0; 
		Iterator<Edge> iter = hull.iterator();
		Edge e;
		while (iter.hasNext()) {
			e = iter.next();

			if (e.visibleFromPoint(v)) {
				return index;
			}
			
			index++;
		}
		
		return -1;
	}
	
	private static Vector[] toPolygon(List<Edge> edges) {
		Vector[] polygon = new Vector[edges.size()];
		int index = 0; 
		Iterator<Edge> iter = edges.iterator();
		Edge e;
		while (iter.hasNext()) {
			e = iter.next();
			
			polygon[index] = e.getStartPoint();
			
			index++;
		}
		
		return polygon;
	}
}
