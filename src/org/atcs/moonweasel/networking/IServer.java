package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.changes.ChangeList;
import org.atcs.moonweasel.util.Vector;

/**
 * The interface linked to being a server. Used only for the purpose of keeping 
 * RMI happy.
 * @author Maxime Serrano, Raphael Townshend
 */
public interface IServer extends Remote
{
	/**
	 * Connects the given client to the server. Given that we don't yet have a 
	 * reliable way of disconnecting, we should perhaps hope that nobody's going 
	 * to have their program randomly crash. 
	 * @param c The client that is being connected to the server. 
	 * @throws RemoteException If bad things happen  server goes away, that sort of thing.
	 */
	public void connect(final String c) throws RemoteException;

	/**
	 * When the client sends in a command, call this method.
	 * @param command The command(s) that have been pressed.
	 * @param c The client that is using this command.
	 */
	public void doCommand(short command, Vector mouse, final String c) throws RemoteException;

	/**
	 * Gets an updated list of ModelEntities.
	 * @param c The client that is asking for an update.
	 * @return A list of ModelEntities.
	 */
	public List<ChangeList> requestUpdate(final String c) throws RemoteException;

	/**
	 * The given client chooses a ship type, which then spawns.
	 * @param clientHostname The client that is being connected to the server. 
	 * @param shipType The type of ship that the client has chosen.
	 * @throws RemoteException If bad things happen - server goes away, that sort of thing.
	 */
	public void chooseShip(final ShipType shipType, final String c) throws RemoteException;
}