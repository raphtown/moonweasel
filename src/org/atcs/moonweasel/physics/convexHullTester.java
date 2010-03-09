//package org.atcs.moonweasel.physics;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.util.ArrayList;
//import java.util.Iterator;
//import javax.swing.JFrame;
//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//
//import org.atcs.moonweasel.util.ConvexHull;
//import org.atcs.moonweasel.util.Vector;
//
//
//public class ConvexHullTester extends JFrame
//{	
//	
//	public static ArrayList<Vector> polygonToDraw = Physics.poly;
//	public static ArrayList<Vector> pointsIn = Physics.pointsIn;
//	public static ArrayList<Vector> pointsOut = Physics.pointsOut;
//	public ConvexHullTester()
//	{
//		this.setSize(1024, 768);
//		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		this.setVisible(true);
//		this.repaint();
//	}
//
//	
//
//	public void paint(Graphics g)
//	{
//		int[] arrayPointsX = new int[polygonToDraw.size()];
//		int[] arrayPointsY = new int[polygonToDraw.size()];
//		
//		for(int i = 0; i < polygonToDraw.size(); i++)
//		{
//			arrayPointsX[i] = (int) polygonToDraw.get(i).x;
//			arrayPointsY[i] = (int) polygonToDraw.get(i).y;
//		}
//		
//		
//		g.setColor(Color.BLACK);
//		g.drawPolygon(arrayPointsX, arrayPointsY, polygonToDraw.size());
//		
//		g.setColor(Color.GREEN);
//		for(int i = 0; i < pointsIn.size(); i++)
//		{
//			//System.out.println("Drawing a RED inside point");
//			g.fillOval((int)pointsIn.get(i).x, (int)pointsIn.get(i).y, 3, 3);
//
//		}
//		g.setColor(Color.BLUE);
//		for(int i = 0; i < pointsOut.size(); i++)
//		{
//			
//			g.fillOval((int)pointsOut.get(i).x, (int)pointsOut.get(i).y, 3, 3);
//
//		}
//	}
//
//
//	
//
//
//
//
//
//
//
//
//
//
//
//
//}