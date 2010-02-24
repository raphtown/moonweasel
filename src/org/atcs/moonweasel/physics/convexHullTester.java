package org.atcs.moonweasel.physics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.atcs.moonweasel.util.ConvexHull;
import org.atcs.moonweasel.util.Vector;


public class convexHullTester extends JFrame
{	
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