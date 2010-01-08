package org.atcs.moonweasel;

import org.atcs.moonweasel.gui.WeaselView;

public class Moonweasel {
	public static void main(String[] args) {
		WeaselView view = new WeaselView(800, 600);
		
		do {
			view.render();
		} while (!view.shouldQuit());
		view.destroy();
		
		System.exit(0);
	}
}
