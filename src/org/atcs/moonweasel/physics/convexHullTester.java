package org.atcs.moonweasel.physics;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import org.atcs.moonweasel.util.ConvexHull;


public class convexHullTester extends JFrame
{
	private static final long serialVersionUID = -6054964178574546408L;
	public static ConvexHull myCH;
	boolean done = false;

	public convexHullTester()
	{
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		double start = System.currentTimeMillis();
		myCH = new ConvexHull(Physics.projectOntoXY(Physics.blah1), "xy");
		double stop = System.currentTimeMillis();
		System.out.println("took " + (stop - start) + " ms to hull the points");
		this.repaint();
	}


	public void paint(Graphics g)
	{
		g.setColor(Color.BLACK);
		for(int i = 0; i < Physics.blah1.size(); i++)
		{
			g.fillOval((int)(Physics.blah1.get(i).x), (int)(Physics.blah1.get(i).y), 3, 3);
		}
		for(int i = 0; i < myCH.getCH().size(); i++)
		{
			g.drawLine((int)(myCH.getCH().get(i).getStartPoint().x), (int)(myCH.getCH().get(i).getStartPoint().y), (int)(myCH.getCH().get(i).getEndPoint().x), (int)(myCH.getCH().get(i).getEndPoint().y));
		}
	}
}