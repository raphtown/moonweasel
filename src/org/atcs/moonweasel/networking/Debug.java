package org.atcs.moonweasel.networking;

public final class Debug
{
	public static final boolean DEBUG = true;
	
	public static void print(String s)
	{
		if(DEBUG)
			System.out.println(s);
	}
}
