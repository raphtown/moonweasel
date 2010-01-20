package org.atcs.moonweasel.rmi;

import static org.atcs.moonweasel.rmi.RMIConfiguration.*;

import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.atcs.moonweasel.physics.Physics;
import org.atcs.moonweasel.rmi.announcer.ServerAnnouncer;

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
public class Server implements IServer
{
	/**
	 * The registry that all objects will be registered to for remote access.
	 */
	private static Registry registry = null;
	
	
	static
	{
		System.setSecurityManager(new SecurityManager());
		
		try
		{
			registry = LocateRegistry.createRegistry(RMIConfiguration.RMI_PORT);
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * The clients that have called the connect() method remotely.
	 */
	private List<String> connectedClients = new ArrayList<String>();
	
	public static void main(String args[])
	{
		new Server("lol");
	}

	public Server(String serverName)
	{
		try
		{
			new ServerAnnouncer(serverName).start();
			registerObject("Simulator", this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Register an object to the registry.
	 * @param name The name to register with.
	 * @param object The object to bind.
	 * @throws RemoteException If there is an error while rebinding.
	 * @throws AccessException If we don't have access to the registry.
	 */
	private static void registerObject(String name, Remote object) throws RemoteException, AccessException
	{
		Remote stub = UnicastRemoteObject.exportObject(object, 0);
		registry.rebind(name, stub);
		if (RMI_DEBUG)
			System.out.println(name + " bound");
	}
	
	/**
	 * Connects the given client to the server. Given that we don't yet have a 
	 * reliable way of disconnecting, we should perhaps hope that nobody's going 
	 * to have their program randomly crash. 
	 * @param c The client that is being connected to the server. 
	 * @throws RemoteException If bad things happen - server goes away, that sort of thing.
	 */
	public void connect(String c) throws RemoteException
	{
		connectedClients.add(c);
		System.out.println("Client connected!");
	}

	/**
	 * Used for nothing but testing purposes.
	 * @return 0.
	 * @throws RemoteException If the server goes away or some other part of RMI explodes.
	 */
	public int doStuff() throws RemoteException
	{
		return 0;
	}

	/**
	 * When the client sends in a command, call this method.
	 * @param command The command(s) that have been pressed.
	 * @param c The client that is using this command.
	 * @return Whether or not the client is allowed to call this method.
	 */
	public boolean doCommand(short command, String c) throws RemoteException
	{
		// TODO send command to physics engine
		Physics physics = null; // getPhysics();
		physics.update(0, 0);
		
		return connectedClients.contains(c);
	}
}