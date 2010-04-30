package org.atcs.moonweasel.gui;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.AxisAngle;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class WeaselView extends View {
	private enum BaseTextures {
		WALL("dev_measuregeneric01.png"), NUM_TEXTURES(null);

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
	private static final float CAMERA_CLIPPING_FAR = 10000;

	private static void drawCubeFace(Texture texture, float radius) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(radius, -radius, 0.f);
		GL11.glTexCoord2f(0, texture.getHeight() * 5);
		GL11.glVertex3f(radius, radius, 0.f);
		GL11.glTexCoord2f(texture.getWidth() * 5, texture.getHeight() * 5);
		GL11.glVertex3f(-radius, radius, 0.f);
		GL11.glTexCoord2f(texture.getWidth() * 5, 0);
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

		/* Set up lighting */
		setUpLighting();

		/* Enable things */
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		initComponents();
	}

	public void initComponents() {
		uiElements = new ArrayList<UIElement>();
		uiElements.add(new HealthBar(new Vector(10, 10, 0), me));
		uiElements.add(new Crosshairs(new Vector(mode.getWidth() / 2, mode
				.getHeight() / 2, 0)));
	}

	private void setUpLighting() {
		GL11.glEnable(GL11.GL_LIGHT0);

		FloatBuffer lamb = BufferUtils.createFloatBuffer(4);
		lamb.put(new float[] { 0.8f, 0.8f, 0.8f,
				1.0f });
		lamb.flip();
		// float[] ldiff = { 0.6f, 0.6f, 0.6f, 1.0f };
		// float[] lspec = { 0.4f, 0.4f, 0.4f, 1.0f };
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lamb);
		// GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, ldiff, 0);
		// GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, lspec, 0);

		FloatBuffer pos = BufferUtils.createFloatBuffer(4);
		pos.put(new float[] { -9, -9, 10, 1 });
		pos.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, pos);

		GL11.glLightf(GL11.GL_LIGHT0, GL11.GL_CONSTANT_ATTENUATION, 0);
		GL11.glLightf(GL11.GL_LIGHT0, GL11.GL_QUADRATIC_ATTENUATION, 0.005f);

		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, GL11.GL_TRUE);
		FloatBuffer amb = BufferUtils.createFloatBuffer(4);
		amb.put(new float[] { 0.4f, 0.4f, 0.4f, 1.0f });
		amb.flip();
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, amb);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
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
		// Vector relative = interp.orientation.rotate(
		// new Vector(0, radius*CAMERA_PILOT_OFFSET_SCALAR, radius *
		// CAMERA_PILOT_OFFSET_SCALAR));

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

	public void render(float alpha) {
		setProjection(alpha);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glClearColor(0.47f, 0.53f, 0.67f, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Texture texture = textures[BaseTextures.WALL.ordinal()];
		texture.bind();

		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, 2000.f);
		drawCubeFace(texture, 2000.f);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(2000.f, 0, 0);
		GL11.glRotatef(90, 0, 1, 0);
		drawCubeFace(texture, 2000.f);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(-2000.f, 0, 0);
		GL11.glRotatef(90, 0, 1, 0);
		drawCubeFace(texture, 2000.f);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, -2000.f);
		GL11.glRotatef(0, 0, 1, 0);
		drawCubeFace(texture, 2000.f);
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
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
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
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
		GL11.glPopAttrib();
		GL11.glMatrixMode(GL11.GL_PROJECTION);

		GL11.glPopMatrix();

		GL11.glFlush();
		Display.update();
	}
}
