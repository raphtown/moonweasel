package org.atcs.moonweasel.networking;

/**
 * Provides a way for the networking system to speak to the physics engine.
 * Essentially a data-storage class - don't care about the internal workings, 
 * they don't matter.
 */
public class Input
{
	// yes I know they're not private, they're package-private
	// TODO define what exactly these mean
	static final short FLAGS_LEFT = 0x1;
	static final short FLAGS_RIGHT = 0x2;
	static final short FLAGS_UP = 0x4;
	static final short FLAGS_DOWN = 0x8;
	static final short FLAGS_SPACE = 0x10;
	static final short FLAGS_CTRL = 0x20;
	static final short FLAGS_SHIFT = 0x40;
	static final short FLAGS_TAB = 0x80;

	// this is easier to transmit via a packet than a set of booleans.
	// remember that a given control, due to lag, may be recognized on the server
	// as simultaneous to another command. We want the client to take advantage of
	// this - perhaps a CTRL->UP would do something different than SHIFT->UP.
	private short flags = 0;

	// Note: Only use this for DEBUGGING purposes
	Input(short flags)
	{
		this.flags = flags;
	}

	//single-control input constructor
	public Input(String command)
	{
		if(command.equalsIgnoreCase("left"))
			flags |= FLAGS_LEFT;
		else if(command.equalsIgnoreCase("right"))
			flags |= FLAGS_RIGHT;
		else if(command.equalsIgnoreCase("up"))
			flags |= FLAGS_UP;
		else if(command.equalsIgnoreCase("down"))
			flags |= FLAGS_DOWN;
		else if(command.equalsIgnoreCase("space"))
			flags |= FLAGS_SPACE;
		else if(command.equalsIgnoreCase("ctrl"))
			flags |= FLAGS_CTRL;
		else if(command.equalsIgnoreCase("shift"))
			flags |= FLAGS_SHIFT;
		else if(command.equalsIgnoreCase("tab"))
			flags |= FLAGS_TAB;
	}

	//multi-control input constructor
	public Input(String... commandList)
	{
		for(String command : commandList)
		{
			if(command.equalsIgnoreCase("left"))
				flags |= FLAGS_LEFT;
			else if(command.equalsIgnoreCase("right"))
				flags |= FLAGS_RIGHT;
			else if(command.equalsIgnoreCase("up"))
				flags |= FLAGS_UP;
			else if(command.equalsIgnoreCase("down"))
				flags |= FLAGS_DOWN;
			else if(command.equalsIgnoreCase("space"))
				flags |= FLAGS_SPACE;
			else if(command.equalsIgnoreCase("ctrl"))
				flags |= FLAGS_CTRL;
			else if(command.equalsIgnoreCase("shift"))
				flags |= FLAGS_SHIFT;
			else if(command.equalsIgnoreCase("tab"))
				flags |= FLAGS_TAB;
		}
	}

	private boolean haveFlag(short flag)
	{
		return (flags & flag) != 0;
	}

	public boolean up()
	{
		return haveFlag(FLAGS_UP);
	}

	public boolean down()
	{
		return haveFlag(FLAGS_DOWN);
	}

	public boolean left()
	{
		return haveFlag(FLAGS_LEFT);
	}

	public boolean right()
	{
		return haveFlag(FLAGS_RIGHT);
	}

	public boolean space()
	{
		return haveFlag(FLAGS_SPACE);
	}

	public boolean ctrl()
	{
		return haveFlag(FLAGS_CTRL);
	}

	public boolean shift()
	{
		return haveFlag(FLAGS_SHIFT);
	}

	public boolean tab()
	{
		return haveFlag(FLAGS_TAB);
	}
}
