package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.Vector;

public abstract class ParticleEntity extends Entity implements Positional {
	private Vector position;
	private Quaternion orientation;
	
	protected ParticleEntity() {
		super();
		
		this.position = Vector.ZERO;
	}
	
	public abstract void draw();
	
	public Vector getPosition() {
		return this.position;
	}
	
	public Quaternion getOrientation() {
		return this.orientation;
	}
	
	public void setPosition(Vector position) {
		this.position = position;
	}
	
	public void setOrientation(Quaternion orientation) {
		this.orientation = orientation;
	}
}
