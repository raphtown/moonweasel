package org.atcs.moonweasel.util;

import java.io.Serializable;

public class AxisAngle implements Serializable {
	private static final long serialVersionUID = 6836121133678129040L;
	public final float angle;
	public final Vector axis;
	
	public AxisAngle(float angle, Vector axis) {
		this.angle = angle;
		this.axis = axis;
	}
}
