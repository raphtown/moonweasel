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
			return m.invoke(server, parameters);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
