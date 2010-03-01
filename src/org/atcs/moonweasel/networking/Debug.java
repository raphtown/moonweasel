package org.atcs.moonweasel.networking;

public final class Debug
{
	public static boolean debug = true;
	
	public static void print(String s)
	{
		if(debug)
		{
			System.out.println(s);
		}
	}
}
