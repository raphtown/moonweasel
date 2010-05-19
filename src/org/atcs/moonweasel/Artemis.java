package org.atcs.moonweasel;

import org.atcs.moonweasel.entities.Asteroid;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.networking.Server;
import org.atcs.moonweasel.util.Vector;

public class Artemis extends Moonweasel {
	protected Server server;
	
	public Artemis(boolean fullscreen) 
	{
		super(fullscreen);
		server = new Server("Moonweasel Server", this);

		Asteroid asteroid = EntityManager.getEntityManager().create("asteroid");
		asteroid.setPosition(new Vector(0, 0, 50));
		asteroid.spawn();
	}
	
	protected boolean shouldQuit() {
		return false;
	}
	
	protected void logic_act(long t, int skip_ticks) 
	{
		entityManager.update(t);
		physics.update(t, skip_ticks);
	}
	
	protected void render_act(float interpolation) {
		server.act();
	}
}
