package org.atcs.moonweasel;

import org.atcs.moonweasel.networking.Server;


public class Artemis extends Moonweasel {
	public Artemis(int width, int height, boolean fullscreen) {
		super(width, height, fullscreen);

		//		System.out.print("Enter server name: ");
		//		String serverName = new java.util.Scanner(System.in).nextLine();
		server = new Server("Moonweasel Server", this);
	}
}
