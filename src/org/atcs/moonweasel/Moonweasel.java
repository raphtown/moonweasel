package org.atcs.moonweasel;

import java.io.IOException;
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
import org.atcs.moonweasel.sound.MidisLoader;

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
		int choice;
		if(args.length == 0)
		{
			Scanner scanner = new Scanner(System.in);
			System.out.println("Server (0) or Client (1) or both(2)?");
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
			Moonweasel weasel = new Lycanthrope(false);
			weasel.run();
		}
		else
		{
			Moonweasel weasel1 = new Artemis(false);
			weasel1.run();
			Runtime rt = Runtime.getRuntime();
			String[] cmdarray = {"java","-ea", "-Djava.security.policy=no.policy",
					"-Xmx256M", "org.atcs.moonweasel.Moonweasel", "1"};
			
			try
			{
				rt.exec(cmdarray);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			Moonweasel weasel2 = new Lycanthrope(false);
			weasel2.run();
		}
		
	}
	
	protected Physics physics;

	protected EntityManager entityManager;

	protected long t;

	protected Moonweasel(boolean fullscreen) 
	{
		this.entityManager = EntityManager.getEntityManager();
		this.physics = new Physics();
	}

	protected void destroy() {
		physics.destroy();
	}
	
	protected final int TICKS_PER_SECOND = 50;
	protected final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	protected final int MAX_FRAMESKIP = 5;
	
	protected void run() 
	{

		long next_logic_tick = System.currentTimeMillis();
		int loops;
		
		while (true) {
			
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) 
			{
				entityManager.update(t);
				physics.update(t, SKIP_TICKS);
				act(next_logic_tick);
				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
//				MidisLoader ml = new MidisLoader("Music.mid");
//				ml.play("Music", true);
			}
		}
	}
	
	protected abstract void act(long next_logic_tick);

	public long getT()
	{
		return t;
	}
}
