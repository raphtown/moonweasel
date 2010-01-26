package org.atcs.moonweasel;

import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.physics.Physics;

public class Moonweasel {
	public static final Map<String, Class<? extends Entity>> ENTITY_MAP;

	static {
		ENTITY_MAP = new HashMap<String, Class<? extends Entity>>();
		ENTITY_MAP.put(Entity.getEntityType(Player.class), Player.class);
		ENTITY_MAP.put(Entity.getEntityType(Snowflake.class), Snowflake.class);
	}

	public static void main(String[] args) {
		Moonweasel weasel = new Moonweasel(800, 600, false);

		// weasel.seeFox();
		weasel.run();
		weasel.destroy(); // eaten

		System.exit(0);
	}

	private Physics physics;
	private WeaselView view;

	private EntityManager entityManager;

	private Moonweasel(int width, int height, boolean fullscreen) {
		this.physics = new Physics();

		this.entityManager = EntityManager.getEntityManager();
		
		Player player = this.entityManager.create("player");
		player.spawn();

		Snowflake snowflake = this.entityManager.create("snowflake");
		snowflake.setPilot(player);
		snowflake.spawn();
		player.setShip(snowflake);
		
		this.view = new WeaselView(width, height, fullscreen, player);
	}

	private void destroy() {
		physics.destroy();
		view.destroy();
	}

	private void run() {
		final int TICKS_PER_SECOND = 25;
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
				entityManager.update();
//				physics.update(t, SKIP_TICKS);

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
