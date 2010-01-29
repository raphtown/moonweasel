package org.atcs.moonweasel.networking;

import java.util.HashMap;

public abstract class Protocol
{
	public final static String[] commands = {"connect", "sendInput", "requestMap"};
	private static HashMap<Integer, String> ismap;
	private static HashMap<String, Integer> simap;
	
	static
	{
		ismap = new HashMap<Integer, String>();
		simap = new HashMap<String, Integer>();
		for(int i = 0; i < commands.length; i++)
		{
			ismap.put(i, commands[i]);
			simap.put(commands[i], i);
		}
	}
	
	public static short shortValue(String command)
	{
		return simap.get(command).shortValue();
	}
	
	public static String stringValue(short command)
	{
		return ismap.get(command);
	}
	
}
