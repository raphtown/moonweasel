package org.atcs.moonweasel;

import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.players.UserCommand.Commands;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputController {
	private UserCommand lastCommand;
	
	public InputController() {
		try {
			Keyboard.create();
			Mouse.create();
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			throw new RuntimeException("Unable to create keyboard or mouse.", e);
		}
		
		lastCommand = new UserCommand();
	}
	
	public UserCommand poll(long t) {
		UserCommand command = new UserCommand();
		command.copyKeyState(lastCommand);
				
		Keyboard.poll();
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			switch (key) {
				case Keyboard.KEY_W: command.set(Commands.UP, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_S: command.set(Commands.DOWN, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_A: command.set(Commands.LEFT, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_D: command.set(Commands.RIGHT, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_R: command.set(Commands.FORWARD, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_F: command.set(Commands.BACKWARD, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_P: command.set(Commands.STOP, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_LSHIFT:
				case Keyboard.KEY_RSHIFT: command.set(Commands.BOOST, Keyboard.getEventKeyState()); break;
				case Keyboard.KEY_LCONTROL: command.set(Commands.ROLLING, Keyboard.getEventKeyState()); break;
			}
			
			if (key == Keyboard.KEY_SPACE && 
					!Keyboard.getEventKeyState()) {
				command.toggle(Commands.AUTOMATIC_THRUSTER_CONTROL);
			} else if (key == Keyboard.KEY_ESCAPE &&
					!Keyboard.getEventKeyState()) {
				// This sucks but no better way atm.
				System.exit(0);
			}
		}
		
		

		Mouse.poll();
		while (Mouse.next()) {
			switch (Mouse.getEventButton()) {
				case 0:
					command.set(Commands.ATTACK_1, Mouse.getEventButtonState());
					break;
				case 1: 
					command.set(Commands.ROLLING, Mouse.getEventButtonState());
					break;
			}
		}
		
		command.setMouse(new Vector(Mouse.getDX(), Mouse.getDY(), 0));
//		if(Moonweasel.fh != null)
//		{
//			float[] RPY = Moonweasel.fh.getRPY();
//			command.setMouse(new Vector(RPY[0] * 5, RPY[1] * 5, 0));
//		}	
//		else
//		{
//			command.setMouse(new Vector(Mouse.getDX(), Mouse.getDY(), 0));
//		}	
		
		command.setTime(t);
		
		lastCommand = command;

		return command;
	}
}
