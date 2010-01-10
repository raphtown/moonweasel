package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Vector;

public class Snowflake extends Ship {
	public static final String NAME = "snowflake";
	
	private long time;
	
	public Snowflake() {
		super(1000, new Vector[0], new Vector[0]);
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
