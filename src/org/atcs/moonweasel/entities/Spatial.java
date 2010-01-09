package org.atcs.moonweasel.entities;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.gui.Loader;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public abstract class Spatial extends Entity {
	private String model;
	private int displayList;
	
	protected State oldState;
	protected State state;
	
	protected Spatial(float mass) {
		super();
		
		this.model = null;
		this.displayList = -1;
		
		this.oldState = new State(mass);
		this.state = new State(mass);
	}
	
	public void draw(GL2 gl) {
		assert displayList != -1;
		
		gl.glCallList(displayList);
	}
	
	public void precache(GL2 gl) {
		int list = gl.glGenLists(1);
		gl.glNewList(list, GL2.GL_COMPILE);

			if (model != null) {
				Loader.load(model, gl);			
			} else {
				draw(gl);
			}

		gl.glEndList();
	}
	
	public State getOldState() {
		return this.oldState;
	}
	
	public State getState() {
		return this.state;
	}
	
	public void setState(State newState) {
		this.oldState = this.state;
		this.state = newState;
	}
	
	public void teleport(Vector position) {
		this.state.position = position;
	}
}
