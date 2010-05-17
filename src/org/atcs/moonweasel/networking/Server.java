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
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.atcs.moonweasel.Debug;
import org.atcs.moonweasel.Moonweasel;
import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.announcer.ServerAnnouncer;
import org.atcs.moonweasel.networking.changes.ChangeList;
import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.util.State;
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
	public PriorityQueue<UserCommand> commandList = new PriorityQueue<UserCommand>();
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

//		Debug.print("Received command " + command + " from " + c + ".");

		float mouseX = mouse.x;
		float mouseY = mouse.y;
		Player plr = playerMap.get(c);
		UserCommand ucommand = new UserCommand(plr);
		ucommand.setKeysAsBitmask(command);
		ucommand.setMouse(mouseX, mouseY);
		ucommand.setTime(m.getT() - 5);
		commandList.add(ucommand);
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

		playerMap.remove(clientName);
		s.destroy();
		plr.destroy();
	}

	public void sendCurrentEntitiesToAll()
	{
		Debug.print("Sending out entities.");
		Set<String> temp = getSafeConnectedClientsSet();
		for (String clientName : temp)
			sendCurrentEntitiesToClient(clientName);
	}

	public void sendCurrentEntitiesToClient(String clientName)
	{
		Debug.print("Sending out entities to " + clientName);
		ArrayList<Entity> eList = new ArrayList<Entity>();
		Range<Entity> range = EntityManager.getEntityManager().getAllOfType(Entity.class);
		synchronized (EntityManager.getEntityManager())
		{
			while(range.hasNext())
			{
				Entity e = range.next();
				if(e.sentToAll)
				{
					eList.add(e);
				}
			}
		}

		sendEntities(true, eList, clientName);
	}

	public void sendNewEntitiesToAll()
	{
		Debug.print("Sending out new entities to all.");
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
					eList.add(e);
				}
			}
		}
		
		Set<String> temp = getSafeConnectedClientsSet();

		for (String clientName : temp)
			sendEntities(true, eList, clientName);

	}
	
	public void sendDeletedEntitiesToAll(ArrayList<Entity> eList)
	{
		Debug.print("Sending out deleted entities to all.");

		Set<String> temp = getSafeConnectedClientsSet();
		for (String clientName : temp)
			sendEntities(false, eList, clientName);
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
			e.printStackTrace();
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

		Set<String> temp = getSafeConnectedClientsSet();

		for (String clientName : temp)
		{
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
	
	public IState generateIState(ModelEntity e)
	{
		State s = e.getState();
		return new IState(s.position,s.momentum,s.orientation,s.angularMomentum,e.getID());
	}
	
	public void sendIStatesToAll()
	{
		Range<ModelEntity> range = EntityManager.getEntityManager().getAllOfType(ModelEntity.class);
		List<IState> list = new LinkedList<IState>();
		synchronized (EntityManager.getEntityManager())
		{
			while(range.hasNext())
			{
				ModelEntity e = range.next();
				list.add(this.generateIState(e));
			}
		}

		Set<String> temp = getSafeConnectedClientsSet();

		for (String clientName : temp)
		{
			IClient c = connectedClients.get(clientName);
			try
			{
				c.receiveIStates(list);
			} 
			catch (RemoteException e)
			{ 
				e.printStackTrace();
				System.err.println("Invalid client in client list...");
				disconnectClient(clientName);
			}
		}
	}

	public void connectionInitializationComplete(String c)
	{
		Debug.print("Completed connection initialization for " + c);
		connectedClients.put(c, connectingClients.get(c));
		connectingClients.remove(c);
		newlyConnectedClients.add(c);
	}


	public Integer getMyID(String ip) throws RemoteException
	{
		while (playerMap.get(ip) == null)
		{
			Debug.print("ID Request from: " + ip);
			System.out.println(playerMap.get(ip));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return playerMap.get(ip).getID();
	}
	


	private void setupClient(String clientName)
	{
		Debug.print("Setting up client " + clientName);

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
		System.out.println(playerMap);
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

		while(commandList.size() != 0)
		{
			UserCommand u = commandList.poll();
			u.player.addCommand(u);
		}
		
		newlyConnectedClients.clear();
//		sendIStatesToAll();
		sendChangesToAll();
	}
	
	private Set<String> getSafeConnectedClientsSet()
	{
		Set<String> temp = new HashSet<String>();
		for(String clientName : connectedClients.keySet())
			temp.add(clientName);

		return temp;
	}
}