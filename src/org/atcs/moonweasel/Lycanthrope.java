package org.atcs.moonweasel;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Client;
import org.lwjgl.opengl.DisplayMode;

public class Lycanthrope extends Moonweasel
{
	private WeaselView view;
	private InputController input;
	private Player player;
	private Client client;
	
	public Lycanthrope(DisplayMode mode, boolean fullscreen)
	{
		super(mode, fullscreen);
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
		
		player.addCommand(command);
		client.sendCommandToServer(command);

	//	player.clearCommandsBefore(t);
		
		interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) / SKIP_TICKS;
		view.render(interpolation);
	}

}
