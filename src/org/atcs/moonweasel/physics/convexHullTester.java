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

import org.atcs.moonweasel.util.Vector;


public class convexHullTester extends JFrame
{
	public convexHullTester()
	{
		this.setSize(1280, 1024);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	
	/* public void paint(Graphics g)
	{
		System.out.println("attempting to paint");
		//super.paint(g);
		g.setColor(Color.white);
		g.fillRect(0,0,this.getBounds().width,this.getBounds().height);
		
		//ArrayList<Vector> cubeVecs = Physics.cubeVectors;
		//ArrayList<Vector> projVecs = Physics.projectedVectors;
		//ArrayList<Vector> convexVecs = Physics.convexHullVectors;
		
		g.setColor(Color.red);
		for(Vector v : projVecs)
		{
			g.drawOval((int) v.x, (int) v.y, 4, 4);
			System.out.println("Attempted to draw a projected component.");
		}
		
		g.setColor(Color.blue);
		int[] xPoints = new int[convexVecs.size()];
		int[] yPoints = new int[convexVecs.size()];
		
		for(int i = 0; i < convexVecs.size(); i++)
		{
			xPoints[i] = (int) convexVecs.get(i).x;
			yPoints[i] = (int) convexVecs.get(i).y;
		}
		
		g.drawPolygon(xPoints, yPoints, convexVecs.size());
		
		
	} */
}
