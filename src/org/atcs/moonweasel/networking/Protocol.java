package org.atcs.moonweasel.networking;

import java.util.HashMap;

public abstract class Protocol
{
	private final static int COMMAND_POSITION = 0;
	private final static int RETURN_POSITION = 1;
	private final static int PARAMETER_POSITION = 2;

	public final static int PARAMETER_NAME_POSITION = 0;
	public final static int PARAMETER_CLASS_POSITION = 1;

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
	private static HashMap<String, String[][]> parammap;
	private static HashMap<String, Class<?>> returnmap;

	static
	{
		ismap = new HashMap<Integer, String[]>();
		simap = new HashMap<String, Integer>();
		parammap = new HashMap<String, String[][]>();
		returnmap = new HashMap<String, Class<?>>();
		
		for(int i = 0; i < commands.length; i++)
		{
			ismap.put(i, commands[i]);
			simap.put(commands[i][COMMAND_POSITION], i);
			
			int numParams = (commands[i].length - PARAMETER_POSITION) / 2;
			String[][] parameters = new String[numParams][2];
			for(int j = 0; j < numParams; j++)
			{
				parameters[j][PARAMETER_NAME_POSITION] = commands[i][PARAMETER_POSITION + j * 2];
				parameters[j][PARAMETER_CLASS_POSITION] = commands[i][PARAMETER_POSITION + j * 2 + 1];
			}
			
			parammap.put(commands[i][COMMAND_POSITION], parameters);
			returnmap.put(commands[i][COMMAND_POSITION], commands[i][RETURN_POSITION].getClass());
		}
	}

	public static short getShortValue(String command)
	{
		return simap.get(command).shortValue();
	}

	public static String getMethodName(short command)
	{
		return ismap.get(Integer.valueOf(command))[COMMAND_POSITION];
	}

	public static String[][] getParameters(String command)
	{
		return parammap.get(command);
	}

	public static String getReturnValue(String command)
	{
		return ismap.get(simap.get(command))[RETURN_POSITION];
	}

	public static int getNumParams(String command)
	{
		return getParameters(command).length;
	}

}
