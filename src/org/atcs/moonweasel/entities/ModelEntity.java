package org.atcs.moonweasel.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.atcs.moonweasel.gui.Loader;
import org.atcs.moonweasel.networking.IState;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;

public abstract class ModelEntity extends Entity implements Positional {
	private static final long serialVersionUID = 8399004035748490715L;
	public final static Map<Class<? extends ModelEntity>, Integer> DISPLAY_LISTS;
	
	static {
		DISPLAY_LISTS = new HashMap<Class<? extends ModelEntity>, Integer>();
	}
	
	protected State lastRenderState;
	protected State state;
	
	protected ModelEntity(float mass, Matrix inertiaTensor) 
	{
		super();

		this.lastRenderState = new State(this, mass, inertiaTensor);
		this.state = new State(this, mass, inertiaTensor);

		this.addChange("create modelentity " + mass + " " + inertiaTensor);
	}

	
	public void collidedWith(ModelEntity other) {
		System.out.println("collidedWith method called");
	}
	
	public void draw() {
		assert DISPLAY_LISTS.containsKey(this.getClass());
		
		GL11.glCallList(DISPLAY_LISTS.get(this.getClass()));
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
	
	public void precache() {
		int list = GL11.glGenLists(1);

		GL11.glNewList(list, GL11.GL_COMPILE);
			if (!Loader.load(getEntityType())) {
				// Something blew up, assume draw is overridden.
				GL11.glEndList();
				GL11.glNewList(list, GL11.GL_COMPILE);
				draw();
			}
		GL11.glEndList();
		DISPLAY_LISTS.put(this.getClass(), list);
	}
	
	public void setLastRenderState(State state) {
		this.lastRenderState = state;
	}
	
	public void setVelocity(Vector velocity) {
		this.state.velocity = velocity;
		this.addChange("set velocity " + velocity);
	}

	
	public void setPosition(Vector position) {
		this.state.position = position;
		this.addChange("set position " + position);
	}
	
	public void setState(State s) 
	{
		state = s;
	}
	
	
	public void unpackageIState(IState is)
	{
		State state = this.state;
		List<Object> objects = is.objects;
		Iterator<Object> iter = objects.iterator();
		state.position = (Vector) iter.next();
		iter.remove();
		state.momentum = (Vector) iter.next();
		iter.remove();
		state.orientation = (Quaternion) iter.next();
		iter.remove();
		state.angularMomentum = (Vector) iter.next();
		iter.remove();
		state.recalculate();
	}
	
	public IState packageIState()
	{
		State s = this.state;
		List<Object> objects = new ArrayList<Object>();
		objects.add(s.position);
		objects.add(s.momentum);
		objects.add(s.orientation);
		objects.add(s.angularMomentum);
		return new IState(objects, getID());
	}
}
