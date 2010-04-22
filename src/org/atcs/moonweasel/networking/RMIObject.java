package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.RMIConfiguration.registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.atcs.moonweasel.Debug;

public abstract class RMIObject implements Remote
{
	private String ip;
	
	protected RMIObject(String objectName)
	{
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		registerObject(objectName, this);
	}

	/** 	
	 * Register an object to the registry.	 	
	 * @param name The name to register with.	
	 * @param object The object to bind.	
	 * @throws RemoteException If there is an error while rebinding.	
	 * @throws AccessException If we don't have access to the registry.	 	
	 */	 	
	private static void registerObject(final String name, final Remote object)
	{	
		try 
		{
			Remote stub = UnicastRemoteObject.exportObject(object, 0);
			registry.rebind(name, stub); 	
			Debug.print(name + " bound");
		}
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getIP() throws RemoteException
	{
		return ip;
	}
}
