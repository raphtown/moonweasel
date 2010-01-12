package org.atcs.moonweasel.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Client
{
	private DatagramSocket socket;
	public Client(DatagramSocket socket)
	{
		this.socket = socket;
	}
	
	public void sendPacket(DatagramPacket packet) throws IOException
	{
		socket.send(packet);
	}
	
	public void destroy()
	{
		
	}
}
