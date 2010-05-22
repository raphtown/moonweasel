package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.RMIConfiguration.CLIENT_OBJECT_NAME;
import static org.atcs.moonweasel.networking.RMIConfiguration.RMI_PORT;
import static org.atcs.moonweasel.networking.RMIConfiguration.SERVER_OBJECT_NAME;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.atcs.moonweasel.Debug;
import org.atcs.moonweasel.Moonweasel;
import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.announcer.ServerAnnouncer;
import org.atcs.moonweasel.networking.changes.ChangeList;

/**
 * Serves as a client for the RMI connection that we are planning to use as 
 * a framework for networking. The basic connection principles still hold, 
 * but this class should serve as the base for all client-based network 
 * communication.
 * 
 * Currently has a main() method so that it can be run by itself for simple 
 * testing, but this can be changed in the future.
 * 
 * @author Maxime Serrano, Raphael Townshend
 */
public class Client extends RMIObject implements IClient
{
	private IServer server = null;
	@SuppressWarnings("unused")
	private Moonweasel m;
	
	public Client(Moonweasel m)
	{
		super(CLIENT_OBJECT_NAME);
		this.m = m;
	}

	public void findAndConnectToServer()
	{
		try
		{
			String serverHostname = getConnectionHostname();
			connectToServer(serverHostname);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void connectToServer(String serverHostName) throws RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry(serverHostName, RMI_PORT);
		server = (IServer) registry.lookup(SERVER_OBJECT_NAME);
		server.connect(getIP());
	}

	public void connectionInitializationComplete()
	{
		try {
			server.connectionInitializationComplete(getIP());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Serves to get a hostname out of the ServerAnnouncer, splitting out the 
	 * server name so that nothing but an IP or DNS is left.
	 * @return The appropriate hostname that should be connected to.
	 * @throws IOException If we fail to properly get the server list.
	 */
	private static String getConnectionHostname() throws IOException
	{
		List<String> hostnames = ServerAnnouncer.getServerList();

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
			System.out.println("Available hosts:");
			for(int i = 0; i < hostnames.size(); i++)
			{
				System.out.print((i + 1) + ") ");
				System.out.println(hostnames.get(i));
			}
			System.out.println("Which server would you like to join?");
			number = console.nextInt();
			console.nextLine();
		}
		return (String) hostnames.get(number - 1).split(" ")[0];
	}

	public IServer getServer()
	{
		return server;
	}

	public void sendCommandToServer(UserCommand command)
	{
		try {
			server.doCommand(command.getAsBitmask(), command.getMouse(), getIP(), command.getTime());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public int getMyID()
	{
		try
		{
			int id = server.getMyID(getIP());
			Debug.print("Got id: " + id);
			return id;
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	private List<Entity> entitiesToAdd = new LinkedList<Entity>();
	private List<Entity> entitiesToDelete = new LinkedList<Entity>();

	public void receiveEntities(boolean add, List<Entity> eList) throws RemoteException
	{
		if(add)
		{
			synchronized(entitiesToAdd)
			{
				entitiesToAdd.addAll(eList);
			}
		}
		else
		{
			synchronized(entitiesToDelete)
			{
				entitiesToDelete.addAll(eList);
			}
		}

	}

	private List<ChangeList> changes = new LinkedList<ChangeList>();

	public void receiveChanges(List<ChangeList> changes) throws RemoteException
	{
		synchronized(this.changes)
		{
			if (changes == null)
			{
				System.err.println("ERROR ERROR ERROR - CHANGE LIST IS NULL");
				System.exit(1);
			}

			if(this.changes.size() > 0)
				Debug.print("Got new changes before old ones processed!");
			this.changes.addAll(changes);
		}
	}

	private List<IState> IStates =  new LinkedList<IState>();

	public void receiveIStates(List<IState> IStates) throws RemoteException
	{
		synchronized(this.IStates)
		{
			if (IStates == null)
			{
				System.err.println("ERROR ERROR ERROR - CHANGE LIST IS NULL");
				System.exit(1);
			}

			if(this.IStates.size() > 0)
			{
				Debug.print("Got new IStates before old ones processed!");
				this.IStates.clear();
			}

			this.IStates.addAll(IStates);
		}
	}


	public ShipType sendShipChoice() throws RemoteException
	{
		return ShipType.SNOWFLAKE;
	}

	public void act()
	{
//		try
//		{
//			long serverTime = server.getTime();
//			 if(serverTime - m.getT() > 100)
//			 {
//				System.out.println("out of sync");
//				 m.setT(serverTime - 50);
//			 }
//		} catch (RemoteException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		EntityManager mgr = EntityManager.getEntityManager();
		synchronized(entitiesToAdd)
		{
			for (Entity e : entitiesToAdd)
			{
				Debug.print("Adding New Entity: " + e + "  with id: " + e.getID());
				mgr.add(e);
			}
			entitiesToAdd.clear();
		}
		
		synchronized(IStates)
		{
			Iterator<IState> i = IStates.iterator();
			while(i.hasNext())
			{
				IState l = i.next();
				ModelEntity me = ((ModelEntity)mgr.get(l.ownerID));
				if(me == null)
					System.err.println("Update for non-existent entity: " + l.ownerID);
				else
				{
//					System.out.println(l.time + "  " + m.getT());
//					if(l.time <= m.getT())
					{
//						System.out.println(me + " " + me.getPosition());
//						me.unpackageIState(l);
						i.remove();
					}
					
				}
			}
		}
		
		
		synchronized(entitiesToDelete)
		{
			for (Entity e : entitiesToDelete)
			{
				Debug.print("Deleting Old Entity: " + e + "  with id: " + e.getID());
				mgr.delete(e);
			}
			this.entitiesToDelete.clear();
		}
	}
}
