package org.atcs.moonweasel.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.atcs.moonweasel.rmi.announcer.ServerAnnouncer;

public class Server implements Simulator {

	private static int stuff = 0;
    public static void main(String[] args) {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try {
            Server engine = new Server();
            new ServerAnnouncer(new java.util.Scanner(System.in).nextLine()).start();
            Registry registry = LocateRegistry.createRegistry(RMIConfiguration.RMI_PORT);
            Simulator stub = (Simulator) UnicastRemoteObject.exportObject(engine, 0);
            registry.rebind("Simulator", stub);
            System.out.println("Simulator bound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public int doStuff() throws RemoteException
	{
		return stuff++;
	}
}