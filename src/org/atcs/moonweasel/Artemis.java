package org.atcs.moonweasel;

import org.atcs.moonweasel.networking.Server;

public class Artemis extends Moonweasel {
	protected Server server;
	
	public Artemis(boolean fullscreen) 
	{
		super(fullscreen);
		server = new Server("Moonweasel Server", this);
	}

	protected void act(final long next_logic_tick) 
	{
		server.act();
	}
}
