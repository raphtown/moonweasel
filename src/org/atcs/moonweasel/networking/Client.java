package org.atcs.moonweasel.networking;

import static org.atcs.moonweasel.networking.NetworkConfiguration.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import org.atcs.moonweasel.Destructible;

public class Client implements Destructible
{
	private Socket socket;
	private PrintStream sout = null;
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
	
	public void sendPacket(String packet)
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

		socket = null;
		sin = null;
		sout = null;

		destroyed = true;
	}

	// careful...
	public String readPacket()
	{
		assert !destroyed : "Attempting to read packet from destroyed client!";
		return sin.nextLine();
	}
}
