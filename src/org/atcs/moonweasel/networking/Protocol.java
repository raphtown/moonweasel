package org.atcs.moonweasel.networking;

import java.util.HashMap;

public abstract class Protocol
{
	private final static int COMMAND_POSITION = 0;
	private final static int RETURN_POSITION = 1;
	private final static int PARAMETER_POSITION = 2;
	
	
	
	/*
	 *	To define a new command, you must obey the following protocol.
	 *
	 * String 1: method name
	 * String 2: return value
	 * 
	 * Optional:
	 * String 3: first parameter name
	 * String 4: first parameter class type
	 * etc...
	 */
	private final static String[][] commands = {
		{"connect",
			"boolean",
			"ip", "String"}, 
		{"sendInput",
			"boolean",
			"ip", "String"}, 
		{"requestMap",
			"boolean",
			"ip", "String"}
		};
	private static HashMap<Integer, String[]> ismap;
	private static HashMap<String, Integer> simap;
	
	static
	{
		ismap = new HashMap<Integer, String[]>();
		simap = new HashMap<String, Integer>();
		for(int i = 0; i < commands.length; i++)
		{
			ismap.put(i, commands[i]);
			simap.put(commands[i][COMMAND_POSITION], i);
		}
	}
	
	public static short shortValue(String command)
	{
		return simap.get(command).shortValue();
	}
	
	public static String methodName(short command)
	{
		return ismap.get(Integer.valueOf(command))[COMMAND_POSITION];
	}
	
	public static String[][] parameters(String command)
	{
		
		String[] commandValues =  ismap.get(simap.get(command));
		int numParams = (commandValues.length - PARAMETER_POSITION) / 2;
		String[][] parameters = new String[numParams][2];
		for(int i = 0; i < numParams; i++)
		{
			parameters[i][0] = commandValues[PARAMETER_POSITION + i * 2];
			parameters[i][1] = commandValues[PARAMETER_POSITION + i * 2 + 1];
		}
		return parameters;
	}
	
	public static String returnValue(String command)
	{
		return ismap.get(simap.get(command))[RETURN_POSITION];
	}
	
}
