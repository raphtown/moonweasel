package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.Identifiable;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public abstract class ParticleEntity extends Entity implements Identifiable {
	private static int nextID = 0;
	private static int getNextID() { 
		return nextID++;
	}
	
	private final int id;

	private State oldState;
	private State state;
	
	protected ParticleEntity() {
		this.id = getNextID();
		
		this.oldState = new State();
		this.state = new State();
	}
	
	public void teleport(Vector position) {
		this.state.position = position;
	}
}
