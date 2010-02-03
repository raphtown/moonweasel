package org.atcs.moonweasel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Client;
import org.atcs.moonweasel.networking.Server;
import org.atcs.moonweasel.physics.Physics;

public class Moonweasel implements ActionListener
{
	public static final Map<String, Class<? extends Entity>> ENTITY_MAP;

	static {
		ENTITY_MAP = new HashMap<String, Class<? extends Entity>>();
		ENTITY_MAP.put(Entity.getEntityType(Player.class), Player.class);
		ENTITY_MAP.put(Entity.getEntityType(Snowflake.class), Snowflake.class);
	}

	public static void main(String[] args) {
		Moonweasel weasel = new Artemis(800, 600, false);

		// weasel.seeFox();
		weasel.run();
		weasel.destroy(); // eaten

		System.exit(0);
	}

	protected Physics physics;
	protected WeaselView view;
	protected InputController input;
	protected Client client;

	private EntityManager entityManager;
	private Player player;

	protected Moonweasel(int width, int height, boolean fullscreen) {
		this.entityManager = EntityManager.getEntityManager();
		
		new Server("MoonweaselServer").addActionListener(this);
		this.client = new Client();
		player = this.entityManager.create("player");
		player.spawn();
		this.view = new WeaselView(width, height, fullscreen, player);

		Snowflake snowflake = this.entityManager.create("snowflake");
		snowflake.setPilot(player);
		snowflake.spawn();
		player.setShip(snowflake);
		
		this.physics = new Physics();
		this.input = new InputController(view.getWindow());
	}

	private void destroy() {
		physics.destroy();
		view.destroy();
	}
	
	private void run() {
		final int TICKS_PER_SECOND = 25;
		final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
		final int MAX_FRAMESKIP = 5;

		long t = 0;
		long next_logic_tick = System.currentTimeMillis();
		int loops;
		float interpolation;
		short lastCommand = 0;
		while (!view.shouldQuit()) {
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) {
				entityManager.update();
				physics.update(t, SKIP_TICKS);
				UserCommand command = input.poll(t);
				player.addCommand(command);
				if (command.getAsBitmask() != lastCommand)
					client.sendCommandToServer(command);
				lastCommand = command.getAsBitmask();
				player.clearCommandsBefore(t);

				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
			}

			interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) 
				/ SKIP_TICKS;
			view.render(interpolation);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String actionCommand = e.getActionCommand();
		String[] parts = actionCommand.split(" ");
		if (parts.length > 0)
		{
			if (parts[0].equals("newClient"))
			{
				// TODO handle new client connecting
				String clientHostname = parts[1];
			}
			else if (parts[0].equals("shipChosen"))
			{
				// TODO handle ship choice
				short shipChoice = Short.parseShort(parts[1]);
				String clientHostname = parts[2];
			}
			else if (parts[0].equals("commRec"))
			{
				// TODO handle command receipt
				short command = Short.parseShort(parts[1]);
				String clientHostname = parts[2];
			}
			else if (parts[0].equals("discClient"))
			{
				// TODO handle client disconnect
				String hostname = parts[1];
			}
		}
	}
}
