package org.atcs.moonweasel.rmi;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

import org.atcs.moonweasel.networking.ServerAnnouncer;

public class Client {
    public static void main(String args[])
    {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try
        {
        	String hostname = getConnection();
        	Registry registry = LocateRegistry.getRegistry(hostname, RMIConfiguration.RMI_PORT);
            Simulator comp = (Simulator) registry.lookup("Simulator");
            int pi = comp.doStuff();
            System.out.println(pi);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
    
    private static String getConnection() throws IOException
	{
		List<String> hostnames = ServerAnnouncer.getServerList();
		
		System.out.println("Client started...");
		System.out.println("Available hosts:");
		for(int i = 0; i < hostnames.size(); i++)
		{
			System.out.print(i + 1 + ") ");
			System.out.println(hostnames.get(i));
		}
		System.out.println("Which server would you like to join?");
		Scanner console = new Scanner(System.in);
		int number = console.nextInt();
		while(number < 1 || number > hostnames.size())
		{
			System.out.println("Invalid server number");
			for(int i = 0; i < hostnames.size(); i++)
			{
				System.out.print((i + 1) + ") ");
				System.out.println(hostnames);
				System.out.println("Which server would you like to join?");
				number = console.nextInt();
				console.nextLine();
			}
		}

		return (String) hostnames.get(number - 1).split(" ")[0];
	}
}
