package org.atcs.moonweasel.networking;

import java.io.Serializable;
import java.util.List;

public class IState implements Serializable
{
	private static final long serialVersionUID = 7529233692222397283L;

	public List<Object> objects;
	
	public int ownerID;
	
	public long time;
	
	public IState(List<Object> objects, int ownerID)
	{
		this.objects = objects;
		this.ownerID = ownerID;
	}
}
