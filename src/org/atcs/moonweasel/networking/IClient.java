package org.atcs.moonweasel.networking;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.changes.ChangeList;
import org.atcs.moonweasel.util.State;

public interface IClient extends IRMIObject
{	
	public void receiveEntities(boolean add, ArrayList<Entity> eList) throws RemoteException;
	
	public void receiveUpdatedStates(Map<Integer, State> sList) throws RemoteException;
	public void receiveChanges(List<ChangeList> changes) throws RemoteException;
	
	public ShipType sendShipChoice() throws RemoteException;
}
