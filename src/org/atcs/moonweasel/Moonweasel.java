package org.atcs.moonweasel;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.particles.Explosion;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Snowflake;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Client;
import org.atcs.moonweasel.networking.Server;
import org.atcs.moonweasel.networking.actions.ServerActionListener;
import org.atcs.moonweasel.physics.Physics;

public class Moonweasel
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
	protected long t;

	protected Moonweasel(int width, int height, boolean fullscreen) {
		this.entityManager = EntityManager.getEntityManager();

		System.out.print("Enter server name: ");
		String serverName = new java.util.Scanner(System.in).nextLine();
		server = new Server(serverName);
		this.client = new Client();
		server.addActionListener(new ServerActionListener(entityManager, playerMap, client, this));
		client.findAndConnectToServer();
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
		long time = System.currentTimeMillis();
		long delta = 0;
		long temp = 0;
		float interpolation;
		short lastCommand = 0;
		while (!view.shouldQuit()) {
			time = System.currentTimeMillis();
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) {
				entityManager.update(t);
				physics.update(t, SKIP_TICKS);
				temp = System.currentTimeMillis();
				delta = temp - time;
				time = temp;
				if(delta > 10)
				{
					System.out.println("Mini1: " + delta);
				}
				UserCommand command = input.poll(t);
				player.addCommand(command);
				temp = System.currentTimeMillis();
				delta = temp - time;
				time = temp;
				if(delta > 10)
				{
					System.out.println("Mini2: " + delta);
				}
				if (command.getAsBitmask() != lastCommand)
					client.sendCommandToServer(command);
				lastCommand = command.getAsBitmask();
				temp = System.currentTimeMillis();
				delta = temp - time;
				time = temp;
				if(delta > 10)
				{
					System.out.println("Mini3: " + delta);
				}
				player.clearCommandsBefore(t);

				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
			}
			time = System.currentTimeMillis();
			interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) 
			/ SKIP_TICKS;
			view.render(interpolation);
			temp = System.currentTimeMillis();
			delta = temp - time;
			time = temp;
			if(delta > 20)
			{
				System.out.print("Big: " + delta);
				System.out.println();
			}

		}
	}

	public long getT()
	{
		return t;
	}
}
