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
	
	protected Entity() {
		this.id = getNextID();
	}
	
	public <T extends Entity> T createEntity(String type) {
		return EntityManager.getEntityManager().create(type);
	}
	public abstract void destroy();
	
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
