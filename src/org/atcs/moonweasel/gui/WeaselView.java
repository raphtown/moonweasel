package org.atcs.moonweasel.gui;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.physics.BoundingBox;
import org.atcs.moonweasel.physics.BoundingShape;
import org.atcs.moonweasel.physics.BoundingSphere;
import org.atcs.moonweasel.util.AxisAngle;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public class WeaselView extends View {
	/* Window size*/
	private int width;
	private int height;
	
    /* OpenGL objects */
	private static GLU glu;
	
	/* Camera parameters */
	public static final double CAMERA_FOV_ANGLE = 60.0;		/* Camera (vertical) field of view angle */
	public static final float CAMERA_PILOT_OFFSET_SCALAR = 1.5f;
	public static final double CAMERA_CLIPPING_NEAR = 0.1;
	public static final double CAMERA_CLIPPING_FAR = 500;
	public static final double CAMERA_WALL_OFFSET = 0.5;
	
    /* Game objects */
    private Player me;
	
	public WeaselView(int w, int h, boolean fullscreen, Player p) {
		super(w, h, fullscreen);

		width = w;
		height = h;
		
		me = p;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		glu = new GLU();
		
		/* Set up texture options */
//		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        
        /* Set viewport */
        gl.glViewport(0, 0, width, height);
        
        /* Set up lighting */
        setUpLighting(gl);
        
        /* Enable things */
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_SMOOTH);
	}
	
	private void setUpLighting(GL2 gl)
	{
        gl.glEnable(GL2.GL_LIGHT0);
        
        float[] lamb = { 0.8f, 0.8f, 0.8f, 1.0f };
        float[] ldiff = { 0.6f, 0.6f, 0.6f, 1.0f };
        float[] lspec = { 0.4f, 0.4f, 0.4f, 1.0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lamb, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, ldiff, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lspec, 0);
        
        float[] pos = {-9, -9, 10, 1};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
        
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, 0);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_QUADRATIC_ATTENUATION, 0.005f);
       
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_TRUE);
        float[] amb = { 0.4f, 0.4f, 0.4f, 1.0f };
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, amb, 0);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
	}
	
	private void setProjection(GL2 gl, float alpha)
	{
		gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        /* Specify perspective transformation */
        glu.gluPerspective(
        		CAMERA_FOV_ANGLE,
        		((float) width) / ((float) height),
        		CAMERA_CLIPPING_NEAR,
        		CAMERA_CLIPPING_FAR);
        
        ModelEntity ent = me.getShip();
        BoundingShape shape = ent.getBoundingShape();
        float radius;
        if (shape instanceof BoundingBox) {
        	radius = ((BoundingBox)shape).maxZ;
        } else if (shape instanceof BoundingSphere) {
        	radius = ((BoundingSphere)shape).radius;
        } else {
        	throw new RuntimeException(String.format(
        			"Unknown bounding shape of type %s.", 
        			shape.getClass().getName()));
        }
        
        State interp = State.interpolate(ent.getOldState(), ent.getState(), alpha);
        Vector relative = interp.orientation.rotate(
        		new Vector(0, 0, -radius * CAMERA_PILOT_OFFSET_SCALAR));
        Vector camera = interp.position.add(relative);
        
        glu.gluLookAt(
        		camera.x, camera.y, camera.z,
        		interp.position.x, interp.position.y, interp.position.z,
        		0, 1, 0);
    }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void display(GL2 gl, float alpha) {
		setProjection(gl, alpha);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	       
        gl.glClearColor(0.47f, 0.53f, 0.67f, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        EntityManager em = EntityManager.getEntityManager();
        State interpolated;
        AxisAngle rotation;
        for (ModelEntity entity : em.getAllOfType(ModelEntity.class)) {
        	if (!entity.isPreCached()) {
        		entity.precache(gl);
        	}
        	
        	interpolated = State.interpolate(entity.getOldState(), entity.getState(), 
        			alpha);
        	rotation = interpolated.orientation.toAxisAngle();
        	
            gl.glPushMatrix();
	        	gl.glTranslatef(interpolated.position.x, interpolated.position.y,
	        			interpolated.position.z);
	        	
	        	if (!rotation.equals(Vector.ZERO)) {
		        	gl.glRotatef(rotation.angle, rotation.axis.x, rotation.axis.y,
		        			rotation.axis.z);	        		
	        	}
	        	entity.draw(gl);
        	gl.glPopMatrix();
        }
        
        gl.glFlush();
	}
}
