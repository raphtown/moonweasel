package org.atcs.moonweasel.entities.ships;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.Vulnerable;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Vector;

public class Ship extends ModelEntity implements Vulnerable {
	private static Matrix BASE_TENSOR = Matrix.IDENTITY;
	
	private final int ORIGINAL_HEALTH;
	private int health;
	
	private Player pilot;
	private Player[] gunners;
	private Vector[] gunnerPositions;
	
	protected Ship(ShipData data) {
		super(data.bounds, data.mass, BASE_TENSOR.scale(data.mass / 100));

		this.ORIGINAL_HEALTH = data.health;
		this.health = this.ORIGINAL_HEALTH;
		
		this.gunners = new Player[data.gunners.length];
		this.gunnerPositions = data.gunners;
	}

	@Override
	public void damage(int damage) {
		health -= damage;
	}
	
	@Override
	public void destroy() {
		EntityManager manager = EntityManager.getEntityManager();
		float distance = getState().mass / 1000;
		float damage;
		for (Ship ship : manager.getAllShipsInSphere(
				getState().position, distance)) {
			if (ship == this) {
				continue;
			}
			
			damage = 50 * (1 - getState().position.distance(ship.getState().position) / distance);
			ship.damage((int)damage);
		}
	}
	
	@Override
	public int getHealth() {
		return health;
	}
	
	@Override
	public int getOriginalHealth() {
		return ORIGINAL_HEALTH;
	}
	
	public Player getPilot() {
		return pilot;
	}
	
	public void setPilot(Player pilot) {
		this.pilot = pilot;
	}

	@Override
	public void spawn() {
	}
}
