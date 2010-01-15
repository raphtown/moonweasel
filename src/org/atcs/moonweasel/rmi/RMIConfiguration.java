package org.atcs.moonweasel.rmi;

/**
 * Serves as the configuration file for all RMI-related classes and methods.
 * @author Maxime Serrano, Raphael Townshend
 */
public final class RMIConfiguration
{
	// just prevents initialization
	private RMIConfiguration() { }
	
	/**
	 * The port that the RMI server will use to create and connect to its registry.
	 */
	public static final int RMI_PORT = 4001;
	
	/**
	 * Whether or not to print out the debug strings.
	 */
	public static final boolean RMI_DEBUG = true;
}
