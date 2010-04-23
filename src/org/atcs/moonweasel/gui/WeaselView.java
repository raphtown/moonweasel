package org.atcs.moonweasel.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.AxisAngle;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

import com.sun.opengl.util.gl2.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

public class WeaselView extends View {
	private enum BaseTextures {
		WALL("dev_measuregeneric01.png"),
		NUM_TEXTURES(null);
		
		public final String filename;
		
		private BaseTextures(String filename) {
			this.filename = filename;
		}
	}
	
    /* OpenGL objects */
	private static GLU glu;
	public static GLUT glut;
	
	/* UI components */
	private ArrayList<UIElement> uiElements;
	
	/* Camera parameters */
	private static final double CAMERA_FOV_ANGLE = 60.0;		/* Camera (vertical) field of view angle */

	private static final double CAMERA_CLIPPING_NEAR = 0.1;
	private static final double CAMERA_CLIPPING_FAR = 10000;
	
	private static void drawCubeFace(TextureCoords tc, float radius, GL2 gl) {
		
    	gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(tc.left() * 5, tc.bottom() * 5);
	    	gl.glVertex3f(radius, -radius, 0.f);
			gl.glTexCoord2f(tc.right() * 5, tc.bottom() * 5);
	    	gl.glVertex3f(radius, radius, 0.f);
			gl.glTexCoord2f(tc.right() * 5, tc.top() * 5);
	    	gl.glVertex3f(-radius, radius, 0.f);
			gl.glTexCoord2f(tc.left() * 5, tc.top() * 5);
	    	gl.glVertex3f(-radius, -radius, 0.f);
    	gl.glEnd();
    }
	
	/* Window size*/
	private int width;
	private int height;
	
	private Texture[] textures;
	
    /* Game objects */
    private Player me;
	
	public WeaselView(int w, int h, boolean fullscreen, Player p) {
		super(w, h, fullscreen);

		width = w;
		height = h;
		
		textures = new Texture[BaseTextures.NUM_TEXTURES.ordinal()];
		
		me = p;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		glu = new GLU();
		glut = new GLUT();

		for (int i = 0; i < textures.length; i++) {
			try {
				textures[i] = TextureIO.newTexture(
						new File("data/textures/" + BaseTextures.values()[i].filename),
						false);
			} catch (IOException e) {
				throw new RuntimeException("Unable to load texture " + 
						BaseTextures.values()[i].name(), e);
			}

			textures[i].setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
			textures[i].setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		}
		
		textures[BaseTextures.WALL.ordinal()].setTexParameteri(
				GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		textures[BaseTextures.WALL.ordinal()].setTexParameteri(
				GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        /* Set viewport */
        gl.glViewport(0, 0, width, height);
        
        /* Set up lighting */
        setUpLighting(gl);
        
        /* Enable things */
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_SMOOTH);
        
        initComponents();
        
//        Image cursorImage = Toolkit.getDefaultToolkit().getImage("xparent.gif");
//        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point( 0, 0), "" );
//        setCursor( blankCursor );
        
        
	}
	
	public void initComponents()
	{
		uiElements = new ArrayList<UIElement>();
		uiElements.add(new HealthBar(new Vector(10, 10, 0), me));
		uiElements.add(new Crosshairs(new Vector(width/2, height/2, 0)));
	}
	
	private void setUpLighting(GL2 gl)
	{
        gl.glEnable(GL2.GL_LIGHT0);
        
        float[] lamb = { 0.8f, 0.8f, 0.8f, 1.0f };
//        float[] ldiff = { 0.6f, 0.6f, 0.6f, 1.0f };
//        float[] lspec = { 0.4f, 0.4f, 0.4f, 1.0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lamb, 0);
//        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, ldiff, 0);
//        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lspec, 0);
        
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
        
        Ship ent = me.getShip();
        
        State interp = State.interpolate(ent.getLastRenderState(), ent.getState(), alpha);
//        Vector relative = interp.orientation.rotate(
//        		new Vector(0, radius*CAMERA_PILOT_OFFSET_SCALAR, radius * CAMERA_PILOT_OFFSET_SCALAR));
        
        Vector relative = interp.orientation.rotate(
        		new Vector(ent.getData().cameraPosOffset.x, ent.getData().cameraPosOffset.y, ent.getData().cameraPosOffset.z));
        Vector look = interp.orientation.rotate(
        		new Vector(ent.getData().cameraLookOffset.x, ent.getData().cameraLookOffset.y, ent.getData().cameraLookOffset.z));
        
        Vector cameraPos = interp.position.add(relative);
        Vector cameraLook = interp.position.add(look);
        Vector up = interp.orientation.rotate(new Vector(0, 1, 0));

        glu.gluLookAt(
        		cameraPos.x, cameraPos.y, cameraPos.z,
        		cameraLook.x, cameraLook.y, cameraLook.z,
        		up.x, up.y, up.z);
    }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
        drawable.getGL().getGL2().glViewport(0, 0, width, height);
	}
	
