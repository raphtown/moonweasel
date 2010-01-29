package org.atcs.moonweasel;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.Arrays;

import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.players.UserCommand.Commands;
import org.atcs.moonweasel.util.Vector;

import com.sun.javafx.newt.KeyEvent;
import com.sun.javafx.newt.KeyListener;
import com.sun.javafx.newt.MouseEvent;
import com.sun.javafx.newt.MouseListener;
import com.sun.javafx.newt.Window;

public class InputController implements KeyListener, MouseListener {
	private Window window;
	private Robot robot;
	private UserCommand command;
	
	public InputController(Window window) {
		this.window = window;
		
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException("Error initializing robot.", e);
		}
		
		this.command = new UserCommand();
		
		window.addKeyListener(this);
		window.addMouseListener(this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W: command.set(Commands.UP, true); break;
		case KeyEvent.VK_S: command.set(Commands.DOWN, true); break;
		case KeyEvent.VK_A: command.set(Commands.LEFT, true); break;
		case KeyEvent.VK_D: command.set(Commands.RIGHT, true); break;
		case KeyEvent.VK_R: command.set(Commands.FORWARD, true); break;
		case KeyEvent.VK_F: command.set(Commands.BACKWARD, true); break;
		case KeyEvent.VK_SHIFT: command.set(Commands.BOOST, true); break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W: command.set(Commands.UP, false); break;
		case KeyEvent.VK_S: command.set(Commands.DOWN, false); break;
		case KeyEvent.VK_A: command.set(Commands.LEFT, false); break;
		case KeyEvent.VK_D: command.set(Commands.RIGHT, false); break;
		case KeyEvent.VK_R: command.set(Commands.FORWARD, false); break;
		case KeyEvent.VK_F: command.set(Commands.BACKWARD, false); break;
		case KeyEvent.VK_SHIFT: command.set(Commands.BOOST, false); break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			command.set(Commands.ATTACK_1, true);			
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			command.set(Commands.ROLLING, true);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			command.set(Commands.ATTACK_1, false);			
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			command.set(Commands.ROLLING, false);
		}
	}
	
	private float[] getMouseDelta() {
		int centerX = window.getX() + window.getWidth() / 2;
		int centerY = window.getY() + window.getHeight() / 2;
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		float[] delta = new float[2];
		delta[0] = (float)(mouse.x - centerX) / window.getWidth() * 2; 
		delta[1] = (float)(centerY - mouse.y) / window.getHeight() * 2; 
		robot.mouseMove(centerX, centerY);
		return delta;
	}

	public UserCommand poll(long t) {
		UserCommand old = command;
		command = new UserCommand();

		float[] mouse = getMouseDelta();
		old.setMouse(new Vector(mouse[0], mouse[1], 0));
		old.setTime(t);
		
		return old;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseWheelMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
