package org.atcs.moonweasel;

import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Snowflake;
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
		this.view = new WeaselView(width, height, fullscreen);

		this.entityManager = EntityManager.getEntityManager();
		Snowflake snowflake = this.entityManager.create("snowflake");
		snowflake.spawn();
	}

	private void destroy() {
		physics.destroy();
		view.destroy();
	}

	private void run() {
		final float dt = 10f; // note: matt redmond correcting a slight mistake
								// on Drew's (?) part.
		// this should be in millseconds, not seconds

		long currentTime = System.currentTimeMillis();
		long newTime, deltaTime;
		float accumulator = 0.0f;
		float t = 0.0f;

		while (!view.shouldQuit()) {
			entityManager.update();

			newTime = System.currentTimeMillis();
			deltaTime = newTime - currentTime;
			currentTime = newTime;

			accumulator += deltaTime;
			while (accumulator >= dt) {
				physics.update(t, dt);

				t += dt;
				accumulator -= dt;
			}

			view.render(accumulator / dt);
		}
	}
}
