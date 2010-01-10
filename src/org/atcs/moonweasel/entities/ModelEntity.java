package org.atcs.moonweasel.entities;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.Positional;
import org.atcs.moonweasel.gui.Loader;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public abstract class ModelEntity extends Entity implements Positional {
	private final static Map<Class<? extends ModelEntity>, Integer> DISPLAY_LISTS;
	
	static {
		DISPLAY_LISTS = new HashMap<Class<? extends ModelEntity>, Integer>();
	}
	
	private int displayList;
	
	protected State oldState;
	protected State state;
	
	protected ModelEntity(float mass, Matrix inertiaTensor) {
		super();
		
		this.displayList = -1;
		
		this.oldState = new State(mass, inertiaTensor);
		this.state = new State(mass, inertiaTensor);
	}
	
	public void draw(GL2 gl) {
		assert DISPLAY_LISTS.containsKey(this.getClass());
		
		gl.glCallList(displayList);
	}
	
	public State getOldState() {
		return this.oldState;
	}
	
	public Vector getPosition() {
		return state.position;
	}
	
	public State getState() {
		return this.state;
	}
	
	public void precache(GL2 gl) {
		int list = gl.glGenLists(1);

		gl.glNewList(list, GL2.GL_COMPILE);
			if (!Loader.load(getEntityType(), gl)) {
				// Something blew up, assume draw is overridden.
				gl.glEndList();
				gl.glNewList(list, GL2.GL_COMPILE);
				draw(gl);
			}
		gl.glEndList();
		DISPLAY_LISTS.put(this.getClass(), list);
	}
	
	public void setState(State newState) {
		this.oldState = this.state;
		this.state = newState;
	}
	
	public void teleport(Vector position) {
		this.state.position = position;
	}
}
