package org.atcs.moonweasel.rmi.announcer;

import static org.atcs.moonweasel.rmi.announcer.AnnouncerConfiguration.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;

import org.atcs.moonweasel.Destructible;

/**
 * Handles listening to server announcements as well as creating them, in order to tell clients they exist. 
 * @author Maxime Serrano, Raphael Townshend
 */
public class ServerAnnouncer extends Thread implements Runnable, Destructible
{
	private boolean running = true;
	private DatagramSocket socket;
	private String serverName; // NOT A HOSTNAME
	
	public ServerAnnouncer(String title)
	{
		serverName = title;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		try 
		{
			if (ANNOUNCER_DEBUG)
				System.out.println("Preparing for announcement...");				
			
			final InetAddress myaddr = InetAddress.getLocalHost();
			final String txt = myaddr.getHostAddress() + " " + serverName;
			final InetAddress group = InetAddress.getByName(ANNOUNCER_MULTICAST_ADDRESS);
			final DatagramPacket packet = new DatagramPacket(txt.getBytes(), txt.length(), group, ANNOUNCER_MULTICAST_PORT);
			socket = new DatagramSocket();
			while (running)
			{
// 				this is annoying
				if (ANNOUNCER_DEBUG)
					System.out.println("Sending packet");

				socket.send(packet);
				Thread.sleep(ANNOUNCER_SLEEP_TIME);
			}
		} 
		catch (Exception e)
		{
			if (ANNOUNCER_DEBUG)
				System.out.println("Error: " + e.getMessage());
		}
	}

	/**
	 * @return A list of server addresses that have been announcing using this port and multicast group.
	 * @throws IOException If there is an error receiving the packet or joining the multicast group.
	 */
	public static List<String> getServerList() throws IOException
	{
		// why so large?
		final int BUFFER_SIZE = 256;

		final MulticastSocket socket = new MulticastSocket(ANNOUNCER_MULTICAST_PORT);

		if (ANNOUNCER_DEBUG)
			System.out.println("Opened multicast socket on " + socket.getInterface().toString());

		final InetAddress group = InetAddress.getByName(ANNOUNCER_MULTICAST_ADDRESS);
		socket.joinGroup(group);

		List<String> servers = new LinkedList<String>();
		boolean done = false;

		// Keep waiting until we get the same server twice
		while (!done)
		{
			byte[] buf = new byte[BUFFER_SIZE];
			final DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());

			if (ANNOUNCER_DEBUG)
				System.out.println("Got announcement: " + received);

			if (!(done = servers.contains(received)))
				servers.add(received);
		}
		socket.leaveGroup(group);
		socket.close();
		return servers;
	}
	
	/**
	 * Essentially works like a deconstructor, but doesn't delete its references.
	 * Do not attempt to use this object after it has been destroyed.
	 */
	public void destroy()
	{
		running = false;
		if (socket != null)
			socket.close();
		socket = null;
	}
}