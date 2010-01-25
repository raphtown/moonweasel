package org.atcs.moonweasel.entities.ships;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.atcs.moonweasel.physics.BoundingBox;
import org.atcs.moonweasel.physics.BoundingShape;
import org.atcs.moonweasel.physics.BoundingSphere;
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

		if (object.containsKey("gunners")) {
			List<Vector> gunners = new LinkedList<Vector>();
			JSONArray array = (JSONArray)object.get("gunners");
			JSONArray position;
			for (Object val : array) {
				position = (JSONArray)val;
				gunners.add(new Vector(
						((Double)position.get(0)).floatValue(),
						((Double)position.get(1)).floatValue(), 
						((Double)position.get(3)).floatValue()));
			}
			
			factory.gunners = gunners.toArray(new Vector[0]);
		} else {
			factory.gunners = new Vector[0];
		}
		
		if (object.containsKey("shape")) {
			if (object.get("shape").equals("box")) {
				JSONArray array = (JSONArray)object.get("bounds");
				factory.bounds = new BoundingBox(
						((Double)array.get(0)).floatValue(),
						((Double)array.get(1)).floatValue(), 
						((Double)array.get(2)).floatValue());
			} else if (object.get("shape").equals("sphere")) {
				factory.bounds = new BoundingSphere(
						((Double)object.get("bounds")).floatValue());
			}
		}
		
		return factory.build();
	}
	
	private static class ShipDataFactory {
		private String name;
		private float mass;
		private int health;
		private int attack;
		private Vector[] gunners;
		
		private BoundingShape bounds;
		
		public ShipData build() {
			return new ShipData(name, mass, health, attack, gunners, bounds);
		}
	}
	
	public final String name;
	public final float mass;
	public final int health;
	public final int attack;
	public final Vector[] gunners;
	
	public final BoundingShape bounds;
	
	private ShipData(String name, float mass, int health, int attack,
			Vector[] gunners, BoundingShape bounds) {
		assert name != null && name.length() > 0;
		assert mass > 0;
		assert health > 0;
		assert attack > 0;
		assert gunners != null;
		assert bounds != null;
		
		this.name = name;
		this.mass = mass;
		this.health = health;
		this.attack = attack;
		this.gunners = gunners;
		this.bounds = bounds;
	}
}
