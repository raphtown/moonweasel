package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.RMIConfiguration.CLIENT_OBJECT_NAME;
import static org.atcs.moonweasel.networking.RMIConfiguration.RMI_PORT;
import static org.atcs.moonweasel.networking.RMIConfiguration.SERVER_OBJECT_NAME;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.announcer.ServerAnnouncer;
import org.atcs.moonweasel.networking.changes.ChangeCompiler;
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

	public Client()
	{
		super(CLIENT_OBJECT_NAME);
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
			server.doCommand(command.getAsBitmask(), command.getMouse(), getIP());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public int getMyID()
	{
		try
		{
			int id = server.getMyID(getIP());
			System.out.println("Got id: " + id);
			return id;
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public void receiveEntities(boolean add, ArrayList<Entity> eList) throws RemoteException
	{
		EntityManager mgr = EntityManager.getEntityManager();
		for (Entity e : eList)
		{
			System.out.println("Received New Entity: " + e + "  with id: " + e.getID());
			if(add)
			{
				mgr.add(e);
			}
			else
			{
				mgr.delete(e);
			}
		}
	}
	
	private List<ChangeList> changes = null;
	
	public void receiveChanges(List<ChangeList> changes) throws RemoteException
	{
		EntityManager mgr = EntityManager.getEntityManager();
		if (changes == null)
		{
			System.err.println("ERROR ERROR ERROR - CHANGE LIST IS NULL");
			System.exit(1);
		}
		
		synchronized(changes)
		{
			this.changes = changes;
		}
		
	}
	
	public List<ChangeList> getChanges()
	{
		if(changes != null)
		{
			synchronized(changes)
			{
				return changes;
			}
		}
		
		return null;
	}
	
	public void resetChanges()
	{
		changes = null;
	}

	public ShipType sendShipChoice() throws RemoteException
	{
		return ShipType.SNOWFLAKE;
	}
}
