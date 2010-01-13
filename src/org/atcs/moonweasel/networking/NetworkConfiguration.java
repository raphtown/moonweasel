package org.atcs.moonweasel.networking;

public final class NetworkConfiguration
{
	// ideally instead of this we would read in from a text file

	// only purpose for this is to make it un-initializable
	private NetworkConfiguration() {}
	
	// Global
	public static final boolean NETWORK_DEBUG = true;
	
	// Server
	public static final int SERVER_PORT = 40001;
	
	// Server Announcer
	public static final String ANNOUNCER_MULTICAST_ADDRESS = "224.0.0.1";
	public static final int ANNOUNCER_MULTICAST_PORT = 4446;
	public static final int ANNOUNCER_SLEEP_TIME = 2000;
}
