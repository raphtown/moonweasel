package org.atcs.moonweasel.entities;

import java.io.Serializable;

import org.atcs.moonweasel.Identifiable;
import org.atcs.moonweasel.networking.changes.ChangeList;
import org.atcs.moonweasel.networking.changes.Trackable;

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
	private ChangeList changes;
	
	protected Entity() {
		this.id = getNextID();
		changes = new ChangeList(getClass().getName(), getID());
		this.destroyed = false;
		this.hasBeenChanged = true;
		this.changes.add("created entity");
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
	
	public ChangeList getRecentChanges()
	{
		return changes;
	}
}
