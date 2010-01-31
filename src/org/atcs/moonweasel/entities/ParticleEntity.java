package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Vector;

public abstract class ParticleEntity extends Entity implements Positional {
	private Vector position;
	
	protected ParticleEntity() {
		super();
		
		this.position = Vector.ZERO;
	}
	
	public Vector getPosition() {
		return this.position;
	}
	
	public void teleport(Vector position) {
		this.position = position;
	}
}
