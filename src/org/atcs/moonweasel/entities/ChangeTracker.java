package org.atcs.moonweasel.entities;

import java.util.HashMap;

public class ChangeTracker
{
	static HashMap<Integer, HashMap<String, Object>> changeMap = new HashMap<Integer, HashMap<String, Object>>();

	public static void created(Entity target)
	{
		HashMap<String, Object> temp = changeMap.get(target.getID());
		temp.put("created", null);
	}

	public static void deleted(Entity target)
	{
		HashMap<String, Object> temp = changeMap.get(target.getID());
		temp.clear();
		temp.put("deleted", null);
	}

	public static void setProperty(Entity target, String propertyName, Object newValue)
	{
		try
		{
			if(target.getClass().getDeclaredField(propertyName).getAnnotations().length != 0 && 
					!target.getClass().getDeclaredField(propertyName).getAnnotations()[0].equals("trackable"))
			{
				if(!changeMap.containsKey(target.getID()))
				{
					changeMap.put(target.getID(), new HashMap<String, Object>());
				}
				HashMap<String, Object> temp = changeMap.get(target.getID());
				temp.put(propertyName, newValue);
			}
			target.getClass().getDeclaredField(propertyName).set(target, newValue);
		} 
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (NoSuchFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
