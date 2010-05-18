package org.atcs.moonweasel.networking;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Serves as the configuration file for all RMI-related classes and methods.
 * @author Maxime Serrano, Raphael Townshend
 */
public final class RMIConfiguration
{
	// just prevents initialization
	private RMIConfiguration() { }
	
	/**
	 * The port that the RMI server will use to create and connect to its registry.
	 */
	public static final int RMI_PORT = 4001;
	
	/**
	 * Whether or not to print out the debug strings.
	 */
	public static final boolean RMI_DEBUG = true;
	
	
	/**
	 * The registry that all objects will be registered to for remote access.
	 */
	public static Registry registry = null;
	
	/**
	 * The name that the server object will be saved at on the server's RMI registry.
	 */
	public static final String SERVER_OBJECT_NAME = "MoonweaselServer";
	
	/**
	 * The name that the client object will be saved at on the client's RMI registry.
	 */
	public static final String CLIENT_OBJECT_NAME = "MoonweaselClient";
	
	static
	{
		System.setSecurityManager(new SecurityManager());
		
		try
		{		
			registry = LocateRegistry.createRegistry(RMI_PORT);
		} 
		catch (RemoteException e)
		{
			System.err.println("Registry already exists! Attempting to obtain it...");
			try
			{
				registry = LocateRegistry.getRegistry(RMI_PORT);
				System.out.println("Registry obtained successfully!");
			} 
			catch (RemoteException e1)
			{
				System.err.println("Registry cannot be obtained...");
				e1.printStackTrace();
			}
		}
	}
}
