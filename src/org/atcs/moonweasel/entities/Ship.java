package org.atcs.moonweasel.entities;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.Player;
import org.atcs.moonweasel.util.Vector;

public class Ship extends Spatial {
	private Player pilot;
	private Player[] gunners;
	private Vector[] gunnerPositions;
	private Vector[] orientations;
	
	protected Ship(Vector[] gunnerPositions, Vector[] gunnerOrientations) {
		assert gunnerPositions.length == gunnerOrientations.length;
		
		this.gunners = new Player[gunnerPositions.length];
		this.gunnerPositions = gunnerPositions;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(GL2 gl) {
	}
	
	@Override
	public void spawn() {
		// TODO Auto-generated method stub
		
	}
}
