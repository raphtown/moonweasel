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
	
	/**
	 * The clients that have called the connect() method remotely.
	 */
	private List<Client> connectedClients = new ArrayList<Client>();

    public static void main(String[] args)
    {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try
        {
        	String name = new java.util.Scanner(System.in).nextLine();
            new ServerAnnouncer(name).start();
            registry = LocateRegistry.createRegistry(RMIConfiguration.RMI_PORT);
            Server engine = new Server();
            engine.registerSelf("Simulator");
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
     * Register yourself to the registry.
     * @param name The name to register with.
     * @throws RemoteException If there is an error while rebinding.
     * @throws AccessException If we don't have access to the registry.
     */
    private void registerSelf(String name) throws RemoteException, AccessException
    {
    	registerObject(name, this);
    }
    
    /**
	 * Connects the given client to the server. Given that we don't yet have a 
	 * reliable way of disconnecting, we should perhaps hope that nobody's going 
	 * to have their program randomly crash. 
	 * @param c The client that is being connected to the server. 
	 * @throws RemoteException If bad things happen - server goes away, that sort of thing.
	 */
    public void connect(Client c) throws RemoteException
    {
    	connectedClients.add(c);
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
}