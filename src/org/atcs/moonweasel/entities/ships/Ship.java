package org.atcs.moonweasel.entities.ships;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.Vulnerable;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Vector;

public class Ship extends ModelEntity implements Vulnerable {
	private static final long serialVersionUID = 6162008933132397822L;

	private static Matrix BASE_TENSOR = Matrix.IDENTITY;
	
	private ShipData data;
	private int health;
	public final int MAX_HEALTH;
	
	private Player pilot;
	private Player[] gunners;
	private Vector[] gunnerPositions;
	
	protected Ship(ShipData data) {
		super(data.bounds, data.mass, BASE_TENSOR.scale(data.mass / 100));

		this.data = data;
		this.health = this.data.health;
		this.MAX_HEALTH = this.health;
		
		this.gunners = new Player[data.gunners.length];
		this.gunnerPositions = data.gunners;
	}

	@Override
	public void damage(int damage) {
		health -= damage;
		
		if (health <= 0) {
			destroy();
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		pilot.died();
		for (Player gunner : gunners) {
			gunner.died();
		}
		
		EntityManager manager = EntityManager.getEntityManager();

		Explosion explosion = manager.create("explosion");
		explosion.setPosition(this.getPosition());

		float distance = getState().mass / 1000;
		float damage;
		for (Ship ship : manager.getAllShipsInSphere(
				getState().position, distance)) {
			if (ship == this || ship.isDestroyed()) {
				continue;
			}
			
			damage = 50 * (1 - getState().position.distance(ship.getState().position) / distance);
			ship.damage((int)damage);
		}
	}
	
	public ShipData getData() {
		return this.data;
	}
	
	@Override
	public int getHealth() {
		return health;
	}
	
	@Override
	public int getOriginalHealth() {
		return this.data.health;
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
