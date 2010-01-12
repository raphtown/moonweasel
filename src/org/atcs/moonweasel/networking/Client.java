package org.atcs.moonweasel.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;

public class Client
{
	private Socket socket;
	public Client(Socket socket)
	{
		this.socket = socket;
	}
	
	public void sendPacket(String packet) throws IOException
	{
		
	}
	
	public void destroy()
	{
		
	}
}
