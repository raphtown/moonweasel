package org.atcs.moonweasel.entities;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Vector;

import com.sun.opengl.util.gl2.GLUT;

public class Laser extends ModelEntity
{
	
	private static final float VELOCITY = 100.0f;
	
	public Laser() {
		super(500, Matrix.IDENTITY);
	}

	@Override
	public void destroy()
	{
	}
	
	public void draw(GL2 gl)
	{
		GLUT glut = WeaselView.glut;
		gl.glPushAttrib(gl.GL_CURRENT_BIT);
		gl.glScalef(5, 5, 5);
		gl.glColor3f(0.0f, 0.1f, 0.9f);
		glut.glutSolidCylinder(0.02, 1.5, 30, 30);
		gl.glPopAttrib();
	}

	@Override
	public void spawn()
	{
		Vector speed = new Vector(0.0f, 0.0f, -VELOCITY);
		this.getState().momentum = this.getState().orientation.rotate(speed);
	}
}
