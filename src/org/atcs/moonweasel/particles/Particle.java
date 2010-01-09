package org.atcs.moonweasel.particles;

import org.atcs.moonweasel.Identifiable;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public class Particle implements Identifiable {
	private static int nextID = 0;
	private static int getNextID() { 
		return nextID++;
	}
	
	private final int id;

	private State oldState;
	private State state;
	
	protected Particle() {
		this.id = getNextID();
		
		this.oldState = new State();
		this.state = new State();
	}
	
	public final int getID() {
		return this.id;
	}
	
	public State getOldState() {
		return oldState;
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State newState) {
		this.oldState = this.state;
		this.state = newState;
	}
	
	public void teleport(Vector position) {
		this.state.position = position;
	}
}
