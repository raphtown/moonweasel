package org.atcs.moonweasel.gui;

import org.atcs.moonweasel.entities.Asteroid;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.ranges.TypeRange;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;

public class Radar extends UIElement {

	private static final int MINI_MAP_RADIUS = 75;
	private static final int TANK_CIRCLE_RADIUS = 5;
	private static final int DEFAULT_RADAR_RADIUS = 100;
	private double viewingR;
	private Player me;
	public Radar(Vector v, Player p) {
		super(v);
		me = p;
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
			GL11.glTranslated(pos.x, pos.y, 0);
			drawMain();
		GL11.glPopMatrix();
	}
	
	private void drawMain()
	{
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);

			if (me.getShip() != null)
			{
				Ship s = me.getShip();
				viewingR = DEFAULT_RADAR_RADIUS;
				
				GL11.glPushMatrix();
					GL11.glColor4f(0, 1, 0, 0.5f);
					WeaselView.drawCircle(MINI_MAP_RADIUS, 100);
					EntityManager em = EntityManager.getEntityManager();
					TypeRange<Ship> tr = em.getAllShipsInSphere(s.getPosition(), (float) viewingR);
					while (tr.hasNext())
					{
						Ship ship = tr.next();

						Vector v = new Vector(0, 0, 0);
						v = s.getState().worldToBody.transform(ship.getPosition());
						if (ship.equals(s))
						{
						}
						else
						{
							GL11.glPushMatrix();
								GL11.glColor4f((float) (0.5 - 0.5*v.y/viewingR), 0, (float) (0.5 + 0.5*v.y/viewingR), 0.5f);
								GL11.glTranslated(v.x*MINI_MAP_RADIUS/viewingR, -v.z*MINI_MAP_RADIUS/viewingR, 0);
								WeaselView.drawDisk(TANK_CIRCLE_RADIUS, 100);		
							GL11.glPopMatrix();
						}						
					}
					
					TypeRange<Asteroid> tra = em.getAllAsteroidsInSphere(s.getPosition(), (float) viewingR);				
					while (tra.hasNext())
					{
						Asteroid ast = tra.next();

						Vector v = new Vector(0, 0, 0);
						v = s.getState().worldToBody.transform(ast.getPosition());
						GL11.glPushMatrix();
							GL11.glColor4f((float) (1 - Math.abs((v.y/viewingR))), (float) (1 - Math.abs((v.y/viewingR))), (float) (1 - Math.abs((v.y/viewingR))), 0.5f);
							GL11.glTranslated(v.x*MINI_MAP_RADIUS/viewingR, -v.z*MINI_MAP_RADIUS/viewingR, 0);
							WeaselView.drawDisk(TANK_CIRCLE_RADIUS, 100);		
						GL11.glPopMatrix();
					}
					
					GL11.glPushMatrix();
						GL11.glColor4f(0, 1, 0, 0.5f);
						GL11.glTranslated(0, 0, 0);
						WeaselView.drawDisk(TANK_CIRCLE_RADIUS, 100);		
					GL11.glPopMatrix();
				}
			
			GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}
