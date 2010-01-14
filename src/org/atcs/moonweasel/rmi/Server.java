package org.atcs.moonweasel.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Simulator {

    public static void main(String[] args) {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try {
            Server engine = new Server();
            Simulator stub = (Simulator) UnicastRemoteObject.exportObject(engine, 4001);
            Naming.rebind("Simulator", stub);
            System.out.println("Simulator bound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public int doStuff() throws RemoteException
	{
		return 0;
	}
}