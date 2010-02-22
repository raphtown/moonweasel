package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.RMIConfiguration.*;

import java.lang.reflect.Method;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.networking.announcer.ServerAnnouncer;
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
public class Server extends ActionSource implements IServer
{
	/**
	 * The clients that have called the connect() method remotely.
	 */
	private final List<String> connectedClients = new ArrayList<String>();
	
	public static void main(String args[])
	{
		Scanner console = new Scanner(System.in);
		System.out.print("Input Server Name...");
		String name = console.nextLine();
		new Server(name);
	}

	/**
	 * Creates a new Server instance with the given name. Starts an announcer announce its presence and attempts to register itself in the RMI registry.
	 */
	public Server(final String serverName)
	{
		try
		{
			new ServerAnnouncer(serverName).start();
			registerObject(SERVER_OBJECT_NAME, this);
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
		fireActionEvent("newClient " + c);
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
		
		fireActionEvent("shipChosen " + shipType + " " + clientHostname);
	}

	/**
	 * When the client sends in a command, call this method.
	 * @param command The command(s) that have been pressed.
	 * @param c The client that is using this command.
	 */
	public void doCommand(short command, Vector mouse, final String c) throws RemoteException
	{
		if (!connectedClients.contains(c))
			throw new RemoteException("Unconnected client trying to execute command!");

		if (RMI_DEBUG)
			System.out.println("Received command " + command + " from " + c + ".");
		
		fireActionEvent("commRec " + command + " " + mouse.x + " " + mouse.y + " " + c);
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

		Range<Entity> range = EntityManager.getEntityManager().getAllOfType(Entity.class);
		List<Entity> entityList = new ArrayList<Entity>();
		while(range.hasNext())
			entityList.add(range.next());
		return entityList;
	}
	
	/**
	 * Forces all of the server's clients to request (and receive) an update.
	 */
	public void update()
	{
		for (String clientName : connectedClients)
		{
	        try
	        {
				Registry registry = LocateRegistry.getRegistry(clientName, RMI_PORT);
				((IClient)registry.lookup(CLIENT_OBJECT_NAME)).forceUpdate();
			}
			catch (AccessException e)
			{
				disconnectClient(clientName);
			}
			catch (RemoteException e)
			{
				disconnectClient(clientName);
			}
			catch (NotBoundException e)
			{
				disconnectClient(clientName);
			}
		}
	}

	@Override
	public Object sendPacket(short command, Object... parameters) throws RemoteException
	{

		try
		{
			String method = Protocol.getMethodName(command);
			int numParams = Protocol.getParameters(method).length;
			
			String[][] expectedParameters = Protocol.getParameters(method);
			
			System.out.println("----------------");
			System.out.println("Received packet!");
			System.out.println("Short: " + command);
			System.out.println("Command: " + method);
			System.out.println("Parameters:");
			System.out.println("	Number: " + numParams);
			for(int i = 0; i < numParams; i++)
			{
				System.out.println("	Parameter class: " + expectedParameters[i][1].getClass().getSimpleName() + "  Parameter name: " + expectedParameters[i][0] + "  Parameter value: " + parameters[i]);
			}
			System.out.println("----------------");
			
			Class<?> server = this.getClass();
			Class<?>[] parameterTypes = new Class[Protocol.getNumParams(method)];
			for(int i = 0; i < parameterTypes.length; i++)
			{
				parameterTypes[i] = expectedParameters[i][1].getClass();
			}
			Method m = server.getMethod(method, parameterTypes);
			m.invoke(this, parameters);
			
//			Class<?> lol = command.getClass();
//			Method[] methList = lol.getMethods();
//			methList[0].getParameterTypes(); 
//			Class<?> partypes[] = new Class[0];
//			Method m = lol.getMethod("toString", partypes);
//			Object arglist[] = new Object[0];
//			m.invoke(command, arglist);
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Disconnects the given client, telling all listeners of the event as well.
	 */
	private void disconnectClient(String clientName)
	{
		connectedClients.remove(clientName);
		fireActionEvent("discClient " + clientName);
	}
}