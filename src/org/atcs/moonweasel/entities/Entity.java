package org.atcs.moonweasel.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.atcs.moonweasel.Identifiable;
import org.atcs.moonweasel.networking.Trackable;

public abstract class Entity implements Identifiable, Serializable, Trackable {
	private static int nextID = 0;
	public static String getEntityType(Class<? extends Entity> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	private static int getNextID() { 
		return nextID++;
	}
	
	private final int id;
	private boolean destroyed;
	private boolean hasBeenChanged = false;
	private List<String> changes = new LinkedList<String>();
	
	protected Entity() {
		this.id = getNextID();
		this.destroyed = false;
		this.hasBeenChanged = true;
		this.changes.add("created");
	}
	
	protected void addChange(String change)
	{
		change();
		changes.add(change);
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
		hasBeenChanged = true;
	}
	
	public boolean hasRecentlyChanged()
	{
		return hasBeenChanged;
	}
	
	public void sent()
	{
		hasBeenChanged = false;
	}
	
	public List<String> getRecentChanges()
	{
		return changes;
	}
}
