package org.atcs.moonweasel;

import org.atcs.moonweasel.networking.Server;


public class Artemis extends Moonweasel {

	protected Server server;
	
	public Artemis(int width, int height, boolean fullscreen) 
	{
		super(width, height, fullscreen);
		server = new Server("Moonweasel Server", this);
	}

	protected void act(long next_logic_tick) 
	{
		server.act();	
	}
}
