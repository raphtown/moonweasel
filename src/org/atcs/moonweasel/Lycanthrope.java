package org.atcs.moonweasel;

import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Client;
import org.atcs.moonweasel.physics.Physics;

public class Lycanthrope
{
	protected WeaselView view;
	protected InputController input;
	private Player player;
	private short lastCommand = 0;
	protected Client client;
	protected long t;
	private EntityManager entityManager;
	protected Physics physics;
	private Map<String, Player> playerMap = new HashMap<String, Player>();
	
	public static void main(String args[])
	{
		Lycanthrope vervolf = new Lycanthrope(800, 600, false);

		vervolf.run();
		vervolf.destroy(); // eaten

		System.exit(0);
	}
	
	public Lycanthrope(int width, int height, boolean fullscreen)
	{
//		For Reference
//		this.client = new Client();
//		client.findAndConnectToServer();
//		int nextID = client.getNextID();
//		client.getStartingEntities();
//		client.connectionInitializationComplete();
//		client.chooseShip();
//
//		this.physics = new Physics();
//		this.entityManager = EntityManager.getEntityManager();
//		entityManager.setNextID(nextID);
//
//		player = this.entityManager.create("player");
//		player.spawn();
//		playerMap.put(client.getIP(), player);
//		Snowflake snowflake = this.entityManager.create("snowflake");
//		snowflake.setPilot(player);
//		snowflake.spawn();
//		player.setShip(snowflake);
//
//		this.view = new WeaselView(width, height, fullscreen, player);
//		this.input = new InputController(view.getWindow());
		
		this.client = new Client();
		client.findAndConnectToServer();
	
		client.connectionInitializationComplete();
		client.chooseShip();
		client.getStartingEntities();
		this.physics = new Physics();
		this.entityManager = EntityManager.getEntityManager();

		this.player = (Player) entityManager.get(client.getMyID());
		this.view = new WeaselView(width, height, fullscreen, player);
		this.input = new InputController(view.getWindow());
	}
	
	private void destroy() {
		physics.destroy();
		view.destroy();
	}

	public void run()
	{
		final int TICKS_PER_SECOND = 25;
		final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
		final int MAX_FRAMESKIP = 5;
		long next_logic_tick = System.currentTimeMillis();
		int loops;
		float interpolation;
		int curr = 0;
		int currmax = 10;
		
		while (!view.shouldQuit()) 
		{
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) {
				entityManager.update(t);
				physics.update(t, SKIP_TICKS);

				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
			}
			
			t = System.currentTimeMillis();
			UserCommand command = input.poll(t);
			player.addCommand(command);
			if (command.getAsBitmask() != lastCommand)
				client.sendCommandToServer(command);
			lastCommand = command.getAsBitmask();
			
			player.clearCommandsBefore(t);
			if ((++curr) % currmax == 0)
			{
				//view.toggleUpdating();
				//int id = view.getMe().getID();
				client.requestUpdateFromServer();
				//Player p = (Player)(entityManager.get(id));
				//view.setMe(p);
				//view.toggleUpdating();
			}
			interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) 
			/ SKIP_TICKS;
			view.render(interpolation);
		}
	}

}
