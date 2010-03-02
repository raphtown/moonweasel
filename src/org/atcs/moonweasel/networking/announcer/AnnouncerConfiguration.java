package org.atcs.moonweasel.networking.announcer;

/**
 * Serves as the configuration file for all Announcer-related classes and methods.
 * @author Maxime Serrano, Raphael Townshend
 */
public final class AnnouncerConfiguration
{
	// only purpose for this is to make it un-initializable
	private AnnouncerConfiguration() {}
	
	/**
	 * The address that the announcer will broadcast to. Multicast groups are 
	 * the best way to, in Java, broadcast individual packets to large amounts 
	 * of anonymous clients - essential for the original listing of servers, 
	 * unless we want to run an independent name server somewhere else.
	 * 
	 * This is also the group that all interested clients must connect to.
	 */
	public static final String ANNOUNCER_MULTICAST_ADDRESS = "224.0.0.1";
	
	/**
	 * The port linked to the above multicast group.
	 */
	public static final int ANNOUNCER_MULTICAST_PORT = 4446;
	
	/**
	 * Time between the sending of "I exist!" packets.
	 */
	public static final int ANNOUNCER_SLEEP_TIME = 2000;
}
