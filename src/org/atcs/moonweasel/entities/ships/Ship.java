package org.atcs.moonweasel.entities.ships;

import java.util.Iterator;
import java.util.List;

import org.atcs.moonweasel.Debug;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Laser;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.Vulnerable;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.players.UserCommand.Commands;
import org.atcs.moonweasel.networking.IState;
import org.atcs.moonweasel.ranges.CustomRange;
import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.MutableVector;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.TimedDerivative;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Ship extends ModelEntity implements Vulnerable {
	private static final long serialVersionUID = 6162008933132397822L;

	private static Matrix BASE_TENSOR = Matrix.IDENTITY;
	private static float LASER_OFFSET = 0.3f;
	private static long COOLDOWN = 200;
	private static final float LASER_SCANNING_RANGE_Y = 0.25f; //radians
	private static final float LASER_SCANNING_RANGE_X = 0.1f;
	
	
	final static float MAX_SPEED = 0.025f;

	private ShipData data;
	private int health;

	private Player pilot;
	private Player[] gunners;
	public float laserOffset;
	private long nextFireTime;


	protected Ship(ShipData data) {
		super(data.mass, BASE_TENSOR.scale(data.mass / 100));

		this.data = data;
		this.health = this.data.health;

		this.gunners = new Player[data.gunners.length];
		addChange("created ship " + data);

		this.laserOffset = LASER_OFFSET;
		this.nextFireTime = 0;
	}

	public void apply(UserCommand command) {
		//Shooting
//		System.out.println("Applying command: " + command);
		if (command.get(Commands.ATTACK_1) && getTime() > nextFireTime)
		{
			EntityManager manager = EntityManager.getEntityManager();
			Laser laser = manager.create("laser");
			laser.setSource(this);
			laserOffset = -laserOffset;

			ModelEntity enemy = autoTargetingLaser();
			if (enemy != null)
			{
				laser.setTarget(enemy);
			}
			laser.spawn();

			nextFireTime = getTime() + COOLDOWN;
		}

		applyMovement(command);
	}

	public void draw()
	{
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		super.draw();
		GL11.glPopAttrib();
		drawExhaust();
	}

	private void drawExhaust()
	{
		Vector velocity = this.getState().orientation.inverse().rotate(this.getState().velocity);
		if(velocity.z < 0.0f)
		{
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float green = velocity.length() * 100;
			Cylinder cylinder = new Cylinder();
			Vector v;

			GL11.glPushMatrix();
			v = new Vector(-0.0305f, -0.015f, 0.491f);
			GL11.glTranslatef(v.x,v.y,v.z);
			GL11.glColor3f(1.0f, 1.0f - green, 0.0f);
			cylinder.draw(.015f,0.001f, .1f, 30, 30);
			GL11.glColor4f(0.8f, 0.75f * (1.0f - green), 0.0f, 0.5f);
			cylinder.draw(.02f,0.001f, .1f, 30, 30);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			v = new Vector(0.0305f, -0.015f, 0.491f);
			GL11.glTranslatef(v.x,v.y,v.z);
			GL11.glColor3f(1.0f, 1.0f - green, 0.0f);
			cylinder.draw(.015f,0.001f, .1f, 30, 30);
			GL11.glColor4f(0.8f, 0.75f * (1.0f - green), 0.0f, 0.5f);
			cylinder.draw(.02f,0.001f, .1f, 30, 30);
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopAttrib();
		}
	}

	private void applyMovement(UserCommand command) {
		State state = getState();
		float f = data.thrust * 0.0000025f; //50 newtons or 50 newton-meters, depending on context
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
		if (command.get(Commands.STOP)) {
			this.getState().momentum = Vector.ZERO;
			this.getState().angularMomentum = Vector.ZERO;
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
			Vector unit = this.getState().velocity.normalize();
			this.getState().momentum = unit.scale(MAX_SPEED * data.mass);
		}

		force.sum(state.orientation.rotate(relativeForce.toVector()));
		torque.sum(state.orientation.rotate(relativeTorque.toVector()));
		state.addDerivative(new TimedDerivative(getTime(), 
				force.toVector(), torque.toVector()));
//		addChange("apply command " + command);   This can't go here as long as new entities are sent as well
	}

	@Override
	public void damage(int damage) {
		health -= damage;
		System.out.println("Owie!  Damaged: " + damage + " Health left: " + health);
		if (health <= 0) {
			die();
		}
		addChange("damage " + damage);
	}
	public void die()
	{
		Debug.print("Ship was killed: " + this);
		if (pilot != null)
			pilot.die();
		for (Player gunner : gunners) {
			gunner.die();
		}
		explode();
	}

	@Override
	public void destroy() {
		super.destroy();
		Debug.print("Ship was destroyed: " + this);
		explode();
		addChange("destroy");
	}

	private void explode()
	{
		EntityManager manager = EntityManager.getEntityManager();
		Explosion explosion = manager.create("explosion");
		explosion.setPosition(this.getPosition());
		explosion.spawn();

		float distance = getState().getMass() / 1000;
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
		addChange("set pilot " + pilot.getID());
	}

	@Override
	public void spawn() {
		assert pilot != null;
		System.out.println("Respawning: " + this.health);
		this.health = this.data.health;
		respawn();
	}

	private Ship autoTargetingLaser()
	{
		Range<Ship> entitiesToCheck = shipsInFront();
		for(Ship me : entitiesToCheck)
		{
			Vector enemyPosition = this.getState().worldToBody.transform(me.getState().position).normalize();

			float thetaY = (new Vector(0,enemyPosition.y,enemyPosition.z)).angleBetween((new Vector(0,0,-1)));
			float thetaX = (new Vector(enemyPosition.x,0,enemyPosition.z)).angleBetween((new Vector(0,0,-1)));
			
			float distance = enemyPosition.length();
			float thetaScaleFactor = (float)(4*(Math.exp(-distance)) + 1)/3;
			
			if (thetaY <= thetaScaleFactor*LASER_SCANNING_RANGE_Y)
			{
				if (thetaX <= thetaScaleFactor*LASER_SCANNING_RANGE_X)
				{
					if (me instanceof Ship)
					{
						((Ship) me).damage(data.attack);
					}
					return me;
				}

			}
		}
		return null;

	}

	public Range<Ship> shipsInFront() //returns a list of all entities in front of this ship
	{
		EntityManager em = EntityManager.getEntityManager();
		final Ship me = this;
		CustomRange<Ship> range = new CustomRange<Ship>(em.getAllOfType(Ship.class)) {
			protected boolean filter(Ship e) {
				return (me.getState().worldToBody.transform(e.getState().position).z < 0);
			}
		};

		return range;
	}
	
	public void unpackageIState(IState is)
	{
		super.unpackageIState(is);
		List<Object> objects = is.objects;
		Iterator<Object> iter = objects.iterator();
		this.health =  (Integer) iter.next();
		iter.remove();
	}
	
	public IState packageIState()
	{
		IState is = super.packageIState();
		List<Object> objects = is.objects;
		objects.add(health);
		return is;
	}
}
