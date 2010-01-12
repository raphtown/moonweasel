package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.NetworkConfiguration.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
	private Socket socket;
	private PrintStream sout = null;
	@SuppressWarnings("unused")
	private Scanner sin = null;
	
	private boolean destroyed = false;

	public Client(Socket socket)
	{
		this.socket = socket;
		initSocketOutAndIn();
	}
	
	private void initSocketOutAndIn()
	{
		try
		{
			sout = new PrintStream(socket.getOutputStream());
			sin = new Scanner(socket.getInputStream());
		}
		catch (IOException e)
		{
			if (NETWORK_DEBUG)
				System.err.println("Error opening socket in/out streams!");
		}
	}
	
	public void sendPacket(String packet) throws IOException
	{
		assert !destroyed : "Attempting to send packet to destroyed client!";
		sout.println(packet);
	}
	
	public void destroy()
	{
		try
		{
			if (socket != null)
				socket.close();
			if (sin != null)
				sin.close();
			if (sout != null)
				sout.close();
		}
		catch (IOException e)
		{
			if (NETWORK_DEBUG)
			{
				System.err.println("Error closing socket");
				e.printStackTrace();
			}
		}
		
		destroyed = true;
	}
}
