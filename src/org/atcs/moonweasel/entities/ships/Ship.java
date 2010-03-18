package org.atcs.moonweasel.entities.ships;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Laser;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.Vulnerable;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.players.UserCommand.Commands;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.MutableVector;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.TimedDerivative;
import org.atcs.moonweasel.util.Vector;

public class Ship extends ModelEntity implements Vulnerable {
	private static Matrix BASE_TENSOR = Matrix.IDENTITY;
	
	private ShipData data;
	private int health;
	
	private Player pilot;
	private Player[] gunners;
	private Vector[] gunnerPositions;
	
	protected Ship(ShipData data) {
		super(data.mass, BASE_TENSOR.scale(data.mass / 100));

		this.data = data;
		this.health = this.data.health;
		
		this.gunners = new Player[data.gunners.length];
		this.gunnerPositions = data.gunners;
	}
	
	public void apply(UserCommand command) {
		//Shooting
		if (command.get(Commands.ATTACK_1))
		{
			
			EntityManager manager = EntityManager.getEntityManager();
			Laser laser = manager.create("laser");
			laser.getState().orientation = this.getState().orientation;
			laser.getState().position = this.getPosition().add(new Vector(0.0f, 0.0f, 0.0f));
			laser.spawn();
			
		}
		
		applyMovement(command);
	}
	
	private void applyMovement(UserCommand command) {
		State state = getState();
		float f = data.thrust * 0.00001f; //50 newtons or 50 newton-meters, depending on context
		Vector relativeVelocity = state.orientation.inverse().rotate(state.velocity);
		
		MutableVector force = new MutableVector();
		MutableVector relativeForce = new MutableVector();
		MutableVector torque = new MutableVector();
		MutableVector relativeTorque = new MutableVector();
		
		// Mouse movement in x axis.
		if (command.get(Commands.ROLLING)) { // User wants to roll.
			relativeTorque.z += 0.001 * command.getMouse().x; // Scale mouse position. 
		} else { // Turn rather than roll.
			relativeTorque.y += 0.001 * command.getMouse().x;			
		}

		// Mouse movement in y axis.
		relativeTorque.x += 0.001 * command.getMouse().y;
				
		// Damp that angular motion!!!
		if (command.get(Commands.AUTOMATIC_THRUSTER_CONTROL)) {
			Vector dampTorque = new Vector(
					0.05f * state.angularVelocity.x,
					0.05f * state.angularVelocity.y,
					0.05f * state.angularVelocity.z);
			torque.minus(dampTorque);
		}

		// Thrusters
		if (command.get(Commands.FORWARD)) {
			relativeForce.z -= f;
		} 
		if (command.get(Commands.BACKWARD)) {
			relativeForce.z += f;
		}
		if (command.get(Commands.BOOST)) {
			relativeForce.z *= 5;
		}
		
		if (command.get(Commands.LEFT) && command.get(Commands.RIGHT)) {
		} else if (command.get(Commands.LEFT)) {
			relativeForce.x -= f;
		} else if (command.get(Commands.RIGHT)) {
			relativeForce.x += f;
		} else if (command.get(Commands.AUTOMATIC_THRUSTER_CONTROL)) {
			relativeForce.x -= 20 * relativeVelocity.x;
		}
		
		if (command.get(Commands.UP) && command.get(Commands.DOWN)) {
		} else if (command.get(Commands.UP)) {
			relativeForce.y += f;
		} else if (command.get(Commands.DOWN)) {
			relativeForce.y -= f;
		} else if (command.get(Commands.AUTOMATIC_THRUSTER_CONTROL)) {
			relativeForce.y -= 20 * relativeVelocity.y;
		}
		
		//// A little forward thrust.
		//if (command.get(Commands.AUTOMATIC_THRUSTER_CONTROL)) {
		//	relativeForce.z -= f / 2;
		//}
		
		force.sum(state.orientation.rotate(relativeForce.toVector()));
		torque.sum(state.orientation.rotate(relativeTorque.toVector()));
		state.addDerivative(new TimedDerivative(getTime(), 
				force.toVector(), torque.toVector()));
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
	
	public void draw(GL2 gl) {
		assert DISPLAY_LISTS.containsKey(this.getClass());
		
		gl.glCallList(DISPLAY_LISTS.get(this.getClass()));
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(this.state.velocity.x*1000,this.state.velocity.y*1000,this.state.velocity.z*1000);
			gl.glVertex3f(0,0,0);
		gl.glEnd();
	}
}
