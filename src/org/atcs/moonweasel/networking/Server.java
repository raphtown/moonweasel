package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.RMIConfiguration.CLIENT_OBJECT_NAME;
import static org.atcs.moonweasel.networking.RMIConfiguration.RMI_PORT;
import static org.atcs.moonweasel.networking.RMIConfiguration.SERVER_OBJECT_NAME;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.atcs.moonweasel.Debug;
import org.atcs.moonweasel.Moonweasel;
import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.announcer.ServerAnnouncer;
import org.atcs.moonweasel.networking.changes.ChangeList;
import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.util.Vector;

/**
 * Serves as the Remote Method Invocation implementation of server software. 
 * Registers itself to the RMI registry on the appropriate port, making certain 
 * of its methods available to the client for calling.
 * 
 * In theory the client should not ever have a copy of this code, but in practice 
 * this is impractical and should not be followed. The client only needs to know 
 * about IServer, not the Server itself, as the class data for this class can 
 * be downloaded at runtime via RMI's capabilities for such a thing.
 * 
 * @author Maxime Serrano, Raphael Townshend
 */
public class Server extends RMIObject implements IServer
{
	/**
	 * The clients that have called the connect() method remotely.
	 */
	private Map<String, IClient> connectedClients = new HashMap<String, IClient>();
	public Map<String, Player> playerMap = new HashMap<String, Player>();
	private ArrayList<String> newlyConnectedClients = new ArrayList<String>();
	private Map<String, IClient> connectingClients = new HashMap<String, IClient>();
	private final Map<Player, Long> playerCommandMap = new HashMap<Player, Long>();
	private Moonweasel m;

