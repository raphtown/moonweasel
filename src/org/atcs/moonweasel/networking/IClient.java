package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote
{
	public void requestUpdateFromServer() throws RemoteException;
}
