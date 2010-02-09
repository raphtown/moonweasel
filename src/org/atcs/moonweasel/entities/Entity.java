package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.Identifiable;

public abstract class Entity implements Identifiable {
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
		this.id = getNextID();
		this.destroyed = false;
	}
	
	public <T extends Entity> T createEntity(String type) {
		return EntityManager.getEntityManager().create(type);
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
	
	protected void scheduleThink(int ms) {
		EntityManager.getEntityManager().registerThink(this, ms);
	}
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
	public abstract void spawn();
	
	public void think() {
	}
}
