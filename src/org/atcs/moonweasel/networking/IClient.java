package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.util.State;

public interface IClient extends Remote
{
	public void requestUpdateFromServer() throws RemoteException;
	
	public void receiveNewEntities(ArrayList<Entity> eList) throws RemoteException;
	
	public void receiveUpdatedStates(Map<Integer, State> sList) throws RemoteException;
}
