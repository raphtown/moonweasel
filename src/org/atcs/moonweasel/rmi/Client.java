package org.atcs.moonweasel.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String args[])
    {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try
        {
        	Registry registry = LocateRegistry.getRegistry("localhost", RMIConfiguration.RMI_PORT);
            Simulator comp = (Simulator) registry.lookup("Simulator");
            int pi = comp.doStuff();
            System.out.println(pi);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
}