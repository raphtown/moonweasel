package org.atcs.moonweasel.entities;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.physics.BoundingBox;
import org.atcs.moonweasel.physics.BoundingShape;
import org.atcs.moonweasel.util.Matrix;

import com.sun.opengl.util.gl2.GLUT;

public class Laser extends ModelEntity
{
	private Laser() {
		super(new BoundingBox(10, 10, 10), 500, Matrix.IDENTITY);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void destroy()
	{
		
		
	}
	
	public void draw(GL2 gl)
	{
		GLUT glut = WeaselView.glut;
		gl.glPushAttrib(gl.GL_CURRENT_BIT);
		gl.glColor3f(0.0f, 0.1f, 0.9f);
		glut.glutSolidCylinder(0.02, 1.5, 30, 30);
		gl.glPopAttrib();
	}

	@Override
	public void spawn()
	{
		
		
	}
}
