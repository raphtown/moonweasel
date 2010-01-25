package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Vector;

public class Snowflake extends Ship
{
	private static final long serialVersionUID = 8769516548398728687L;

	public static final String NAME = "snowflake";
	
	private long time;
	
	private Snowflake() {
		super(1000, new Matrix(1,0,0,0,1,0,0,0,1), 
				new Vector[0], new Vector[0]);
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
