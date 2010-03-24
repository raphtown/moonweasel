package org.atcs.moonweasel.entities;

import java.io.Serializable;

import org.atcs.moonweasel.Identifiable;

public abstract class Entity implements Identifiable, Serializable {
	private static int nextID = 0;
	public static String getEntityType(Class<? extends Entity> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	private static int getNextID() { 
		return nextID++;
	}
	
	private final int id;
	private boolean destroyed;
	
	protected Entity() {
		ChangeTracker.created(this);
		this.id = getNextID();
		this.destroyed = false;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public void destroy() {
		this.destroyed = true;
		ChangeTracker.deleted(this);
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
}
