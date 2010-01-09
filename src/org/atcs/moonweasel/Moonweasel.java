package org.atcs.moonweasel;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Snowflake;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.physics.Physics;

public class Moonweasel {
	public static final Map<String, Class<? extends Entity>> ENTITY_MAP;
	
	static {
		ENTITY_MAP = new HashMap<String, Class<? extends Entity>>();
		ENTITY_MAP.put("player", Player.class);
		ENTITY_MAP.put("snowflake", Snowflake.class);
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
		this.view = new WeaselView(800, 600, fullscreen);
		
		this.entityManager = EntityManager.getEntityManager();
		Snowflake snowflake = this.entityManager.create("snowflake");
		snowflake.spawn();
	}
	
	private void destroy() {
		physics.destroy();
		view.destroy();
	}
	
	private void run() {
		final float dt = 0.01f;
		
		float currentTime = System.currentTimeMillis();
		float newTime, deltaTime;
		float accumulator = 0.0f;
		float t = 0.0f;
		
		while (!view.shouldQuit()) {
			newTime = System.currentTimeMillis();
			deltaTime = newTime - currentTime;
			currentTime = newTime;
			
			accumulator += deltaTime;
			while (accumulator >= dt) {
				accumulator -= dt;
				physics.update(t, dt);
				
				t += dt;
				accumulator -= dt;
			}
			
			entityManager.update();
			view.render(accumulator / dt);
		}
	}
}
