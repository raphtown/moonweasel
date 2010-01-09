package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.Identifiable;

public abstract class Entity implements Identifiable {
	private static int nextID = 0;
	private static int getNextID() { 
		return nextID++;
	}
	
	private final int id;
	
	protected Entity() {
		this.id = getNextID();
	}
	
	public abstract void destroy();
	public abstract void spawn();

	public final int getID() {
		return this.id;
	}
	
	protected void scheduleThink(int ms) {
		
	}
	
	public void think() {
	}
}
