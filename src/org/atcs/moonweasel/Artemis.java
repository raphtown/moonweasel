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

		for (int i = 0; i < 10; i++)
		{
			float x, y, z;
			x = (float)(Math.random()) * 50;
			y = (float)(Math.random()) * 50;
			z = (float)(Math.random()) * 50;

			if (Math.random() >= 0.5)
				x *= -1;
			if (Math.random() >= 0.5)
				y *= -1;
			if (Math.random() >= 0.5)
				z *= -1;
			
			float dx, dy, dz;
			dx = (float)(Math.random()) * 0.0025f;
			dy = (float)(Math.random()) * 0.0025f;
			dz = (float)(Math.random()) * 0.0025f;

			if (Math.random() >= 0.5)
				dx *= -1;
			if (Math.random() >= 0.5)
				dy *= -1;
			if (Math.random() >= 0.5)
				dz *= -1;
			

			Asteroid asteroid = EntityManager.getEntityManager().create("asteroid");
			asteroid.setPosition(new Vector(x, y, z));
			asteroid.setVelocity(new Vector(dx, dy, dz));
			asteroid.spawn();
		}
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
