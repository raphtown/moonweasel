package org.atcs.moonweasel.entities.ships;

import javax.media.opengl.GL2;

import com.sun.opengl.util.gl2.GLUT;

public class Snowflake extends Ship {
	public static final String NAME = "snowflake";
	
	private static final ShipData DATA = 
		ShipData.loadShipData(Snowflake.class.getSimpleName().toLowerCase());
	private static GLUT glut = new GLUT();
	
	private long time;
	
	private Snowflake() {
		super(DATA);
	}
	
	@Override
	public void draw(GL2 gl) {
		glut.glutSolidCube(10);
	}
	
	@Override
	public void spawn() {
		time = System.currentTimeMillis();
		scheduleThink(1000);
	}
	
	@Override
	public void think() {
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();
		scheduleThink(1000);
	}
}
