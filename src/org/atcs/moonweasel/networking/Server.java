package org.atcs.moonweasel.networking;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

import static org.atcs.moonweasel.networking.NetworkConfiguration.*;

/**
 * Serves as the core of the server itself - tells an announcer to begin announcing its presence, 
 * as well as listening for client packets and sending the client the appropriate update packets.
 * @author Maxime Serrano, Raphael Townshend
 */
public class Server extends Thread implements Networking, Runnable
{
	private boolean destroyed = false;
	private ArrayList<Client> clients = new ArrayList<Client>();
	private ServerSocket serverSocket;
	private ServerAnnouncer announcer;
	
	/**
	 * Builds a new Server instance, creating a new ServerSocket at port SERVER_PORT.<br />
	 * Does not start the server itself - must start() the thread to do that!
	 */
	public Server()
	{
		try
		{
			serverSocket = new ServerSocket(SERVER_PORT);
		}
		catch (IOException e)
		{
			if (NETWORK_DEBUG)
				System.out.println("Error opening server socket!");
		}
	}
	
	/**
	 * Updates all the clients. Should be called somewhere else, the server thread does none of this.
	 * @see org.atcs.moonweasel.networking.Networking#update()
	 */
	public void update()
	{
		assert !destroyed : "Trying to call update() on destroyed Server!";

		if (NETWORK_DEBUG)
			System.out.println("Server running update for " + clients.size() + " clients.");

		for (Client client : clients)
			sendUpdate(client);
	}
	
	/**
	 * Destroys the server instance, closing the client ports, its own port, and stopping the announcer.<br />
	 * Do not attempt to update clients after this has been called! 
	 */
	public void destroy()
	{
		try
		{
			serverSocket.close();
		}
		catch (IOException e)
		{
			if (NETWORK_DEBUG)
				System.err.println("Error while closing server socket.");
		}

		for (Client client : clients)
			client.destroy();

		clients.clear();
		destroyed = true;
	}
	
	private void sendUpdate(Client c)
	{
		System.out.println("Sending update to client " + c);
	}
	
	/**
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		try
		{
			announcer = new ServerAnnouncer();
			announcer.start();

			while (true)
			{
				if (NETWORK_DEBUG)
					System.out.println("Waiting for client");
				clients.add(new Client(serverSocket.accept()));
				if (NETWORK_DEBUG)
					System.out.println("Accepted Connection");
			}
		}
		catch (Exception e)
		{
			// this should NEVER happen
			if (NETWORK_DEBUG)
				e.printStackTrace();
		}
	}
	
	public ServerAnnouncer getAnnouncer()
	{
		return announcer;
	}
}
