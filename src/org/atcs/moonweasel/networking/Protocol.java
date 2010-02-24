package org.atcs.moonweasel.networking;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
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
	

	public static short getShortValue(String command)
	{
		return simap.get(command).shortValue();
	}

	public static String getMethodName(short command)
	{
		return ismap.get(command);
	}

	public static Class<?>[] getParameters(String command)
	{
		return parammap.get(command);
	}

	public static Class<?> getReturnValue(String command)
	{
		return returnmap.get(command);
	}

	public static int getNumParams(String command)
	{
		return parammap.get(command).length;
	}

	public static Object sendPacket(String command, IServer server, Object self)
	{
		return sendPacket(command, new Object[getNumParams(command)], server, self);
	}
	
	public static Object[] getEmptyParamList(String command)
	{
		Object[] params = new Object[Protocol.getNumParams(command)];
		return params;
	}
	
	public static Object sendPacket(String command, Object[] parameters, IServer server, Object self)
	{
		Class<?>[] expectedParameters = Protocol.getParameters(command);
		Object[] values = new Object[Protocol.getNumParams(command)];
		for(int i = 0; i < expectedParameters.length; i++)
		{
			try
			{
				if(parameters[i] == null)
				{
					values[i] = self.getClass().getField(expectedParameters[i][Protocol.PARAMETER_NAME_POSITION]).get(self);
				}
				else
				{
					values[i] = parameters[i];
				}
			} catch (SecurityException e)
			{
				e.printStackTrace();
			} catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			return server.sendPacket(Protocol.getShortValue(command), values);
		} 
		catch (RemoteException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static Class<?> autoUnBox(Class<?> c)
	{
		if(c.getSimpleName().equals("Short")) return short.class;
		if(c.getSimpleName().equals("Integer")) return int.class;
		else return c;
	}

}
