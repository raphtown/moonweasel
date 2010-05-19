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

	
	final int SENDS_PER_UPDATE = 3;
	int count = 0;
	protected void act(long next_logic_tick) 
	{
		count++;
//		if(count >= SENDS_PER_UPDATE)
//		{
			count = 0;
			t += SKIP_TICKS;
			this.next_logic_tick += SKIP_TICKS;
			loops++;
			entityManager.update(t);
			physics.update(t, SKIP_TICKS);
//		}
		server.act();

		

	}
}
