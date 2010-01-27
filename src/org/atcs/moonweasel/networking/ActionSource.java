package org.atcs.moonweasel.networking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public abstract class ActionSource
{
	ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	static int counter = 0;
	
	public void fireActionEvent(String command)
	{
		for(ActionListener al : actionListeners)
		{
			al.actionPerformed(new ActionEvent(this, counter++, command));
		}
	}
	
	public void addActionListener (ActionListener e)
	{
		actionListeners.add(e);
	}
	
	
	public void removeActionListener(ActionListener e)
	{
		actionListeners.remove(e);
	}
}
