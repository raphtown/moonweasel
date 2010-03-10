package org.atcs.moonweasel.entities.players;

import java.io.Serializable;

import org.atcs.moonweasel.Timed;
import org.atcs.moonweasel.util.Vector;

public class UserCommand implements Timed, Comparable<UserCommand>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Commands {
		UP((short)0x1),
		DOWN((short)0x2),
		LEFT((short)0x4),
		RIGHT((short)0x8),
		
		FORWARD((short)0x10),
		BACKWARD((short)0x20),
		
		ROLLING((short)0x40),
		
		BOOST((short)0x80),
		ATTACK_1((short)0x100),
		AUTOMATIC_THRUSTER_CONTROL((short)0x200),
		
		MOUSE_LEFT((short)0x400),
		MOUSE_RIGHT((short)0x800),
		MOUSE_UP((short)0x1000),
		MOUSE_DOWN((short)0x2000),
		
		NUM_COMMANDS((short)0x4000);
		public final short bitmask;
		Commands(short bitmask)
		{
			this.bitmask = bitmask;
		}
	}
	
	private long time;
	private boolean[] commands;
	private Vector mouse;
	
	public UserCommand() {
		this.commands = new boolean[Commands.NUM_COMMANDS.ordinal()];
		set(Commands.AUTOMATIC_THRUSTER_CONTROL, true);
		set(Commands.ROLLING, true);
	}
	
	public void copyKeyState(UserCommand o) {
		setKeysAsBitmask(o.getAsBitmask());
	}
	
	public boolean get(Commands command) {
		return commands[command.ordinal()];
	}
	
	public Vector getMouse() {
		return mouse;
	}
	
	public long getTime() {
		return time;
	}
	
	public void set(Commands command, boolean value) {
		commands[command.ordinal()] = value;
	}
	
	public void setMouse(Vector position) {
		this.mouse = position;

		if (position.x < 0)
			commands[Commands.MOUSE_LEFT.ordinal()] = true;
		else if (position.x > 0)
			commands[Commands.MOUSE_RIGHT.ordinal()] = true;
		
		if (position.y < 0)
			commands[Commands.MOUSE_UP.ordinal()] = true;
		else if (position.y > 0)
			commands[Commands.MOUSE_DOWN.ordinal()] = true;
	}
	
	public void setTime(long t) {
		this.time = t;
	}
	
	public void toggle(Commands command) {
		commands[command.ordinal()] = !commands[command.ordinal()];
	}
	
	public short getAsBitmask()
	{
		short bitmask = 0;
		for (Commands command : Commands.values())
		{
			if (command.ordinal() >= Commands.NUM_COMMANDS.ordinal())
				break;
			if (commands[command.ordinal()])
				bitmask |= command.bitmask;
		}
		return bitmask;
	}
	
	public void setKeysAsBitmask(short bitmask)
	{
		for (Commands command : Commands.values())
		{
			if (command.ordinal() >= Commands.NUM_COMMANDS.ordinal())
				break;
			commands[command.ordinal()] = ((bitmask & command.bitmask) != 0);
		}
	}
	
	public void setMouse(float x, float y)
	{
		setMouse(new Vector(x, y, 0));
	}

	@Override
	public int compareTo(UserCommand o)
	{
		return (int)(getTime() - o.getTime());
	}
}
