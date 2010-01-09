package org.atcs.moonweasel.particles;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.entities.Ship;

public class Explosion extends Particle {
	private Ship creator;
	
	public Explosion(Ship creator) {
		this.creator = creator;
	}
	
	public void destroy() {
	}
	
	public void draw(GL2 gl) {
	}
	
	public void spawn() {
		teleport(creator.getState().position);
	}
}
