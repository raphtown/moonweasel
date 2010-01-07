package org.atcs.moonweasel.entities;

import javax.media.opengl.GL2;

public abstract class Spatial extends Entity {
	protected State state;
	
	public Spatial() {
		super();
		
		this.state = new State();
	}
	
	public abstract void draw(GL2 gl);
}
