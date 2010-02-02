package org.atcs.moonweasel.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.atcs.moonweasel.entities.Entity;

/**
 * The interface linked to being a server. Used only for the purpose of keeping 
 * RMI happy.
 * @author Maxime Serrano, Raphael Townshend
 */
public interface IBetaServer extends Remote
{
    /**
	 * When the client sends in a command, call this method.
	 * @param command The command(s) that have been pressed.
	 * @param c The client that is using this command.
	 */
	public Object sendPacket(short command, String c) throws RemoteException;
}