package org.atcs.moonweasel.networking;

import java.lang.reflect.Method;

import java.util.HashMap;

public abstract class Protocol
{

	private static HashMap<Integer, String> ismap = new HashMap<Integer, String>();
	private static HashMap<String, Integer> simap = new HashMap<String, Integer>();
	private static HashMap<String,  Class<?>[]> parammap = new HashMap<String, Class<?>[]>();
	private static HashMap<String, Class<?>> returnmap = new HashMap<String, Class<?>>();

	static
	{
		Method[] methods = IServer.class.getMethods();

		for(int i = 0; i < methods.length; i++)
		{
			String methodName = methods[i].getName();
			Class<?>[] parameters = methods[i].getParameterTypes();
			Class<?> returnType = methods[i].getReturnType();

			ismap.put(i, methodName);
			simap.put(methodName, i);
			parammap.put(methodName, parameters);
			returnmap.put(methodName, returnType);
		}
	}

	/*

	public static short getShortValue(String command)
	{
		return simap.get(command).shortValue();
	}

	public static String getMethodName(short command)
	{
		return ismap.get(command);
	}

	public static Class<?> getReturnValue(String command)
	{
		return returnmap.get(command);
	}

	public static int getNumParams(String command)
	{
		return parammap.get(command).length;
	}

	public static Object[] getEmptyParamList(String command)
	{
		Object[] params = new Object[Protocol.getNumParams(command)];
		return params;
	}

	 */

	public static Class<?>[] getParameters(String command)
	{
		return parammap.get(command);
	}

	public static Object sendPacket(String command, Object[] parameters, IServer server)
	{
		try
		{
			Class<?>[] paramClasses = getParameters(command);
			Method m = server.getClass().getDeclaredMethod(command, paramClasses);
			System.out.print("Invoking: " + m.getName() + " with parameters: ");
			for(int i = 0; i < parameters.length; i++)
			{
				System.out.print(parameters[i] + " ");
			}
			System.out.println();
			Object o =  m.invoke(server, parameters);
			System.out.println("Object returned: " + o);
			return o;
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
