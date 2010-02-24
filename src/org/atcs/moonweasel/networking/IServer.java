package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface linked to being a server. Used only for the purpose of keeping 
 * RMI happy.
 * @author Maxime Serrano, Raphael Townshend
 */
public interface IServer extends Remote
{
	public Object sendPacket(short command, Object... parameters) throws RemoteException;
}