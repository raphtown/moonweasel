package org.atcs.moonweasel.entities.ships;

import java.util.ArrayList;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Laser;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.Vulnerable;
import org.atcs.moonweasel.entities.particles.Exhaust;
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
	private static float LASER_OFFSET = 0.3f;
	private static long COOLDOWN = 200;
	//private static final float STERN = 0.1f;
	private static final float LASER_SCANNING_RANGE_Y = 0.15f; //radians
	private static final float LASER_SCANNING_RANGE_X = 0.05f;
	
	
	final static float MAX_SPEED = 0.05f;
	
	private ShipData data;
	private int health;
	
	private Player pilot;
	private Player[] gunners;
	private float laserOffset;
	private long nextFireTime;
	
	private Exhaust tailPipe;
	
	protected Ship(ShipData data) {
		super(data.mass, BASE_TENSOR.scale(data.mass / 100));

		this.data = data;
		this.health = this.data.health;
		
		this.gunners = new Player[data.gunners.length];
		this.laserOffset = LASER_OFFSET;
		this.nextFireTime = 0;
	}
	
	public void apply(UserCommand command) {
		//Shooting
		if (command.get(Commands.ATTACK_1) && getTime() > nextFireTime)
		{
			EntityManager manager = EntityManager.getEntityManager();
			Laser laser = manager.create("laser");
			laser.setSource(this, new Vector(laserOffset, 0, 0));
			laserOffset = -laserOffset;
			
			ModelEntity enemy = autoTargetingLaser();
			if (enemy != null)
			{
				System.out.println("Targeted an enemy - firing an auto-targeted laser");
				laser.setTarget(enemy);
			}
			else
			{
				System.out.println("Did not find an enemy - firing a straight laser");
			}
			laser.spawn();

			nextFireTime = getTime() + COOLDOWN;
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
			relativeTorque.z += -0.000005 * command.getMouse().x; // Scale mouse position. 
		} else { // Turn rather than roll.
			relativeTorque.y += -0.000005 * command.getMouse().x;
		}

		// Mouse movement in y axis.
		relativeTorque.x += -0.00001 * command.getMouse().y;
				
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
		
		if(this.getState().velocity.length() >= MAX_SPEED) {
			if ((relativeForce.x)/(this.getState().velocity.x) > 0) {
				relativeForce.x = 0;
			}
			if ((relativeForce.y)/(this.getState().velocity.y) > 0) {
				relativeForce.y = 0;
			}
			if ((relativeForce.z)/(this.getState().velocity.z) > 0) {
				relativeForce.z = 0;
			}			
		}
		
		
		System.out.println(this.getState().velocity.length());
		
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
		
		if (pilot != null)
			pilot.died();
		for (Player gunner : gunners) {
			gunner.died();
		}
		
		EntityManager manager = EntityManager.getEntityManager();
		Explosion explosion = manager.create("explosion");
		explosion.setPosition(this.getPosition());

		float distance = getState().mass / 1000;
		float damage;
		float scale;
		for (Ship ship : manager.getAllShipsInSphere(
				getState().position, distance)) {
			if (ship == this || ship.isDestroyed()) {
				continue;
			}
			
			scale = 1 - getState().position.distance(ship.getState().position) / distance;
			damage = data.mass * scale;
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
	
	public void killed(Ship target) {
		pilot.killedPlayer();
		for (Player gunner : gunners) {
			gunner.killedPlayer();
		}
	}
	
	public void setPilot(Player pilot) {
		this.pilot = pilot;
	}

	@Override
	public void spawn() {
		assert pilot != null;
		tailPipe = EntityManager.getEntityManager().create("exhaust");
		tailPipe.setShip(this);
		tailPipe.spawn();
	}
	
	public ModelEntity autoTargetingLaser()
	{
		ArrayList<ModelEntity> entitiesToCheck = entitiesInFront();
		for(ModelEntity me : entitiesToCheck)
		{
			Vector enemyPosition = this.getState().worldToBody.transform(me.getState().position).normalize();
			
			float thetaY = (new Vector(0,enemyPosition.y,enemyPosition.z)).angleBetween((new Vector(0,0,-1)));
			float thetaX = (new Vector(enemyPosition.x,0,enemyPosition.z)).angleBetween((new Vector(0,0,-1)));
			
			
			System.out.println("ThetaY: " + thetaY);
			System.out.println("ThetaX: " + thetaX);
			
			
			if (thetaY <= LASER_SCANNING_RANGE_Y)
			{
				if (thetaX <= LASER_SCANNING_RANGE_X)
				{
					if (me instanceof Ship)
					{
						((Ship) me).damage(20);
						System.out.println("Auto-targeted hitscan");
					}
					return me;
				}
				
			}
		}
		return null;
		
	}
	
//	public ModelEntity laserHitScan() //returns null if no collision, otherwise the object that was hit
//	{
//		// <a,b,c> + t*<x,y,z>
//		Vector x1 = this.getState().position;
//		Vector x2 = this.getState().bodyToWorld.transform(new Vector(0,0,-1));
//		
//		
//		ArrayList<ModelEntity> entitiesToCheck = entitiesInFront();
//		for(ModelEntity me : entitiesToCheck) //just checks hitscan on centroids
//		{
//			Vector x0 = me.getState().position;
//			float distance = (x0.subtract(x1).cross(x0.subtract(x2))).length()/(x2.subtract(x1)).length();
//			if(distance < Laser_Hitscan_Threshold)
//			{
//				System.out.println("LAZORED");
//				if(me instanceof Ship)
//				{
//					((Ship) me).damage(25);
//				}
//				return me;	 
//			}
//		}
//		System.out.println("Missed");
//		return null;
//	}
	
	public ArrayList<ModelEntity> entitiesInFront() //returns a list of all entities in front of this ship
	{
		ArrayList<ModelEntity> forwardEntities = new ArrayList<ModelEntity>();
		for(ModelEntity me : EntityManager.getEntityManager().getAllOfType(Ship.class))
		{
			if(this.getState().worldToBody.transform(me.getState().position).z < 0)
			{
				forwardEntities.add(me);
			}
		}
		//todo: add asteroid class here
		
		return forwardEntities;
	}
}
