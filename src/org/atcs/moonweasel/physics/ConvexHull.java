package org.atcs.moonweasel.physics;

import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.gui.Loader;
import org.atcs.moonweasel.util.Vector;
import org.atcs.moonweasel.util.Vector.Direction;

public class ConvexHull {
	private static final Map<Class<? extends ModelEntity>, ConvexHull> hullMap;
	
	static {
		hullMap = new HashMap<Class<? extends ModelEntity>, ConvexHull>();
	}
	
	public enum Projection {
		XY(0, Direction.X, Direction.Y),
		YZ(2, Direction.Y, Direction.Z),
		ZX(1, Direction.Z, Direction.X);
		
		private final int offset;
		private final Direction x;
		private final Direction y;
		
		private Projection(int offset, Direction x, Direction y) {
			this.offset = offset;
			this.x = x;
			this.y = y;
		}
		
		public Direction get(Direction d) {
			Direction[] values = Direction.values();
			return values[(d.ordinal() + offset) % values.length];
		}
		
		public Vector project(Vector v) {
			return new Vector(v.get(x), v.get(y), 0);
		}
	}
	
	public static Vector[] getConvexHull(ModelEntity e, Projection p) {
		return getConvexHull(e.getClass(), p);
	}
	
	public static Vector[] getConvexHull(Class<? extends ModelEntity> clazz, 
			Projection p) {
		if (!hullMap.containsKey(clazz)) {
			Vector[] geometry = Loader.getGeometry(clazz.getSimpleName().toLowerCase());
			ConvexHull hull = new ConvexHull(geometry);
			hullMap.put(clazz, hull);
		}
		
		return hullMap.get(clazz).getProjection(p);
	}
	
	private final Vector[] geometry;
	private final Vector[][] convexHull;
	
	private ConvexHull(Vector[] geometry) {
		this.geometry = geometry;
		this.convexHull = new Vector[Projection.values().length][];
	}
	
	private Vector[] getProjection(Projection p) {
		if (convexHull[p.ordinal()] == null) {
			convexHull[p.ordinal()] = ConvexHullMaker.makeConvexHull(geometry, p);
		}
		
		return convexHull[p.ordinal()];
	}
}
