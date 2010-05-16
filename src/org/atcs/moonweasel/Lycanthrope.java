package org.atcs.moonweasel;

import java.util.List;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Client;
import org.atcs.moonweasel.networking.changes.ChangeCompiler;
import org.atcs.moonweasel.networking.changes.ChangeList;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Lycanthrope extends Moonweasel
{
	private WeaselView view;
	private InputController input;
	private Player player;
	private Client client;
	
	public Lycanthrope(boolean fullscreen)
	{
		super(fullscreen);
		
		int width = 800, height = 600;
		DisplayMode mode = null;
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();

			for (int i = 0; i < modes.length; i++) {
				if ((modes[i].getWidth() == width)
						&& (modes[i].getHeight() == height)) {
					mode = modes[i];
					break;
				}
			}
			
			if (mode == null) {
				throw new RuntimeException(String.format(
						"Unable to find target display mode width %d, height %d.",
						width, height));
			}
		} catch (LWJGLException e) {
			throw new RuntimeException("Unable to choose display mode.", e);
		}
		
		this.client = new Client();
		client.findAndConnectToServer();
		client.connectionInitializationComplete();
		while (player == null)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.player = (Player) entityManager.get(client.getMyID());
		}
		this.view = new WeaselView(mode, fullscreen, player);
		this.input = new InputController();
	}
	
	protected void destroy() {
		super.destroy();
		view.destroy();
	}

	protected void act(long next_logic_tick) 
	{
		float interpolation;
		UserCommand command = input.poll(t);
		
		if(command != null)
		{
			player.addCommand(command);
			client.sendCommandToServer(command);
		}
		
		
		List<ChangeList> changes = client.getChanges();
		
		if(changes != null)
		{
			for (ChangeList l : changes)
				ChangeCompiler.compile(l, entityManager);
			
			client.resetChanges();
		}
		

	//	player.clearCommandsBefore(t);
		
		interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) / SKIP_TICKS;
		view.render(interpolation);
	}

}
