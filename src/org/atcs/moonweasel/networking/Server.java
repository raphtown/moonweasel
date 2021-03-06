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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

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
	public PriorityBlockingQueue<UserCommand> commandList = new PriorityBlockingQueue<UserCommand>();
	@SuppressWarnings("unused")
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

		float mouseX = mouse.x;
		float mouseY = mouse.y;
		Player plr = playerMap.get(c);
		UserCommand ucommand = new UserCommand(plr);
		ucommand.setKeysAsBitmask(command);
		ucommand.setMouse(mouseX, mouseY);
//		ucommand.setTime(m.getT() - 5);
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
		Debug.print("Disconnecting client: " + clientName);
		connectedClients.remove(clientName);
		Player plr = playerMap.get(clientName);
		playerMap.remove(clientName);
		if(plr != null)
		{
			plr.destroy();
		}
		
	}

	public void sendCurrentEntitiesToAll()
	{
		Debug.print("Sending out entities.");
		Set<String> temp = getSafeConnectedClientsSet();
		for (String clientName : temp)
			sendCurrentEntitiesToClient(clientName);
	}

	public void sendCurrentEntitiesToClient(final String clientName)
	{
		Debug.print("Sending out current entities to " + clientName);
		ArrayList<Entity> eList = new ArrayList<Entity>();
		Range<Entity> range = EntityManager.getEntityManager().getAllOfType(Entity.class);
		synchronized (EntityManager.getEntityManager())
		{
			while(range.hasNext())
			{
				Entity e = range.next();
				if(e.sentToAll)
				{
					Debug.print("Sending current entity: " + e + " with id: " + e.getID() + " to client: " + clientName);
					eList.add(e);
				}
			}
		}


		final Server s = this;
		final List<Entity> fList = eList;
		Thread thread = new Thread()        
		{                 

			@Override                
			public void run()                 
			{  
				s.sendEntities(true, fList, clientName);
			}            
		}; 
		thread.start();
	}

	public void sendNewEntitiesToAll()
	{
		List<Entity> eList = new ArrayList<Entity>();
		Range<Entity> range = EntityManager.getEntityManager().getAllOfType(Entity.class);

		while(range.hasNext())
		{
			Entity e = range.next();
			if(!e.sentToAll)
			{
				Debug.print("Sending out new entity to all: " + e);
				e.sentToAll = true;
				eList.add(e);
			}
		}

		Set<String> temp = getSafeConnectedClientsSet();

		for (final String clientName : temp)
		{
			final Server s = this;
			final List<Entity> fList = eList;
			Thread thread = new Thread()        
			{                 

				@Override                
				public void run()                 
				{  
					s.sendEntities(true, fList, clientName);
				}            
			}; 
			thread.start();
		}


	}

	public void sendDeletedEntitiesToAll(ArrayList<Entity> eList)
	{
		;

		Set<String> temp = getSafeConnectedClientsSet();
		for (String clientName : temp)
		{
			if(eList.size() != 0)
			{
				Debug.print("Sending out deleted entities to all:  " + eList);
			}

			sendEntities(false, eList, clientName);
		}

	}

	private void sendEntities(boolean add, List<Entity> eList, String clientName)
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

		Set<String> temp = getSafeConnectedClientsSet();

		for (String clientName : temp)
		{
			final IClient c = connectedClients.get(clientName);
			final List<ChangeList> fList = list;
			Thread thread = new Thread()        
			{                 

				@Override                
				public void run()                 
				{  
					try
					{
						c.receiveChanges(fList);
					} catch (RemoteException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}            
			}; 
			thread.start();
		}
	}

	public void sendIStatesToAll()
	{
		Range<ModelEntity> range = EntityManager.getEntityManager().getAllOfType(ModelEntity.class);
		List<IState> list = new LinkedList<IState>();
		while(range.hasNext())
		{
			ModelEntity e = range.next();
			IState is = e.packageIState();
			//is.time = m.getT();
			list.add(is);
		}

		Set<String> temp = getSafeConnectedClientsSet();

		for (String clientName : temp)
		{
			final IClient c = connectedClients.get(clientName);
			final List<IState> fList = list;
			Thread thread = new Thread()        
			{                 

				@Override                
				public void run()                 
				{  
					try
					{
						c.receiveIStates(fList);
					} catch (RemoteException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}            
			}; 
			thread.start();
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
		Debug.print("ID Request from: " + ip);
		while (playerMap.get(ip) == null)
		{
			Debug.print("No ID found for: " + ip);
			System.out.println(playerMap.get(ip));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
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
		System.out.println("Players: " + playerMap);
		plr.spawn();

		Ship ship = mgr.create(shipType.typeName);
		ship.setPilot(plr);
		ship.spawn();
		plr.setShip(ship);
		sendCurrentEntitiesToClient(clientName);
		sendNewEntitiesToAll();
	}

	private final int TICKS_PER_ISTATE_UPDATE = 5;
	private int IStateUpdateCount = 0;

	public void act()
	{

		EntityManager mgr = EntityManager.getEntityManager();
		for(String clientName : newlyConnectedClients)
		{
			Debug.print("Performing setup...");
			setupClient(clientName);
		}

		newlyConnectedClients.clear();

		while(commandList.size() != 0)
		{
			UserCommand u = commandList.poll();
			u.player.addCommand(u);
		}

		if(IStateUpdateCount++ >= TICKS_PER_ISTATE_UPDATE)
		{   
			sendIStatesToAll(); 
			IStateUpdateCount = 0;
		}
		sendNewEntitiesToAll();
		sendDeletedEntitiesToAll(mgr.deletedEntities);
		mgr.deletedEntities.clear();


	}

	private Set<String> getSafeConnectedClientsSet()
	{
		Set<String> temp = new HashSet<String>();
		for(String clientName : connectedClients.keySet())
			temp.add(clientName);

		return temp;
	}

	public long getTime() throws RemoteException
	{
		return 0;
//		return m.getT();
	}

}