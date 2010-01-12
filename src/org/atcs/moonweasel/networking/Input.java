package org.atcs.moonweasel.networking;


//this class provides a way for the networking to send control inputs to the physics engine.
//it holds a bunch of boolean values corresponding to currently pressed controls, and the physics engine interprets these bools
public class Input
{
	public boolean left;
	public boolean right;
	public boolean up;
	public boolean down;
	public boolean space;
	public boolean ctrl;
	public boolean shift;
	public boolean tab;
	
	//empty constructor
	public Input()
	{
		this.left = false;
		this.right = false;
		this.up = false;
		this.down = false;
		this.space = false;
		this.ctrl = false;
		this.shift = false;
		this.tab = false;
	}
	
	//single-control input constructor
	public Input(String command)
	{
		if(command.equalsIgnoreCase("left"))
		{
			this.left = true;
		}
		else if(command.equalsIgnoreCase("right"))
		{
			this.right = true;
		}
		else if(command.equalsIgnoreCase("up"))
		{
			this.up = true;
		}
		else if(command.equalsIgnoreCase("down"))
		{
			this.down = true;
		}
		else if(command.equalsIgnoreCase("space"))
		{
			this.space = true;
		}
		else if(command.equalsIgnoreCase("ctrl"))
		{
			this.ctrl = true;
		}
		else if(command.equalsIgnoreCase("shift"))
		{
			this.shift = true;
		}
		else if(command.equalsIgnoreCase("tab"))
		{
			this.tab = true;
		}
		else //invalid command
		{
			
		}
		
	}
	
	//multi-control input constructor
	public Input(String[] commandList)
	{
		for(int i = 0; i < commandList.length; i++)
		{
			String command = commandList[i];
			if(command.equalsIgnoreCase("left"))
			{
				this.left = true;
			}
			else if(command.equalsIgnoreCase("right"))
			{
				this.right = true;
			}
			else if(command.equalsIgnoreCase("up"))
			{
				this.up = true;
			}
			else if(command.equalsIgnoreCase("down"))
			{
				this.down = true;
			}
			else if(command.equalsIgnoreCase("space"))
			{
				this.space = true;
			}
			else if(command.equalsIgnoreCase("ctrl"))
			{
				this.ctrl = true;
			}
			else if(command.equalsIgnoreCase("shift"))
			{
				this.shift = true;
			}
			else if(command.equalsIgnoreCase("tab"))
			{
				this.tab = true;
			}
			else //invalid command
			{
				
			}
			
		}
	}
	
	
	
	
}
