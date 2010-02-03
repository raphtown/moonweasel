package org.atcs.moonweasel.entities.players;

import org.atcs.moonweasel.Timed;
import org.atcs.moonweasel.util.Vector;

public class UserCommand implements Timed {
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
		
		NUM_COMMANDS((short)0x400);
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
	}
	
	public void copyKeyState(UserCommand o) {
		for(int i = 0; i < Commands.NUM_COMMANDS.ordinal(); i++) {
			commands[i] = o.commands[i];
		}
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
}
