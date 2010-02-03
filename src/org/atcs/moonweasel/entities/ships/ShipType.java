package org.atcs.moonweasel.entities.ships;

public enum ShipType
{	
	SNOWFLAKE((byte)1, "snowflake");
	public final byte type;
	public final String typeName;
	private ShipType(byte type, String typeName) {
		this.type = type;
		this.typeName = typeName;
	}
	
	public static ShipType getFromType(byte type)
	{
		return values()[type - 1];
	}
}
