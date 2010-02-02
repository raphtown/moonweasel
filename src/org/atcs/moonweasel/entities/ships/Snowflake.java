package org.atcs.moonweasel.entities.ships;

public class Snowflake extends Ship {
	public static final String NAME = "snowflake";
	
	private static final ShipData DATA = 
		ShipData.loadShipData(Snowflake.class.getSimpleName().toLowerCase());
	
	private Snowflake() {
		super(DATA);
	}
	
	@Override
	public void spawn() {
	}
	
	@Override
	public void think() {
	}
}
