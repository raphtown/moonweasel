package org.atcs.moonweasel;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Networking;
import org.atcs.moonweasel.physics.Physics;
import org.atcs.moonweasel.util.Vector;

public class Moonweasel {
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

		// weasel.seeFox();
		weasel.run();
		weasel.destroy(); // eaten

		System.exit(0);
	}

	protected Physics physics;
	protected WeaselView view;
	protected InputController input;
	protected Networking networking;

	private EntityManager entityManager;
	private Player player;

	protected Moonweasel(int width, int height, boolean fullscreen) {
		this.entityManager = EntityManager.getEntityManager();
		
		player = this.entityManager.create("player");
		player.spawn();

		Snowflake snowflake = this.entityManager.create("snowflake");
		snowflake.setPilot(player);
		snowflake.spawn();
		player.setShip(snowflake);
		
		Snowflake snowflake2 = this.entityManager.create("snowflake");
		snowflake2.setPosition(new Vector(0,0,-10));
		snowflake2.spawn();
		
		this.physics = new Physics();
		this.view = new WeaselView(width, height, fullscreen, player);
		this.input = new InputController(view.getWindow());
	}

	private void destroy() {
		physics.destroy();
		view.destroy();
	}
	
	private void run() {
		final int TICKS_PER_SECOND = 50;
		final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
		final int MAX_FRAMESKIP = 5;

		long t = 0;
		long next_logic_tick = System.currentTimeMillis();
		int loops;
		float interpolation;
		
		while (!view.shouldQuit()) {
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) {
				entityManager.update(t);
				physics.update(t, SKIP_TICKS);
				player.addCommand(input.poll(t));

				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
			}

			interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) 
				/ SKIP_TICKS;
			view.render(interpolation);
		}
	}
}
