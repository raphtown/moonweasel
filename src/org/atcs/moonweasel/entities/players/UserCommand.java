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
		AUTOMATIC_THRUSTER_CONTROL,
		
		MOVE_CAMERA_OUT,
		MOVE_CAMERA_IN,
		
		
		NUM_COMMANDS
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
}
