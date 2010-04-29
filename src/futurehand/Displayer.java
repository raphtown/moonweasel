package futurehand;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;


public class Displayer extends JFrame implements Runnable
{
	private static final long serialVersionUID = 1L;
	FutureHand fh;
	boolean first = true;
	int count = 0;
	
	public Displayer(FutureHand fh)
	{
		this.fh = fh;
		this.setSize(1000,1022);
		this.setVisible(true);
		this.run();
	}
	
	public void paint(Graphics g)
	{
		g.translate(0, 22);
		if(first)
		{
			g.setColor(Color.WHITE);
			g.fillRect(0,0,1000,1000);
			first = false;
		}

		
		float[] rpy = fh.getRPY();
		for(int i = 0; i < rpy.length; i++)
		{
			rpy[i] =  ((-rpy[i] / (float)Math.PI + 1 + 2 * i) * 1000 / 6);
		}
		
		g.setColor(Color.RED);
		g.drawLine(count, (int)rpy[0], count, (int)rpy[0]);
		
		g.setColor(Color.BLUE);
		g.drawLine(count, (int)rpy[1], count, (int)rpy[1]);
		
		g.setColor(Color.GREEN);
		g.drawLine(count, (int)rpy[2], count, (int)rpy[2]);
		count++;
		if (count>1000) {
			count=0;
			first=true;
		}
	}

	@Override
	public void run()
	{
		
		while(true)
		{
			try
			{
				Thread.sleep(20);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			repaint();

		}

		
	}
}