	/**
	 * Creates a new Server instance with the given name. Starts an announcer announce its presence and attempts to register itself in the RMI registry.
	 */
	public Server(final String serverName, Moonweasel m)
	{
		super(SERVER_OBJECT_NAME);
		this.m = m;

		try
		{
			new ServerAnnouncer(serverName).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}



	/**
	 * Connects the given client to the server. Given that we don't yet have a 
	 * reliable way of disconnecting, we should perhaps hope that nobody's going 
	 * to have their program randomly crash. 
	 * @param c The client that is being connected to the server. 
	 * @throws RemoteException If bad things happen - server goes away, that sort of thing.
	 */
	public void connect(final String clientName) throws RemoteException
	{
		try
		{
			Registry registry = LocateRegistry.getRegistry(clientName, RMI_PORT);
			IClient	client = (IClient) registry.lookup(CLIENT_OBJECT_NAME);
			connectingClients.put(clientName, client);
			Debug.print("Client " + clientName + " connected!");
		} 
		catch (NotBoundException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * When the client sends in a command, call this method.
	 * @param command The command(s) that have been pressed.
	 * @param c The client that is using this command.
	 */
	public void doCommand(short command, Vector mouse, final String c) throws RemoteException
	{
		if (!connectedClients.keySet().contains(c))
			throw new RemoteException("Unconnected client trying to execute command!");

		Debug.print("Received command " + command + " from " + c + ".");
		
		Debug.print("Artemis to Lycanthrope ... we have lift-off");
		float mouseX = mouse.x;
		float mouseY = mouse.y;
		Player plr = playerMap.get(c);
		if (playerCommandMap.get(plr) != null)
			if (playerCommandMap.get(plr).compareTo(new Long(command)) == 0)
				return;
		System.out.println("Command received from client: " + c + "  command: " + command  + "Player: " + plr);
		UserCommand ucommand = new UserCommand();
		ucommand.setKeysAsBitmask(command);
		ucommand.setMouse(mouseX, mouseY);
		ucommand.setTime(m.getT() - 5);
		plr.addCommand(ucommand);
		System.out.println("Wow: " + plr.getID() + "   " + plr.getCommandsBefore(m.getT()));
		playerCommandMap.put(plr, new Long(command));
	}

	/**
	 * Gets an updated list of Entities.
	 * @param c The client that is asking for an update.
	 * @return A list of Entities.
	 */
	public void requestUpdate(final String c) throws RemoteException
	{
		sendCurrentEntitiesToClient(c);
	}

	/**
	 * Disconnects the given client, telling all listeners of the event as well.
	 */
	private void disconnectClient(String clientName)
	{
		System.err.println("Disconnecting client: " + clientName);
		connectedClients.remove(clientName);
		Player plr = playerMap.get(clientName);
		Ship s = plr.getShip();
		ArrayList<Entity> toDelete = new ArrayList<Entity>();
		toDelete.add(plr);
		toDelete.add(s);
		this.sendDeletedEntitiesToAll(toDelete);

		EntityManager.getEntityManager().delete(plr);
		EntityManager.getEntityManager().delete(s);
		playerMap.remove(clientName);

		s.destroy();
		plr.destroy();
	}

	public void sendCurrentEntitiesToAll()
	{
		Set<String> temp = new HashSet<String>();
		for(String clientName : connectedClients.keySet())
		{
			temp.add(clientName);
		}
		Iterator<String> i = temp.iterator();
		while(i.hasNext())
		{
			String clientName = i.next();
			sendCurrentEntitiesToClient(clientName);
		}
	}

	public void sendCurrentEntitiesToClient(String clientName)
	{
		System.out.println("In Send All Current Entities To Clinet...");
		ArrayList<Entity> eList = new ArrayList<Entity>();
		Range<Entity> range = EntityManager.getEntityManager().getAllOfType(Entity.class);
		synchronized (EntityManager.getEntityManager())
		{
			while(range.hasNext())
			{
				Entity e = range.next();
				if(e.sentToAll)
				{
					System.out.println("Sending object: " + e.getID());
					eList.add(e);
				}
			}
		}

		sendEntities(true, eList, clientName);

		System.out.println("Finished send all entities to Client: " + clientName);
	}

	public void sendNewEntitiesToAll()
	{
		System.out.println("In Send New Entities To All...");
		ArrayList<Entity> eList = new ArrayList<Entity>();
		Range<Entity> range = EntityManager.getEntityManager().getAllOfType(Entity.class);
		synchronized (EntityManager.getEntityManager())
		{
			while(range.hasNext())
			{
				Entity e = range.next();
				if(!e.sentToAll)
				{
					e.sentToAll = true;
					System.out.println("Sending new object: " + e.getID());
					eList.add(e);
				}
			}
		}
		
		Set<String> temp = new HashSet<String>();
		for(String clientName : connectedClients.keySet())
		{
			temp.add(clientName);
		}
		Iterator<String> i = temp.iterator();
		while(i.hasNext())
		{
			String clientName = i.next();
			sendEntities(true, eList, clientName);
		}

		System.out.println("Finished send new entities...");
	}
	
	public void sendDeletedEntitiesToAll(ArrayList<Entity> eList)
	{
		Set<String> temp = new HashSet<String>();
		for(String clientName : connectedClients.keySet())
		{
			temp.add(clientName);
		}
		Iterator<String> i = temp.iterator();
		while(i.hasNext())
		{
			String clientName = i.next();
			sendEntities(false, eList, clientName);
		}
	}
	
	private void sendEntities(boolean add, ArrayList<Entity> eList, String clientName)
	{
		IClient c = connectedClients.get(clientName);
		try
		{
			c.receiveEntities(add, eList);
		}
		catch (Exception e)
		{
			System.err.println("Invalid client in client list...");
			disconnectClient(clientName);
		}
	}

	public void sendChangesToAll()
	{
		Range<Entity> range = EntityManager.getEntityManager().getAllOfType(Entity.class);
		List<ChangeList> list = new LinkedList<ChangeList>();
		synchronized (EntityManager.getEntityManager())
		{
			while(range.hasNext())
			{
				Entity e = range.next();
				if(e.hasChangedForAll())
				{
					list.add(e.getRecentChanges());
					e.sent();
				}
			}
		}
		Set<String> temp = new HashSet<String>();
		for(String clientName : connectedClients.keySet())
		{
			temp.add(clientName);
		}
		Iterator<String> i = temp.iterator();
		while(i.hasNext())
		{
			String clientName = i.next();
			IClient c = connectedClients.get(clientName);
			try
			{
				c.receiveChanges(list);
			} 
			catch (RemoteException e)
			{ 
				System.err.println("Invalid client in client list...");
				disconnectClient(clientName);
			}
		}
	}

	public void connectionInitializationComplete(String c)
	{
		connectedClients.put(c, connectingClients.get(c));
		connectingClients.remove(c);
		newlyConnectedClients.add(c);
	}


	public Integer getMyID(String ip) throws RemoteException
	{
		return playerMap.get(ip).getID();
	}

	private void setupClient(String clientName)
	{
		EntityManager mgr = EntityManager.getEntityManager();
		IClient client = connectedClients.get(clientName);
		ShipType shipType;
		try
		{
			shipType = client.sendShipChoice();
		} 
		catch (RemoteException e)
		{
			System.err.println("Client is not sending ship choice!  Aborting client setup...");
			disconnectClient(clientName);
			e.printStackTrace();
			return;
		}
		Player plr = mgr.create("player");
		playerMap.put(clientName, plr);
		plr.spawn();

		Ship ship = mgr.create(shipType.typeName);
		ship.setPilot(plr);
		ship.spawn();
		plr.setShip(ship);
		sendCurrentEntitiesToClient(clientName);
		sendNewEntitiesToAll();
	}

	public void act()
	{
		for(String clientName : newlyConnectedClients)
		{
			System.out.println("Performing setup...");
			setupClient(clientName);
		}
		newlyConnectedClients.clear();
		sendChangesToAll();
	}
}