	@Override
	public void display(GL2 gl, float alpha) {
		setProjection(gl, alpha);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	       
        gl.glClearColor(0.47f, 0.53f, 0.67f, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        gl.glPushAttrib(GL2.GL_DEPTH_BUFFER_BIT);
        	gl.glDisable(GL2.GL_LIGHTING);
        	gl.glEnable(GL2.GL_TEXTURE_2D);
        	Texture tex = textures[BaseTextures.WALL.ordinal()];
        	TextureCoords tc = tex.getImageTexCoords();
        	tex.enable();
        	tex.bind();
        	
        	gl.glPushMatrix();
	        	gl.glTranslatef(0, 0, 2000.f);
	        	drawCubeFace(tc, 2000.f, gl);
        	gl.glPopMatrix();
        	gl.glPushMatrix();
	        	gl.glTranslatef(2000.f, 0, 0);
	        	gl.glRotatef(90, 0, 1, 0);
	        	drawCubeFace(tc, 2000.f, gl);
	        gl.glPopMatrix();
	    	gl.glPushMatrix();
		    	gl.glTranslatef(-2000.f, 0, 0);
		    	gl.glRotatef(90, 0, 1, 0);
		    	drawCubeFace(tc, 2000.f, gl);
		    gl.glPopMatrix();
			gl.glPushMatrix();
				gl.glTranslatef(0, 0, -2000.f);
				gl.glRotatef(0, 0, 1, 0);
				drawCubeFace(tc, 2000.f, gl);
			gl.glPopMatrix();
        	
        	gl.glDisable(GL2.GL_TEXTURE_2D);
        	gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopAttrib();
        	
        
        EntityManager em = EntityManager.getEntityManager();
        State interpolated;
        AxisAngle rotation;
        for (ModelEntity entity : em.getAllOfType(ModelEntity.class)) {
        	if (!entity.isPreCached()) {
        		entity.precache(gl);
        	}
        	
        	interpolated = State.interpolate(entity.getLastRenderState(), entity.getState(), 
        			alpha);
        	entity.setLastRenderState(entity.getState().clone());
        	rotation = interpolated.orientation.toAxisAngle();
        	
            gl.glPushMatrix();
	        	gl.glTranslatef(interpolated.position.x, interpolated.position.y,
	        			interpolated.position.z);
	        	if (!rotation.axis.equals(Vector.ZERO)) {
		        	gl.glRotated(Math.toDegrees(rotation.angle), rotation.axis.x, rotation.axis.y,
		        			rotation.axis.z);
	        	}
	        	entity.draw(gl);
        	gl.glPopMatrix();
        }
        
        for (ParticleEntity entity : em.getAllOfType(ParticleEntity.class)) {
        	rotation = entity.getOrientation().toAxisAngle();
        	
            gl.glPushMatrix();
	        	gl.glTranslatef(entity.getPosition().x, entity.getPosition().y,
	        			entity.getPosition().z);
	        	if (!rotation.axis.equals(Vector.ZERO)) {
		        	gl.glRotated(Math.toDegrees(rotation.angle), rotation.axis.x, rotation.axis.y,
		        			rotation.axis.z);
	        	}
	        	entity.draw(gl);
        	gl.glPopMatrix();
        }
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
   		gl.glPushMatrix();
   		gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
   			gl.glDisable(GL2.GL_LIGHTING);
   			gl.glLoadIdentity();
   			glu.gluOrtho2D(0, width, 0, height);
   			gl.glMatrixMode(GL2.GL_MODELVIEW);
   			gl.glLoadIdentity();
   			
   			for(UIElement e : uiElements)
   			{
   				gl.glPushMatrix();
   					gl.glTranslated(e.pos.x,e.pos.y,e.pos.z);
   					e.draw(gl);
   				gl.glPopMatrix();
   			}
   		gl.glPopAttrib();
   		gl.glMatrixMode(GL2.GL_PROJECTION);

   		gl.glPopMatrix();
   		
        gl.glFlush();
	}
}
