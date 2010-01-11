package org.atcs.moonweasel.networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ServerAnnouncer extends Thread implements Runnable
{
	private static final String MULTICAST_ADDRESS = "224.0.0.1";
	private static final int MULTICAST_PORT = 4446;
	
	public ServerAnnouncer()
	{
		// we don't want to do very much in here,
		// don't take up main thread with server info
	}
	
	public void run()
	{
		try 
		{
			System.out.println("Preparing for announcement...");
			InetAddress myaddr = InetAddress.getLocalHost();
			String txt = myaddr.getHostAddress();
			InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
			DatagramPacket packet = new DatagramPacket(txt.getBytes(), txt.length(), group, MULTICAST_PORT);
			DatagramSocket socket = new DatagramSocket();
			while (true)
			{
				System.out.println("Sending packet");
				socket.send(packet);
				Thread.sleep(2000);
			}
		} 
		catch (Exception e)
		{
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static List<String> getServerList() throws IOException
	{
		MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);
		System.out.println("Opened multicast socket on " + socket.getInterface().toString());
		InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
		socket.joinGroup(group);

		DatagramPacket packet;
		List<String> servers = new LinkedList<String>();
		boolean done = false;

		// Keep waiting until we get the same server twice
		while (!done) 
		{
			byte[] buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Got announcement: " + received);

			if (!(done = servers.contains(received)))
				servers.add(received);
		}
		socket.leaveGroup(group);
		socket.close();
		return servers;
	}
}
