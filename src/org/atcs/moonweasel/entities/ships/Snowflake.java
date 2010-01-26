package org.atcs.moonweasel.entities.ships;

public class Snowflake extends Ship {
	public static final String NAME = "snowflake";
	
	private static final ShipData DATA = 
		ShipData.loadShipData(Snowflake.class.getSimpleName().toLowerCase());
	
	private long time;
	
	private Snowflake() {
		super(DATA);
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
