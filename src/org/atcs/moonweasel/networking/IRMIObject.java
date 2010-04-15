package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRMIObject extends Remote 
{
	public String getIP() throws RemoteException;
}
