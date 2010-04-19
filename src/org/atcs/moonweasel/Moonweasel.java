package org.atcs.moonweasel;

import java.util.Map;
import java.util.TreeMap;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.networking.Server;
import org.atcs.moonweasel.physics.Physics;

public class Moonweasel
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
	}

	public static void main(String[] args) {
		Moonweasel weasel = new Artemis(800, 600, false);
		weasel.run();
		weasel.destroy(); // eaten

		System.exit(0);
	}

	protected Physics physics;

	protected Server server;

	protected EntityManager entityManager;

	protected long t;

	protected Moonweasel(int width, int height, boolean fullscreen) {
		this.entityManager = EntityManager.getEntityManager();


		this.physics = new Physics();
	}

	private void destroy() {
		physics.destroy();
	}

	protected void run() {
		final int TICKS_PER_SECOND = 25;
		final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
		final int MAX_FRAMESKIP = 5;

		t = 0;
		long next_logic_tick = System.currentTimeMillis();
		int loops;
		long time = System.currentTimeMillis();
		long delta = 0;
		long temp = 0;
		int count = 0;
		while (true) {
			
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) 
			{
				server.act();
				time = System.currentTimeMillis();
				entityManager.update(t);
				physics.update(t, SKIP_TICKS);
				temp = System.currentTimeMillis();
				delta = temp - time;
				time = temp;
				if(delta > 10)
				{
					Debug.print("Mini: " + delta);
				}
				
				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
				count++;
			}
		}
	}

	public long getT()
	{
		return t;
	}
}
