package org.atcs.moonweasel.gui;

import javax.media.opengl.GL;

import org.atcs.moonweasel.util.Vector;

public abstract class UIElement {

	public Vector pos;
	
	public UIElement(Vector p)
	{
		pos = p;
	}
	
	public abstract void draw(GL gl);
}
