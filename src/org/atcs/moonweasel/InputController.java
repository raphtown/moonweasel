package org.atcs.moonweasel;

import java.awt.event.MouseEvent;

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
		lastCommand = command;
		
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
				case Keyboard.KEY_LSHIFT:
				case Keyboard.KEY_RSHIFT: command.set(Commands.BOOST, Keyboard.getEventKeyState()); break;
			}
			
			if (key == Keyboard.KEY_SPACE && 
					!Keyboard.getEventKeyState()) {
				command.toggle(Commands.AUTOMATIC_THRUSTER_CONTROL);
			}
		}
		
		Mouse.poll();
		while (Mouse.next()) {
			switch (Mouse.getEventButton()) {
				case MouseEvent.BUTTON1:
					command.set(Commands.ATTACK_1, Mouse.getEventButtonState());
					break;
				case MouseEvent.BUTTON3: 
					command.set(Commands.ROLLING, !Mouse.getEventButtonState());
					break;
			}
		}
		
		command.setMouse(new Vector(Mouse.getDX(), Mouse.getDY(), 0));
		command.setTime(t);
		
		return command;
	}
}
