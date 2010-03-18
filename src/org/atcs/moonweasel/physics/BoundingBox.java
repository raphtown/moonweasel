package org.atcs.moonweasel.physics;

public class BoundingBox implements BoundingShape {
	private static final long serialVersionUID = -4510379773664613105L;
	public final float maxX, maxY, maxZ;
	
	public BoundingBox(float maxX, float maxY, float maxZ) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
}
