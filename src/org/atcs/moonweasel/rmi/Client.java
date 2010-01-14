package org.atcs.moonweasel.rmi;

import java.rmi.Naming;

public class Client {
    public static void main(String args[])
    {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try
        {
            Simulator comp = (Simulator) Naming.lookup("Simulator");
            int pi = comp.doStuff();
            System.out.println(pi);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
}