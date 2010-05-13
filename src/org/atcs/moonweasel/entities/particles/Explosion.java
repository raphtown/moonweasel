package org.atcs.moonweasel.entities.particles;

import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.util.Quaternion;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class Explosion extends ParticleEntity {
	private int age;
	private final int MAX_RADIUS = 1;
	private final int MAX_LIFE = 50;
	
	public Explosion() {
	}
	
	public void draw() {
		float factor = (float)age / MAX_LIFE;
		
		//float[] amb = {1.0f, 0.8f * factor, 0.0f, (0.8f * (1 - factor))};
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    		Sphere s = new Sphere();
			s.draw(factor * MAX_RADIUS, 30, 30);
			GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopAttrib();
	}
	
	public void spawn() {
		age = 25;
		setOrientation(Quaternion.ZERO);
	}
}
