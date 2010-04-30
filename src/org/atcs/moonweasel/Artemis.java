package org.atcs.moonweasel;

import org.atcs.moonweasel.networking.Server;
import org.lwjgl.opengl.DisplayMode;

public class Artemis extends Moonweasel {
	protected Server server;
	
	public Artemis(DisplayMode mode, boolean fullscreen) 
	{
		super(mode, fullscreen);
		server = new Server("Moonweasel Server", this);
	}

	protected void act(long next_logic_tick) 
	{
		server.act();
	}
}
