package org.atcs.moonweasel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Client;
import org.atcs.moonweasel.networking.Server;
import org.atcs.moonweasel.physics.Physics;

public class Moonweasel implements ActionListener
{
	private static final Map<String, Class<? extends Entity>> ENTITY_MAP;
	private static final Map<Integer, Class<? extends Entity>> ENTITY_TYPE_ID_MAP;

	private static void addEntityClass(Class<? extends Entity> clazz) {
		ENTITY_MAP.put(Entity.getEntityType(clazz), clazz);
		ENTITY_TYPE_ID_MAP.put(clazz.toString().hashCode(), clazz);
	}
	
	public static int getTypeID(Class<? extends Entity> clazz) {
		return clazz.toString().hashCode();
	}
	
	public static Class<? extends Entity> getEntityClassByID(int id) {
		return ENTITY_TYPE_ID_MAP.get(id);
	}
	
	public static Class<? extends Entity> getEntityClassByName(String name) {
		return ENTITY_MAP.get(name);
	}
	
	static {
		ENTITY_MAP = new TreeMap<String, Class<? extends Entity>>();
		ENTITY_TYPE_ID_MAP = new TreeMap<Integer, Class<? extends Entity>>();
		addEntityClass(Player.class);
		addEntityClass(Snowflake.class);
		addEntityClass(Explosion.class);
	}

	public static void main(String[] args) {
		Moonweasel weasel = new Artemis(800, 600, false);

		weasel.run();
		weasel.destroy(); // eaten

		System.exit(0);
	}

	protected Physics physics;
	protected WeaselView view;
	protected InputController input;
	protected Client client;
	protected Server server;

	private EntityManager entityManager;
	private Player player;
	private Map<String, Player> playerMap = new HashMap<String, Player>();
	private Map<Player, Long> playerCommandMap = new HashMap<Player, Long>();
	protected long t;

	protected Moonweasel(int width, int height, boolean fullscreen) {
		this.entityManager = EntityManager.getEntityManager();
		
		System.out.print("Enter server name: ");
		String serverName = new java.util.Scanner(System.in).nextLine();
		server = new Server(serverName);
		server.addActionListener(this);
		this.client = new Client();
		client.chooseShip();
		player = this.entityManager.create("player");
		player.spawn();
		playerMap.put(client.getIP(), player);
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

		t = 0;
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
		System.out.println("Action performed: " + actionCommand);
		String[] parts = actionCommand.split(" ");
		if (parts.length > 0)
		{
			if (parts[0].equals("newClient"))
			{
				String clientHostname = parts[1];
				
				Player plr = this.entityManager.create("player");
				plr.spawn();
				
				playerMap.put(clientHostname, plr);
			}
			else if (parts[0].equals("shipChosen"))
			{
				byte shipChoice = Byte.parseByte(parts[1]);
				String clientHostname = parts[2];
				if (!clientHostname.equals(client.getIP()))
				{
					String shipTypeName = ShipType.getFromType(shipChoice).typeName;
					Player plr = playerMap.get(clientHostname);
					Ship ship = this.entityManager.create(shipTypeName);
					ship.setPilot(plr);
					ship.spawn();
					plr.setShip(ship);
				}
			}
			else if (parts[0].equals("commRec"))
			{
				short command = Short.parseShort(parts[1]);
				float mouseX = Float.parseFloat(parts[2]);
				float mouseY = Float.parseFloat(parts[3]);
				String clientHostname = parts[4];
				if (!clientHostname.equals(client.getIP()))
				{
					Player plr = playerMap.get(clientHostname);
					if (playerCommandMap.get(plr) != null)
						if (playerCommandMap.get(plr).compareTo(new Long(command)) == 0)
							return;
					UserCommand ucommand = new UserCommand();
					ucommand.setKeysAsBitmask(command);
					ucommand.setMouse(mouseX, mouseY);
					ucommand.setTime(t);
					plr.addCommand(ucommand);
					playerCommandMap.put(plr, new Long(command));
				}
			}
			else if (parts[0].equals("discClient"))
			{
				String hostname = parts[1];
				Player plr = playerMap.get(hostname);
				this.entityManager.delete(plr);
				playerMap.remove(hostname);
				plr.getShip().destroy();
				plr.destroy();
				((Server)(e.getSource())).update();
			}
		}
	}
}
