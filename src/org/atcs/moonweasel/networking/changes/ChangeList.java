package org.atcs.moonweasel.networking.changes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ChangeList implements Serializable
{
	private static final long serialVersionUID = 2756185332850488215L;
	private final List<String> changes = new LinkedList<String>();
	private final String typeName;
	private final int entityID;

	public ChangeList(String typeName, int entityID)
	{
		this.typeName = typeName;
		this.entityID = entityID;
	}
	
	public int getID()
	{
		return entityID;
	}
	
	public String getTypeName()
	{
		return typeName;
	}
	
	public List<String> getChanges()
	{
		return changes;
	}
	
	public void add(String change)
	{
		changes.add(change);
	}
}
