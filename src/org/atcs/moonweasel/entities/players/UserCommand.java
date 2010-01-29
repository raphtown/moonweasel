package org.atcs.moonweasel.entities.players;

import org.atcs.moonweasel.Timed;
import org.atcs.moonweasel.util.Vector;

public class UserCommand implements Timed {
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
	
	private long time;
	private boolean[] commands;
	private Vector mouse;
	
	public UserCommand() {
		this.commands = new boolean[Commands.NUM_COMMANDS.ordinal()];
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
}
