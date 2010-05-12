package org.atcs.moonweasel.entities.particles;

import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.util.Quaternion;

public class Explosion extends ParticleEntity {
	public Explosion() {
	}
	
	public void draw() {
	}
	
	public void spawn() {
		setOrientation(Quaternion.ZERO);
	}
}
