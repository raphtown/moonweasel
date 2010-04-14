package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.atcs.moonweasel.entities.Entity;

public interface IClient extends Remote
{
	public void requestUpdateFromServer() throws RemoteException;
	
	public void receiveNewEntities(ArrayList<Entity> eList) throws RemoteException;
}
