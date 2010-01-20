package org.atcs.moonweasel.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
	 * @throws RemoteException If bad things happen - server goes away, that sort of thing.
	 */
	public void connect(String c) throws RemoteException;
	
	/**
	 * Used for nothing but testing purposes.
	 * @return 0.
	 * @throws RemoteException If the server goes away or some other part of RMI explodes.
	 */
    public int doStuff() throws RemoteException;
    
    /**
	 * When the client sends in a command, call this method.
	 * @param command The command(s) that have been pressed.
	 * @param c The client that is using this command.
	 * @return Whether or not the client is allowed to call this method.
	 */
	public boolean doCommand(short command, String c) throws RemoteException;
}