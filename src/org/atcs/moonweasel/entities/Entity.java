package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.Identifiable;

public abstract class Entity implements Identifiable {
	private static int nextID = 0;
	private static int getNextID() { 
		return nextID++;
	}
	
	public static String getEntityType(Class<? extends Entity> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	private final int id;
	
	protected Entity() {
		this.id = getNextID();
	}
	
	public abstract void destroy();
	public abstract void spawn();
	
	public <T extends Entity> T createEntity(String type) {
		return EntityManager.getEntityManager().create(type);
	}

	public final int getID() {
		return this.id;
	}
	
	public final String getEntityType() {
		return this.getClass().getSimpleName().toLowerCase();
	}
	
	protected void scheduleThink(int ms) {
		EntityManager.getEntityManager().registerThink(this, ms);
	}
	
	public void think() {
	}
}
