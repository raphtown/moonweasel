package org.atcs.moonweasel.gui;




import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;

import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;



import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import org.atcs.moonweasel.util.Vector;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;

import com.sun.opengl.util.gl2.GLUT;


public class WeaselView extends View {
	private float value;
	
	/* Window size*/
	private int width;
	private int height;
	
    /* OpenGL objects */
	public static GLU glu;
	public static GLUT glut;
	
	/* Camera parameters */
	public static final double CAMERA_FOV_ANGLE = 60.0;		/* Camera (vertical) field of view angle */
	public static final double CAMERA_OFFSET = 8.0;		/* Offset (not quite distance) between camera and tank */
	public static final double CAMERA_HEIGHT = 2.0;		/* Height of camera above tank (almost) */
	public static final double CAMERA_TARGET_HEIGHT = 1.0;		/* Height of point camera points at above tank (almost) */
	public static final double CAMERA_SINKAGE = 0.5;	/* Controls amount camera drops when tilted upwards */
	public static final double CAMERA_CLIPPING_NEAR = 0.1;
	public static final double CAMERA_CLIPPING_FAR = 500;
	public static final double CAMERA_WALL_OFFSET = 0.5;
	
    /* Game objects */
    private Player me;
    private Ship myShip;
    
	/* Last known position and orientation */
	private Vector savedpos;
	private double savedzrot;
	private double savedtalt;
	private double cameraXtemp;
	private double cameraYtemp;
	private double cameraZtemp;
	
	public WeaselView(int w, int h, boolean fullscreen, Player p) {
		super(w, h, fullscreen);

		this.value = 0;
		width = w;
		height = h;
		
		me = p;
		myShip = p.getShip();
		
		savedpos = new Vector(0, 0, 0);
		savedzrot = 0;
		savedtalt = 0;
		

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
//		gl.setSwapInterval(1);
//		
//		gl.glMatrixMode(GL2.GL_PROJECTION);
//		gl.glLoadIdentity();
//		gl.glEnable(GL2.GL_DEPTH_TEST);
//		gl.glOrtho(-5, 5, -5, 5, 0.01, 10);
//
//		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		glu = new GLU();
		glut = new GLUT();
		
		/* Set up texture options */
//		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        
        /* Load textures */
//        loadTextures(gl);
        
        /* Set viewport */
        gl.glViewport(0, 0, width, height);
        
        /* Set up lighting */
        setUpLighting(gl);
        
        /* Enable things */
        gl.glEnable(gl.GL_LIGHTING);
        gl.glDepthFunc(gl.GL_LEQUAL);
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glShadeModel(gl.GL_SMOOTH);
		
	}
	
	private void setUpLighting(GL2 gl)
	{
        gl.glEnable(gl.GL_LIGHT0);
        
        float[] lamb = { 0.8f, 0.8f, 0.8f, 1.0f };
        float[] ldiff = { 0.6f, 0.6f, 0.6f, 1.0f };
        float[] lspec = { 0.4f, 0.4f, 0.4f, 1.0f };
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_AMBIENT, lamb, 0);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, ldiff, 0);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_SPECULAR, lspec, 0);
        
        float[] pos = {-9, -9, 10, 1};
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_POSITION, pos, 0);
        
        gl.glLightf(gl.GL_LIGHT0, gl.GL_CONSTANT_ATTENUATION, 0);
        gl.glLightf(gl.GL_LIGHT0, gl.GL_QUADRATIC_ATTENUATION, 0.005f);
       
        gl.glLightModeli(gl.GL_LIGHT_MODEL_LOCAL_VIEWER, gl.GL_TRUE);
        float[] amb = { 0.4f, 0.4f, 0.4f, 1.0f };
        gl.glLightModelfv(gl.GL_LIGHT_MODEL_AMBIENT, amb, 0);
        gl.glLightModeli(gl.GL_LIGHT_MODEL_TWO_SIDE, gl.GL_TRUE);
	}
	
	private void setProjection(GL2 gll)
	{
		GL2 gl = gll;
		gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        /* Specify perspective transformation */
        glu.gluPerspective(
        		CAMERA_FOV_ANGLE,
        		((float) width) / ((float) height),
        		CAMERA_CLIPPING_NEAR,
        		CAMERA_CLIPPING_FAR);
        
        /* Specify camera position and orientation */
        double tx = savedpos.x;
        double ty = savedpos.y;
        double sinTurretRotation = Math.sin(savedzrot);
        double cosTurretRotation = Math.cos(savedzrot);
        double cameraOffsetZ = CAMERA_SINKAGE * CAMERA_OFFSET * Math.tan(savedtalt);
        double cameraX = tx - (CAMERA_OFFSET * cosTurretRotation);
        double cameraY = ty - (CAMERA_OFFSET * sinTurretRotation);
        double cameraZ = CAMERA_HEIGHT - cameraOffsetZ;
        
        cameraXtemp = cameraX;
        cameraYtemp = cameraY;
        cameraZtemp = cameraZ;
        
        glu.gluLookAt(
        		cameraX,
        		cameraY,
        		cameraZ,
        		tx + cosTurretRotation,
        		ty + sinTurretRotation,
        		CAMERA_HEIGHT + cameraOffsetZ,
        		0, 1, 0);
    }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void display(GL2 gl, float alpha) {
//		gl.glClearColor(0.2f + 2 * (float)Math.sin(value) / 3, 0.2f, 0.2f, 0);
//		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
//		gl.glLoadIdentity();
//		
//		value += 0.01;
//		gl.glTranslatef(0, 2 * (float)Math.sin(value), 0);
//		
//		gl.glBegin(GL.GL_TRIANGLES);
//			gl.glVertex3f(2, 0, -3);
//			gl.glVertex3f(0, 2, -3);
//			gl.glVertex3f(-2, 0, -3);
//		gl.glEnd();
		
		savedpos = myShip.getPosition();
//		savedzrot = t.getTurretZRot();
//		savedtalt = t.getTurretAltitude();
		
		setProjection(gl);
		
		gl.glMatrixMode(gl.GL_MODELVIEW);
		gl.glLoadIdentity();
	       
        gl.glClearColor(0, 0.5f, 0.5f, 0);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        gl.glTranslatef(myShip.getPosition().x, myShip.getPosition().y, myShip.getPosition().z);
        myShip.draw(gl);
        
        gl.glFlush();
	}
}
