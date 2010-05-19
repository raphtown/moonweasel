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
	private static final int DEFAULT_RADAR_RADIUS = 30;

	private double viewingR;

	
	private Player me;
	
	
	public Radar(Vector v, Player p) {
		
		super(v);
		me = p;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		GL11.glPushMatrix();
//		GL11.glPushAttrib(GL11.GL_STENCIL_BUFFER_BIT);
			GL11.glTranslated(pos.x, pos.y, 0);
		
//			GL11.glClearStencil(0);
//			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
//			GL11.glEnable(GL11.GL_STENCIL_TEST);
//			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
//			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
//			GL11.glColorMask(false, false, false, false);		/* Disable writes to the color buffer */
//			GL11.glDepthMask(false);		/* Disable writes to the depth buffer */
//			
//			WeaselView.drawCircle(MINI_MAP_RADIUS, 100);
			
//			GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
//			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
//			GL11.glColorMask(true, true, true, true);
//			GL11.glDepthMask(true);
			
			drawMain();
//		GL11.glPopAttrib();
		GL11.glPopMatrix();
		
	}
	
	private void drawMain()
	{
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
//			GL11.glEnable(GL11.GL_BLEND);
//			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
			if (me.getShip() != null)
			{
				Ship s = me.getShip();
				viewingR = DEFAULT_RADAR_RADIUS;
				
				GL11.glPushMatrix();
	
//					GL11.glRotated(-t.getTurretZRotDegrees() + 90, 0, 0, 1);
					
					GL11.glColor4f(0, 1, 0, 0.5f);
					WeaselView.drawCircle(MINI_MAP_RADIUS, 100);
//					GL11.glColor4f(0, 1, 0, 0.5f);
					EntityManager em = EntityManager.getEntityManager();
//					State interpolated;
//					AxisAngle rotation;
					TypeRange<Ship> tr = em.getAllShipsInSphere(s.getPosition(), (float) viewingR);
					while (tr.hasNext())
					{
						Ship ship = tr.next();
						

							Vector v = new Vector(0, 0, 0);
							v = s.getState().worldToBody.transform(ship.getPosition());
//							v.add(ship.getPosition());
//							v.subtract(s.getPosition());
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
//							GL11.glColor4f(0, 1, 0, 0.5f);	
							}
							
						
					}
					
					
					TypeRange<Asteroid> tra = em.getAllAsteroidsInSphere(s.getPosition(), (float) viewingR);				
					while (tra.hasNext())
					{
						Asteroid ast = tra.next();

							Vector v = new Vector(0, 0, 0);
							v = s.getState().worldToBody.transform(ast.getPosition());
//							v.add(ship.getPosition());
//							v.subtract(s.getPosition());

								GL11.glPushMatrix();
								GL11.glColor4f((float) (1 - Math.abs((v.y/viewingR))), (float) (1 - Math.abs((v.y/viewingR))), (float) (1 - Math.abs((v.y/viewingR))), 0.5f);
//								GL11.glColor4f((float) (0.5 - 0.5*v.y/viewingR), 0, (float) (0.5 + 0.5*v.y/viewingR), 0.5f);
								GL11.glTranslated(v.x*MINI_MAP_RADIUS/viewingR, -v.z*MINI_MAP_RADIUS/viewingR, 0);
								WeaselView.drawDisk(TANK_CIRCLE_RADIUS, 100);		
							GL11.glPopMatrix();
//							GL11.glColor4f(0, 1, 0, 0.5f);	

							
						
					}
					
					GL11.glPushMatrix();
					GL11.glColor4f(0, 1, 0, 0.5f);
					GL11.glTranslated(0, 0, 0);
					WeaselView.drawDisk(TANK_CIRCLE_RADIUS, 100);		
					GL11.glPopMatrix();
//					GL11.glColor4f(0, 1, 0, 0.5f);	
					
//					for(int i = 0; i < verts.length; i++)
//					{
//						Vector2D temp = new Vector2D(0, 0);
//						temp = temp.plus(verts[i]);
//						temp = temp.minus(t.getPosition().getXYVector2D());
//						
//						Vector2D temp2 = new Vector2D(0, 0);
//						temp2 = temp2.plus(verts[(i+1) % verts.length]);
//						temp2 = temp2.minus(t.getPosition().getXYVector2D());
//						
//						
//						GL11.glBegin(GL_LINES);
//							GL11.glVertex2d(temp.getX()*MINI_MAP_RADIUS/viewingR, temp.getY()*MINI_MAP_RADIUS/viewingR);
//							GL11.glVertex2d(temp2.getX()*MINI_MAP_RADIUS/viewingR, temp2.getY()*MINI_MAP_RADIUS/viewingR);
//						GL11.glEnd();
//					}
					
//					for(Obstacle ob: obstacles)
//					{
//						for(int i = 0; i < ob.points.length; i++)
//						{
//							Vector2D temp = new Vector2D(0, 0);
//							temp = temp.plus(ob.points[i]);
//							temp = temp.minus(t.getPosition().getXYVector2D());
//							
//							Vector2D temp2 = new Vector2D(0, 0);
//							temp2 = temp2.plus(ob.points[(i+1) % ob.points.length]);
//							temp2 = temp2.minus(t.getPosition().getXYVector2D());
//							
//							
//							GL11.glBegin(GL_LINES);
//								GL11.glVertex2d(temp.getX()*MINI_MAP_RADIUS/viewingR, temp.getY()*MINI_MAP_RADIUS/viewingR);
//								GL11.glVertex2d(temp2.getX()*MINI_MAP_RADIUS/viewingR, temp2.getY()*MINI_MAP_RADIUS/viewingR);
//							GL11.glEnd();
//						}
//					}
					
				}
			
				GL11.glPopMatrix();
			GL11.glPopAttrib();
	}

}
