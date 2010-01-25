package org.atcs.moonweasel.entities.players;

import org.atcs.moonweasel.util.Vector;

public class UserCommand
{
	public enum Commands {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		
		FORWARD,
		BACKWARD,
		
		ROLLING,
		
		BOOST,
		ATTACK_1,
		
		NUM_COMMANDS
	}
	
	private final long time;
	private final boolean[] commands;
	private final Vector mouse;
	
	public UserCommand(long time, Vector mouse) {
		this.time = time;
		this.commands = new boolean[Commands.NUM_COMMANDS.ordinal()];
		this.mouse = mouse;
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
}
