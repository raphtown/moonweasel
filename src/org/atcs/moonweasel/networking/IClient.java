package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.changes.ChangeList;

public interface IClient extends Remote
{	
	public void receiveEntities(boolean add, ArrayList<Entity> eList) throws RemoteException;
	public void receiveChanges(List<ChangeList> changes) throws RemoteException;
	public void receiveIStates(List<IState> IStates) throws RemoteException;
	public ShipType sendShipChoice() throws RemoteException;
}
