package org.atcs.moonweasel.rmi;

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
	
	static
	{
		System.setSecurityManager(new SecurityManager());
		
		try
		{
			if (LocateRegistry.getRegistry(RMI_PORT) == null)
				registry = LocateRegistry.createRegistry(RMI_PORT);
			else
				registry = LocateRegistry.getRegistry(RMI_PORT);
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
}
