package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.Player;
import org.atcs.moonweasel.util.Vector;

public class Ship extends ModelEntity implements Collidable {
	private Player pilot;
	private Player[] gunners;
	private Vector[] gunnerPositions;
	private Vector[] orientations;
	
	protected Ship(float mass, Vector[] gunnerPositions, Vector[] gunnerOrientations) {
		super(mass);

		assert mass > 0;
		assert gunnerPositions.length == gunnerOrientations.length;
				
		this.gunners = new Player[gunnerPositions.length];
		this.gunnerPositions = gunnerPositions;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void spawn() {
	}
}
