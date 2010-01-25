package org.atcs.moonweasel.physics;

public class BoundingBox implements BoundingShape {
	public final float maxX, maxY, maxZ;
	
	public BoundingBox(float maxX, float maxY, float maxZ) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
}
