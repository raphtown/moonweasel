package org.atcs.moonweasel.rmi.announcer;

public class AnnouncerConfiguration
{
	// ideally instead of this we would read in from a text file

	// only purpose for this is to make it un-initializable
	private AnnouncerConfiguration() {}

	public static final boolean ANNOUNCER_DEBUG = true;
	public static final String ANNOUNCER_MULTICAST_ADDRESS = "224.0.0.1";
	public static final int ANNOUNCER_MULTICAST_PORT = 1100;
	public static final int ANNOUNCER_SLEEP_TIME = 2000;
}
