package org.atcs.moonweasel.physics;

public class BoundingSphere implements BoundingShape {
	private static final long serialVersionUID = -3374847166095950287L;
	public final float radius;
	
	public BoundingSphere(float radius) {
		this.radius = radius;
	}
}
