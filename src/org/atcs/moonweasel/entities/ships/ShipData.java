package org.atcs.moonweasel.entities.ships;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.atcs.moonweasel.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ShipData {
	public static ShipData loadShipData(String type) {
		JSONObject object;
		try {
			object = (JSONObject)JSONValue.parse(new FileReader(String.format("data/ships/%s.json", type)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		ShipDataFactory factory = new ShipDataFactory();
		if (object.containsKey("name")) {
			factory.name = (String)object.get("name");
		} else {
			factory.name = type;
		}
		
		if (object.containsKey("mass")) {
			factory.mass = ((Double)object.get("mass")).floatValue();
		}
		if (object.containsKey("health")) {
			factory.health = ((Long)object.get("health")).intValue();
		}
		if (object.containsKey("attack")) {
			factory.attack = ((Long)object.get("attack")).intValue();
		}
		if (object.containsKey("thrust")) {
			factory.thrust = ((Double)object.get("thrust")).floatValue();
		}

		if (object.containsKey("gunners")) {
			List<Vector> gunners = new LinkedList<Vector>();
			JSONArray array = (JSONArray)object.get("gunners");
			JSONArray position;
			for (Object val : array) {
				position = (JSONArray)val;
				gunners.add(new Vector(
						((Double)position.get(0)).floatValue(),
						((Double)position.get(1)).floatValue(), 
						((Double)position.get(2)).floatValue()));
			}
			
			factory.gunners = gunners.toArray(new Vector[0]);
		} else {
			factory.gunners = new Vector[0];
		}
				
		if (object.containsKey("cameraPosOffset"))
		{
			JSONArray position = (JSONArray)object.get("cameraPosOffset");
			factory.cameraPosOffset = (new Vector(
					((Double)position.get(0)).floatValue(),
					((Double)position.get(1)).floatValue(), 
					((Double)position.get(2)).floatValue()));
		}
		
		if (object.containsKey("cameraLookOffset"))
		{
			JSONArray position = (JSONArray)object.get("cameraLookOffset");
			factory.cameraLookOffset = (new Vector(
					((Double)position.get(0)).floatValue(),
					((Double)position.get(1)).floatValue(), 
					((Double)position.get(2)).floatValue()));
		}
		
		return factory.build();
	}
	
	private static class ShipDataFactory {
		private String name;
		private float mass;
		private int health;
		private int attack;
		private float thrust;
		private Vector[] gunners;
		private Vector cameraPosOffset;
		private Vector cameraLookOffset;
		
		
		public ShipData build() {
			return new ShipData(name, mass, health, attack, thrust, gunners, cameraPosOffset, cameraLookOffset);
		}
	}
	
	public final String name;
	public final float mass;
	public final int health;
	public final int attack;
	public final float thrust;
	public final Vector[] gunners;
	public final Vector cameraPosOffset;
	public final Vector cameraLookOffset;
	
	private ShipData(String name, float mass, int health, int attack,
			float thrust, Vector[] gunners, Vector cameraPosOffset, 
			Vector cameraLookOffset) {
		assert name != null && name.length() > 0;
		assert mass > 0;
		assert health > 0;
		assert attack > 0;
		assert thrust > 0;
		assert gunners != null;
		
		this.name = name;
		this.mass = mass;
		this.health = health;
		this.attack = attack;
		this.thrust = thrust;
		this.gunners = gunners;


		this.cameraPosOffset = cameraPosOffset;
		this.cameraLookOffset = cameraLookOffset;
	}
}
