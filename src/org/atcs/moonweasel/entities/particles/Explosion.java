package org.atcs.moonweasel.entities.particles;

import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.util.Quaternion;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class Explosion extends ParticleEntity {
	private static final long serialVersionUID = 7516058388917314934L;
	private int age;
	private final float MAX_RADIUS = 1.0f;
	private final int MAX_LIFE = 50;
	
	public Explosion() {
	}
	
	public void draw() {
		float factor = (float)age / MAX_LIFE;
		
		float[] amb = {1.0f, 0.8f * factor, 0.0f, (0.8f * (1 - factor))};
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			GL11.glColor3f(amb[0], amb[1], amb[2]);
			Sphere s = new Sphere();
        	s.draw(factor * MAX_RADIUS, 30, 30);
        GL11.glPopAttrib();
	}
	
	public void spawn() {
		age = 0;
		setOrientation(Quaternion.ZERO);
		scheduleThink(50);
	}
	
	public void think() {
		age++;
		
		if (age < MAX_LIFE) {
			this.scheduleThink(50);			
		} else {
			destroy();
		}
	}
}
