package org.atcs.moonweasel.entities.particles;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.entities.ships.Ship;

public class Explosion extends ParticleEntity {
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
