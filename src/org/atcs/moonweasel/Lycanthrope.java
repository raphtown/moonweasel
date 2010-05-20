package org.atcs.moonweasel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Client;
import org.atcs.moonweasel.sound.SimpleMidiLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Lycanthrope extends Moonweasel
{
	private WeaselView view;
	private InputController input;
	private Player player;
	private Client client;
	
	private Queue<UserCommand> commands;
	
	public Lycanthrope(boolean fullscreen)
	{
		super(fullscreen);
		SimpleMidiLoader myLoader = new SimpleMidiLoader("sf64b2.mid");
		myLoader.playMidi();
		
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
		
		this.client = new Client(this);
		client.findAndConnectToServer();
		client.connectionInitializationComplete();
		while (player == null)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.act();
			this.player = (Player) entityManager.get(client.getMyID());
		}
		this.view = new WeaselView(mode, fullscreen, player);
		this.input = new InputController();
		this.commands = new LinkedList<UserCommand>();
	}
	
	protected void destroy() {
		super.destroy();
		view.destroy();
	}
	
	protected boolean shouldQuit() {
		return view.shouldQuit();
	}

	protected void logic_act(long t, int skip_ticks) 
	{
		physics.update(t, skip_ticks);
		
		UserCommand command = input.poll(t);
		player.addCommand(command);
		commands.add(command);
	}

	protected void render_act(float interpolation) {
		view.render(interpolation);
		
		Iterator<UserCommand> iter = commands.iterator();
		while (iter.hasNext()) {
			client.sendCommandToServer(iter.next());
			iter.remove();
		}
		
		client.act();
	}
}
