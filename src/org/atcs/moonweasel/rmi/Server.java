package org.atcs.moonweasel.rmi;



import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Simulator {

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Simulator";
            Server engine = new Server();
            Simulator stub =
                (Simulator) UnicastRemoteObject.exportObject(engine, 1099);
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.rebind("Simulator",stub);
            //registry.rebind(name, stub);
            System.out.println("Simulator bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
    }

	@Override
	public int doStuff() throws RemoteException
	{
		// TODO Auto-generated method stub
		return 0;
	}
}