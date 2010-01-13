package org.atcs.moonweasel.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Simulator {

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
            System.getSecurityManager().checkAccept("127.0.0.1", 4001);
            System.getSecurityManager().checkAccept("172.30.24.61", 4001);
        }
        try {
            String name = "Simulator";
            Server engine = new Server();
            Simulator stub =
                (Simulator) UnicastRemoteObject.exportObject(engine, 4001);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Simulator bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
    }

	public int doStuff() throws RemoteException
	{
		// TODO Auto-generated method stub
		return 0;
	}
}