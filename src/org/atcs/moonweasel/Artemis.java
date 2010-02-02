package org.atcs.moonweasel;

import org.atcs.moonweasel.networking.Server;

public class Artemis extends Moonweasel {
	public Artemis(int width, int height, boolean fullscreen) {
		super(width, height, fullscreen);
		
		this.networking = new Server();
	}
}
