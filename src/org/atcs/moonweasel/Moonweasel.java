package org.atcs.moonweasel;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.atcs.moonweasel.entities.EnergyBomb;
import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Laser;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.physics.Physics;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

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
	}

	public static void main(String[] args) {
		int width = 800, height = 600;
		DisplayMode mode = null;
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();

			for (int i = 0; i < modes.length; i++) {
				if ((modes[i].getWidth() == width)
						&& (modes[i].getHeight() == height)) {
					mode = modes[i];
					break;
				}
			}
			
			if (mode == null) {
				throw new RuntimeException(String.format(
						"Unable to find target display mode width %d, height %d.",
						width, height));
			}
		} catch (LWJGLException e) {
			throw new RuntimeException("Unable to choose display mode.", e);
		}

		Scanner scanner = new Scanner(System.in);
		System.out.println("Server (0) or Client (1)?");
		Moonweasel weasel;
		int choice = scanner.nextInt();
		if(choice == 0)
		{
			weasel = new Artemis(mode, false);
		}
		else
		{
			weasel = new Lycanthrope(mode, false);
		}

		// weasel.seeFox();
		weasel.run();
		weasel.destroy(); // eaten

		System.exit(0);
	}

	protected final int TICKS_PER_SECOND = 25;
	protected final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	protected final int MAX_FRAMESKIP = 5;
	
	protected Physics physics;

	protected EntityManager entityManager;

	protected long t;

	protected Moonweasel(DisplayMode mode, boolean fullscreen) {
		this.entityManager = EntityManager.getEntityManager();
		this.physics = new Physics();
	}

	protected void destroy() {
		physics.destroy();
	}
	protected void run() {
		final int TICKS_PER_SECOND = 50;
		final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
		final int MAX_FRAMESKIP = 5;
		long next_logic_tick = System.currentTimeMillis();
		int loops;
		
		while (true) {
			
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) 
			{
				entityManager.update(t);
				physics.update(t, SKIP_TICKS);
				
				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
			}
			this.act(next_logic_tick);
		}
	}
	
	protected abstract void act(long next_logic_tick);

	public long getT()
	{
		return t;
	}
}
