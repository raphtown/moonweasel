package org.atcs.moonweasel;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.atcs.moonweasel.entities.Asteroid;
import org.atcs.moonweasel.entities.EnergyBomb;
import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Laser;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.physics.ConvexHull;
import org.atcs.moonweasel.physics.Physics;
import org.atcs.moonweasel.physics.ConvexHull.Projection;

@SuppressWarnings("unchecked")
public abstract class Moonweasel
{
	private static final Map<String, Class<? extends Entity>> ENTITY_MAP;
	private static final Map<Integer, Class<? extends Entity>> ENTITY_TYPE_ID_MAP;

	private static void addEntityClass(Class<? extends Entity> clazz) {
		ENTITY_MAP.put(Entity.getEntityType(clazz), clazz);
		ENTITY_TYPE_ID_MAP.put(clazz.toString().hashCode(), clazz);
	}

	public static int getTypeID(Class<? extends Entity> clazz) {
		return clazz.toString().hashCode();
	}

	public static Class<? extends Entity> getEntityClassByID(int id) {
		return ENTITY_TYPE_ID_MAP.get(id);
	}

	public static Class<? extends Entity> getEntityClassByName(String name) {
		return ENTITY_MAP.get(name);
	}

	static {
		ENTITY_MAP = new TreeMap<String, Class<? extends Entity>>();
		ENTITY_TYPE_ID_MAP = new TreeMap<Integer, Class<? extends Entity>>();
		addEntityClass(Player.class);
		addEntityClass(Snowflake.class);
		addEntityClass(Explosion.class);
		addEntityClass(Laser.class);
		addEntityClass(EnergyBomb.class);
		addEntityClass(Asteroid.class);
		
		for (Class<? extends Entity> clazz : ENTITY_MAP.values()) {
			if (ModelEntity.class.isAssignableFrom(clazz)) {
				ConvexHull.getConvexHull((Class<? extends ModelEntity>)clazz, Projection.XY);
				ConvexHull.getConvexHull((Class<? extends ModelEntity>)clazz, Projection.YZ);
				ConvexHull.getConvexHull((Class<? extends ModelEntity>)clazz, Projection.ZX);
			}
		}
	}

	public static void main(String[] args) {
		int choice;
		if(args.length == 0)
		{
			Scanner scanner = new Scanner(System.in);
			System.out.println("Server (0) or Client (1)?");
			choice = scanner.nextInt();
		}
		else
		{
			choice = Integer.parseInt(args[0]);
		}

		if(choice == 0)
		{
			Moonweasel weasel = new Artemis(false);
			weasel.run();
		}
		else if(choice == 1)
		{
			Moonweasel weasel = new Lycanthrope(true);
			weasel.run();
		}
	}
	
	protected Physics physics;
	protected EntityManager entityManager;

	protected Moonweasel(boolean fullscreen) 
	{
		this.entityManager = EntityManager.getEntityManager();
		this.physics = new Physics();
	}

	protected void destroy() {
		physics.destroy();
	}
	
	protected void run() 
	{
		final int TICKS_PER_SECOND = 50;
		final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
		final int MAX_FRAMESKIP = 5;

		long t = 0;
		long next_logic_tick = System.currentTimeMillis();
		int loops;
		float interpolation;
		
		while (!shouldQuit()) {
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) {
				logic_act(t, SKIP_TICKS);

				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
			}

			interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) 
					/ SKIP_TICKS;
			render_act(interpolation);
		}
	}
	
	protected abstract boolean shouldQuit();
	
	protected abstract void logic_act(long t, int skip_ticks);
	protected abstract void render_act(float interpolation);
}
