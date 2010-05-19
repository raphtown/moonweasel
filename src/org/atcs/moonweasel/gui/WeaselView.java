package org.atcs.moonweasel.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.AxisAngle;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class WeaselView extends View {
	private enum BaseTextures {
		WALL("starfield2.png"), NUM_TEXTURES(null);

		public final String filename;

		private BaseTextures(String filename) {
			this.filename = filename;
		}
	}

	/* UI components */
	private ArrayList<UIElement> uiElements;

	/* Camera parameters */
	private static final float CAMERA_FOV_ANGLE = 60.0f; /*
														  * Camera (vertical)
														  * field of view angle
														  */
	private static final float CAMERA_CLIPPING_NEAR = 0.1f;
	private static final float CAMERA_CLIPPING_FAR = 500000000;


	private static void drawCubeFace(Texture texture, float radius) {

		final float TILE_CONSTANT = 2;
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(radius, -radius, 0.f);
		GL11.glTexCoord2f(0, texture.getHeight() * TILE_CONSTANT);
		GL11.glVertex3f(radius, radius, 0.f);
		GL11.glTexCoord2f(texture.getWidth() * TILE_CONSTANT, texture.getHeight() * TILE_CONSTANT);
		GL11.glVertex3f(-radius, radius, 0.f);
		GL11.glTexCoord2f(texture.getWidth() * TILE_CONSTANT, 0);
		GL11.glVertex3f(-radius, -radius, 0.f);
		GL11.glEnd();
	}

	private Texture[] textures;

	/* Game objects */
	private Player me;
	
	public WeaselView(DisplayMode mode, boolean fullscreen, Player p) {
		super(mode, fullscreen);

		textures = new Texture[BaseTextures.NUM_TEXTURES.ordinal()];
		me = p;

		init();
	}

	private void init() {
		TextureLoader loader = new TextureLoader();
		for (int i = 0; i < textures.length; i++) {
			try {
				textures[i] = loader.getTexture("data/textures/"
						+ BaseTextures.values()[i].filename);
			} catch (IOException e) {
				throw new RuntimeException("Unable to load texture "
						+ BaseTextures.values()[i].name(), e);
			}
		}

		/* Set viewport */
		GL11.glViewport(0, 0, mode.getWidth(), mode.getHeight());

		/* Enable things */
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		initComponents();
	}

	public void initComponents() {
		uiElements = new ArrayList<UIElement>();
		uiElements.add(new HealthBar(new Vector(10, 10, 0), me));
		uiElements.add(new Crosshairs(new Vector(mode.getWidth() / 2, mode
				.getHeight() / 2, 0)));
		uiElements.add(new Radar(new Vector(350, 250, 0), me));
	}

	private void setProjection(float alpha) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		/* Specify perspective transformation */
		GLU.gluPerspective(CAMERA_FOV_ANGLE, ((float) mode.getWidth())
				/ ((float) mode.getHeight()), CAMERA_CLIPPING_NEAR,
				CAMERA_CLIPPING_FAR);

		Ship ent = me.getShip();

		State interp = State.interpolate(ent.getLastRenderState(), ent
				.getState(), alpha);

		Vector relative = interp.orientation.rotate(new Vector(
				ent.getData().cameraPosOffset.x,
				ent.getData().cameraPosOffset.y,
				ent.getData().cameraPosOffset.z));
		Vector look = interp.orientation.rotate(new Vector(
				ent.getData().cameraLookOffset.x,
				ent.getData().cameraLookOffset.y,
				ent.getData().cameraLookOffset.z));

		Vector cameraPos = interp.position.add(relative);
		Vector cameraLook = interp.position.add(look);
		Vector up = interp.orientation.rotate(new Vector(0, 1, 0));

		GLU.gluLookAt(cameraPos.x, cameraPos.y, cameraPos.z, cameraLook.x,
				cameraLook.y, cameraLook.z, up.x, up.y, up.z);
	}

	@Override
	public void render(float alpha) {
		
		setProjection(alpha);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glClearColor(0.47f, 0.53f, 0.67f, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Texture texture = textures[BaseTextures.WALL.ordinal()];
		texture.bind();

		final float BG_DISTANCE = 3750.f;
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, BG_DISTANCE);
		drawCubeFace(texture, BG_DISTANCE);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(BG_DISTANCE, 0, 0);
		GL11.glRotatef(90, 0, 1, 0);
		drawCubeFace(texture, BG_DISTANCE);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(-BG_DISTANCE, 0, 0);
		GL11.glRotatef(90, 0, 1, 0);
		drawCubeFace(texture, BG_DISTANCE);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, -BG_DISTANCE);
		GL11.glRotatef(0, 0, 1, 0);
		drawCubeFace(texture, BG_DISTANCE);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -BG_DISTANCE, 0);
		GL11.glRotatef(90, 1, 0, 0);
		drawCubeFace(texture, BG_DISTANCE);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(0, BG_DISTANCE, 0);
		GL11.glRotatef(90, 1, 0, 0);
		drawCubeFace(texture, BG_DISTANCE);
		GL11.glPopMatrix();
		texture.unbind();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPopAttrib();

		EntityManager em = EntityManager.getEntityManager();
		State interpolated;
		AxisAngle rotation;
		for (ModelEntity entity : em.getAllOfType(ModelEntity.class)) {
			if (!entity.isPreCached()) {
				entity.precache();
			}

			interpolated = State.interpolate(entity.getLastRenderState(),
					entity.getState(), alpha);
			entity.setLastRenderState(entity.getState().clone());
			rotation = interpolated.orientation.toAxisAngle();

			GL11.glPushMatrix();
			GL11.glTranslatef(interpolated.position.x, interpolated.position.y,
					interpolated.position.z);
			if (!rotation.axis.equals(Vector.ZERO)) {
				GL11.glRotatef((float) Math.toDegrees(rotation.angle),
						rotation.axis.x, rotation.axis.y, rotation.axis.z);
			}
			entity.draw();
			GL11.glPopMatrix();
		}

        for (ParticleEntity entity : em.getAllOfType(ParticleEntity.class)) {
        	if (entity == null || entity.getOrientation() == null)
        		continue;
        	rotation = entity.getOrientation().toAxisAngle();
        	
            GL11.glPushMatrix();
	        	GL11.glTranslatef(entity.getPosition().x, entity.getPosition().y,
	        			entity.getPosition().z);
	        	if (!rotation.axis.equals(Vector.ZERO)) {
		        	GL11.glRotatef((float)Math.toDegrees(rotation.angle), rotation.axis.x, rotation.axis.y,
		        			rotation.axis.z);
	        	}
	        	entity.draw();
        	GL11.glPopMatrix();
        }
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0, mode.getWidth(), 0, mode.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		for (UIElement e : uiElements) {
			GL11.glPushMatrix();
			GL11.glTranslated(e.pos.x, e.pos.y, e.pos.z);
			e.draw();
			GL11.glPopMatrix();
		}
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		
		GL11.glFlush();
		Display.update();
	}
	
	public static void drawDisk(double r, int n)
	{
		double ts = 2 * Math.PI / (double) n;
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex2i(0, 0);
			
			for(int x = 0; x < n; x++)
				GL11.glVertex2d(r * Math.cos(ts * x), r * Math.sin(ts * x));
			
			GL11.glVertex2d(r, 0);	// Close circle
			GL11.glEnd();
	}
	
	public static void drawCircle(double r, int n)
	{
		double ts = 2 * Math.PI / (double) n;
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
			for(int x = 0; x < n; x++)
				GL11.glVertex2d(r * Math.cos(ts * x), r * Math.sin(ts * x));
		GL11.glEnd();
	}
}
