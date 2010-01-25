package org.atcs.moonweasel.rmi;

import static org.atcs.moonweasel.rmi.RMIConfiguration.RMI_DEBUG;
import static org.atcs.moonweasel.rmi.RMIConfiguration.registry;

import java.awt.event.ActionEvent;
import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.atcs.moonweasel.entities.Entity;
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
public class Server extends ActionSource implements IServer
{
	/**
	 * The clients that have called the connect() method remotely.
	 */
	private final List<String> connectedClients = new ArrayList<String>();
	
	public static void main(String args[])
	{
		new Server("Moonweasel Server");
	}

	public Server(final String serverName)
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
	private static void registerObject(final String name, final Remote object) throws RemoteException, AccessException
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
	public void connect(final String c) throws RemoteException
	{
		connectedClients.add(c);
		if (RMI_DEBUG)
			System.out.println("Client " + c + " connected!");
		this.fireActionEvent("newClient " + c);
	}
	
	/**
	 * The given client chooses a ship type, which then spawns.
	 * @param clientHostname The client that is being connected to the server. 
	 * @param shipType The type of ship that the client has chosen.
	 * @throws RemoteException If bad things happen - server goes away, that sort of thing.
	 */
	public void chooseShip(final String clientHostname, final byte shipType) throws RemoteException
	{
		if(!connectedClients.contains(clientHostname))
			throw new RemoteException("Unconnected client trying to choose ship!");
		
		if (RMI_DEBUG)
			System.out.println("Received ship choice " + shipType + " from " + clientHostname + ".");
		
		// TODO handle the choice itself
	}

	/**
	 * When the client sends in a command, call this method.
	 * @param command The command(s) that have been pressed.
	 * @param c The client that is using this command.
	 */
	public void doCommand(short command, final String c) throws RemoteException
	{
		if (!connectedClients.contains(c))
			throw new RemoteException("Unconnected client trying to execute command!");

		if (RMI_DEBUG)
			System.out.println("Received command " + command + " from " + c + ".");

		// TODO handle the input itself
	}
	
	/**
	 * Gets an updated list of Entities.
	 * @param c The client that is asking for an update.
	 * @return A list of Entities.
	 */
	public List<Entity> requestUpdate(final String c) throws RemoteException
	{
		if (!connectedClients.contains(c))
			throw new RemoteException("Unconnected client trying to get an update!");

		// TODO build a list of entities and prepare it to be sent to the client
		return null;
	}
}