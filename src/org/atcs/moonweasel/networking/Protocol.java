package org.atcs.moonweasel.networking;

import java.util.HashMap;

public abstract class Protocol
{
	public final static String[] commands = {"connect", "sendInput", "requestMap"};
	public final static String[][] parameters = {{"ip", "String"}, {"ip", "String"}, {"ip", "String"}};
	public final static String[] returnValue = {"boolean", "boolean", "Map"};
	private static HashMap<Integer, String> ismap;
	private static HashMap<String, Integer> simap;
	private static HashMap<String, String[]> parammap;
	private static HashMap<String, String> returnmap;
	
	static
	{
		ismap = new HashMap<Integer, String>();
		simap = new HashMap<String, Integer>();
		parammap = new HashMap<String, String[]>();
		returnmap = new HashMap<String, String>();
		for(int i = 0; i < commands.length; i++)
		{
			ismap.put(i, commands[i]);
			simap.put(commands[i], i);
			parammap.put(commands[i], parameters[i]);
			returnmap.put(commands[i], returnValue[i]);
		}
	}
	
	public static short shortValue(String command)
	{
		return simap.get(command).shortValue();
	}
	
	public static String methodName(short command)
	{
		return ismap.get(Integer.valueOf(command));
	}
	
	public static String[] parameters(String command)
	{
		return parammap.get(command);
	}
	
	public static String returnValue(String command)
	{
		return returnmap.get(command);
	}
	
}
