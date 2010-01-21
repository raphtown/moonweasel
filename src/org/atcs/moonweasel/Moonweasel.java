package org.atcs.moonweasel;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.Snowflake;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.networking.Server;
import org.atcs.moonweasel.networking.ServerAnnouncer;
import org.atcs.moonweasel.physics.Physics;

public class Moonweasel {
	public static final Map<String, Class<? extends Entity>> ENTITY_MAP;

	static {
		ENTITY_MAP = new HashMap<String, Class<? extends Entity>>();
		ENTITY_MAP.put(Entity.getEntityType(Player.class), Player.class);
		ENTITY_MAP.put(Entity.getEntityType(Snowflake.class), Snowflake.class);
	}

	public static void main(String[] args) {
		new Server().start();
		Moonweasel weasel = new Moonweasel(800, 600, false);

		// weasel.seeFox();
		weasel.run();
		weasel.destroy(); // eaten

		System.exit(0);
	}

	private Physics physics;
	private WeaselView view;

	private EntityManager entityManager;

	private Moonweasel(int width, int height, boolean fullscreen) {
		this.physics = Physics.getSingleton();
		this.view = new WeaselView(width, height, fullscreen);

		this.entityManager = EntityManager.getEntityManager();
		Snowflake snowflake = this.entityManager.create("snowflake");
		snowflake.spawn();
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
		
		try {
			getConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!view.shouldQuit()) {
			loops = 0;
			while (System.currentTimeMillis() > next_logic_tick &&
					loops < MAX_FRAMESKIP) {
				entityManager.update();
				physics.update(t, SKIP_TICKS);

				t += SKIP_TICKS;
				next_logic_tick += SKIP_TICKS;
				loops++;
			}

			interpolation = (float)(System.currentTimeMillis() + SKIP_TICKS - next_logic_tick) 
				/ SKIP_TICKS;
			view.render(interpolation);
		}
	}
	 
	private void getConnection() throws IOException
	{
		List<String> hostnames = ServerAnnouncer.getServerList();
		
		System.out.println("Client started...");
		System.out.println("Available hosts:");
		for(int i = 0; i < hostnames.size(); i++)
		{
			System.out.print(i + 1 + ") ");
			System.out.println(hostnames.get(i));
		}
		System.out.println("Which server would you like to join?");
		Scanner console = new Scanner(System.in);
		int number = console.nextInt();
		while(number < 1 || number > hostnames.size())
		{
			System.out.println("Invalid server number");
			for(int i = 0; i < hostnames.size(); i++)
			{
				System.out.print(i + 1 + ") ");
				System.out.println(hostnames);
				System.out.println("Which server would you like to join?");
				number = console.nextInt();
				console.nextLine();
			}
		}

		String selection = (String) hostnames.get(number - 1);
		int port = 40001;
		new Socket(selection, port);
	}
}
