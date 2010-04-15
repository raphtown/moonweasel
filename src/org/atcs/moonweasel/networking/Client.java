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
import java.util.Map;
import java.util.Scanner;

import org.atcs.moonweasel.Debug;
import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.announcer.ServerAnnouncer;
import org.atcs.moonweasel.util.State;

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
public class Client extends RMIObject implements IClient, Runnable
{
	private IServer server = null;
	
	public static void main(String args[])
	{
		new Client().findAndConnectToServer();
	}

	public Client()
	{
		super(CLIENT_OBJECT_NAME);
	}
	
	@Override
	public void run()
	{
		
		
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
		Object[] parameters = {ip};
		Protocol.sendPacket("connect", parameters, server);
	}
	
	public void connectionInitializationComplete()
	{
		try {
			server.connectionInitializationComplete(getIP());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void chooseShip()
	{
		Object[] parameters = {ShipType.SNOWFLAKE, getIP()};
		Protocol.sendPacket("chooseShip", parameters, server);
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



	@SuppressWarnings("unchecked")
	public void requestUpdateFromServer()
	{
		Object[] parameters = { getIP() };
		Map<Integer, State> sList = (Map<Integer, State>) Protocol.sendPacket("requestUpdate", parameters, server);
		EntityManager mgr = EntityManager.getEntityManager();
		
		if (sList == null)
		{
			System.out.println("ERROR ERROR ERROR - CHANGE LIST IS NULL");
			System.exit(0);
			return;
		}

		for (Integer id : sList.keySet())
		{
			ModelEntity m = mgr.get(id);
			System.out.println("Obtained model entity: " + m + " with id: " + id);
			m.setState(sList.get(id));
		}
	}

	public IServer getServer()
	{
		return server;
	}

	public String getIP()
	{
		return ip;
	}

	public void sendCommandToServer(UserCommand command)
	{
		Object[] parameters = {command.getAsBitmask(), command.getMouse(), getIP()};
		long start = System.currentTimeMillis();
		Protocol.sendPacket("doCommand", parameters, server);
		long end = System.currentTimeMillis() - start;
		Debug.print("RMI delay: " + end);
	}
	
	public int getMyID()
	{
		try
		{
			int id = server.getMyID(ip);
			System.out.println("Got id: " + id);
			return id;
		} 
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public void receiveEntities(ArrayList<Entity> eList) throws RemoteException
	{
		EntityManager mgr = EntityManager.getEntityManager();
		for (Entity e : eList)
		{
			System.out.println("Received New Entity: " + e + "  with id: " + e.getID());
			mgr.add(e);
		}
	}
	
	public void receiveUpdatedStates(Map<Integer, State> sList) throws RemoteException
	{
		EntityManager mgr = EntityManager.getEntityManager();
		if (sList == null)
		{
			System.err.println("ERROR ERROR ERROR - STATE LIST IS NULL");
			System.exit(0);
			return;
		}

		for (Integer id : sList.keySet())
		{
			ModelEntity m = mgr.get(id);
			if(m == null)
			{
				System.err.println("This id was not located: " + id);
//				System.exit(0);
//				return;
			}
			System.out.println("Received Updated State For Entity: " + id);
			m.setState(sList.get(id));
		}
	}

	@Override
	public void act()
	{
		// TODO Auto-generated method stub
		
	}
}
