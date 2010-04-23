package org.atcs.moonweasel;

import org.atcs.moonweasel.networking.Server;
import org.lwjgl.opengl.DisplayMode;

public class Artemis extends Moonweasel {
	public Artemis(DisplayMode mode, boolean fullscreen) {
		super(mode, fullscreen);
		
		this.networking = new Server();
	}
}
