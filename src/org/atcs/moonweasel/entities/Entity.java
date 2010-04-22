package org.atcs.moonweasel.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.atcs.moonweasel.Identifiable;
import org.atcs.moonweasel.networking.changes.ChangeList;

public abstract class Entity implements Identifiable, Serializable {
	private static int nextID = 0;
	
	public static String getEntityType(Class<? extends Entity> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	private static int getNextID() { 
		return nextID++;
	}
	
	static int getNextIDWithoutChanging()
	{
		return nextID;
	}
	
	static void setNextID(int nextIDIn)
	{
		nextID = nextIDIn;
	}
	
	public boolean sentToAll;
	private final int id;
	private boolean destroyed;
	private List<String> clientsThatHaveGottenChanges = new LinkedList<String>();
	private ChangeList globalChanges;
	private Map<String, ChangeList> clientSpecificChanges = new HashMap<String, ChangeList>();
	private boolean hasChanged = false;
	 
	protected Entity() {
		sentToAll = false;
		this.id = getNextID();
		globalChanges = new ChangeList(getClass().getSimpleName(), getID());
		this.destroyed = false;
		this.globalChanges.add("created entity");
	}
	
	protected void addChange(String change)
	{
		change();
		globalChanges.add(change);
		for (String key : clientSpecificChanges.keySet())
			clientSpecificChanges.get(key).add(change);
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public void destroy() {
		this.destroyed = true;
	}
	
	public final String getEntityType() {
		return this.getClass().getSimpleName().toLowerCase();
	}

	public final int getID() {
		return this.id;
	}
	
	public final long getTime() {
		return EntityManager.getEntityManager().getTime();
	}
	
	protected final void scheduleThink(int ms) {
		EntityManager.getEntityManager().registerThink(this, ms);
	}
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
	public abstract void spawn();
	
	public void think() {
	}
	
	protected void change()
	{
		hasChanged = true;
		clientsThatHaveGottenChanges = new LinkedList<String>();
	}
	
	public boolean hasRecentlyChangedForClient(String c)
	{
		return !(clientsThatHaveGottenChanges.contains(c));
	}
	
	public boolean hasChangedForAll()
	{
		return hasChanged;
	}
	
	public void sent(String c)
	{
		clientsThatHaveGottenChanges.add(c);
		clearChanges(c);
	}
	
	public void sent()
	{
		hasChanged = false;
		clearChanges();
	}
	
	public ChangeList getRecentChanges(String c)
	{
		if (!clientSpecificChanges.keySet().contains(c))
			return globalChanges;
		else
			return clientSpecificChanges.get(c);
	}
	
	public ChangeList getRecentChanges()
	{
		return globalChanges;
	}
	
	public void clearChanges()
	{
		globalChanges = new ChangeList(getClass().getName(), getID());
		clientsThatHaveGottenChanges = new LinkedList<String>();
		hasChanged = false;
	}
	
	private void clearChanges(String c)
	{
		clientSpecificChanges.put(c, new ChangeList(getClass().getName(), getID()));
		clientsThatHaveGottenChanges.remove(c);
	}

	public boolean equals(Entity e)
	{
		return e.getID() == this.getID();
	}
}
