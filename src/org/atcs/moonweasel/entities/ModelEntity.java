package org.atcs.moonweasel.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.gui.Loader;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.TimedDerivative;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public abstract class ModelEntity extends Entity implements Positional {
	public final static Map<Class<? extends ModelEntity>, Integer> DISPLAY_LISTS;
	
	static {
		DISPLAY_LISTS = new HashMap<Class<? extends ModelEntity>, Integer>();
	}
	
	protected State lastRenderState;
	protected State state;
	
	protected ModelEntity(float mass, Matrix inertiaTensor) 
	{
		super();
		
		this.lastRenderState = new State(mass, inertiaTensor);
		this.state = new State(mass, inertiaTensor);
		
	
	}

	
	public void collidedWith(ModelEntity other) {
	}
	
	public void draw(GL2 gl) {
		assert DISPLAY_LISTS.containsKey(this.getClass());
		
		gl.glCallList(DISPLAY_LISTS.get(this.getClass()));
	}
	
	
	public State getLastRenderState() {
		return this.lastRenderState;
	}
	
	public Vector getPosition() {
		return this.state.position;
	}
	
	public State getState() {
		return this.state;
	}
	
	public boolean isPreCached() {
		return DISPLAY_LISTS.containsKey(this.getClass());
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
	
	public void setLastRenderState(State state) {
		this.lastRenderState = state;
	}
	
	protected void setVelocity(Vector velocity) {
		this.state.velocity = velocity;
	}

	
	public void setPosition(Vector position) {
		this.state.position = position;
	}
}
