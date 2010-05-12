package org.atcs.moonweasel.networking;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Thread implements Networking
{
	private static final int SERVER_PORT = 40001;
	
	private int numClients = 0;
	private ArrayList<Client> clients = new ArrayList<Client>();
	private ServerSocket serverSocket;
	
	public Server()
	{
		try
		{
			serverSocket = new ServerSocket(SERVER_PORT);
		}
		catch (IOException e)
		{
			System.out.println("Error opening server socket!");
		}
	}

	public void update()
	{
		System.out.println("Server running update for " + numClients + " clients.");
		for (Client client : clients)
			sendUpdate(client);
	}
	
	public void destroy()
	{

	}
	
	private void sendUpdate(Client c)
	{
		System.out.println("Sending update to client " + c);
	}
	
	public void run()
	{
		try
		{
			new ServerAnnouncer().start();
			while (true)
			{
				System.out.println("Waiting for client");
				Socket client = serverSocket.accept();
				System.out.println("Accepted Connection");
				clients.add(new Client(client));
			}
		}
		catch (Exception e)
		{
			
		}
	}
}
