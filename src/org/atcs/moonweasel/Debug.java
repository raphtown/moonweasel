package org.atcs.moonweasel;

public final class Debug
{
	public static boolean debug = false;
	
	public static void print(String s)
	{
		if(debug)
		{
			System.out.println("DEBUG: " + s);
		}
	}
}
