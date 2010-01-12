package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.NetworkConfiguration.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles listening to server announcements as well as creating them, in order to tell clients they exist. 
 * @author Maxime Serrano, Raphael Townshend
 */
public class ServerAnnouncer extends Thread implements Runnable
{	
	/**
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		try 
		{
			if (NETWORK_DEBUG)
				System.out.println("Preparing for announcement...");				
			
			final InetAddress myaddr = InetAddress.getLocalHost();
			final String txt = myaddr.getHostAddress();
			final InetAddress group = InetAddress.getByName(ANNOUNCER_MULTICAST_ADDRESS);
			final DatagramPacket packet = new DatagramPacket(txt.getBytes(), txt.length(), group, ANNOUNCER_MULTICAST_PORT);
			final DatagramSocket socket = new DatagramSocket();
			while (true)
			{
				if (NETWORK_DEBUG)
					System.out.println("Sending packet");

				socket.send(packet);
				Thread.sleep(2000);
			}
		} 
		catch (Exception e)
		{
			if (NETWORK_DEBUG)
				System.out.println("Error: " + e.getMessage());
		}
	}

	/**
	 * @return A list of server addresses that have been announcing using this port and multicast group.
	 * @throws IOException If there is an error receiving the packet or joining the multicast group.
	 */
	public static List<String> getServerList() throws IOException
	{
		final MulticastSocket socket = new MulticastSocket(ANNOUNCER_MULTICAST_PORT);

		if (NETWORK_DEBUG)
			System.out.println("Opened multicast socket on " + socket.getInterface().toString());

		final InetAddress group = InetAddress.getByName(ANNOUNCER_MULTICAST_ADDRESS);
		socket.joinGroup(group);

		List<String> servers = new LinkedList<String>();
		boolean done = false;

		// Keep waiting until we get the same server twice
		while (!done) 
		{
			byte[] buf = new byte[256];
			final DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());

			if (NETWORK_DEBUG)
				System.out.println("Got announcement: " + received);

			if (!(done = servers.contains(received)))
				servers.add(received);
		}
		socket.leaveGroup(group);
		socket.close();
		return servers;
	}
}