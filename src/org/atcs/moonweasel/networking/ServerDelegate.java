package org.atcs.moonweasel.networking;

import org.atcs.moonweasel.util.Vector;

public interface ServerDelegate 
{
	public void doCommand(short command, Vector mouse, int entityID);
}
