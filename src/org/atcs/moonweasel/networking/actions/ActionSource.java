package org.atcs.moonweasel.networking.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public abstract class ActionSource
{
	private Set<ActionListener> actionListeners = new HashSet<ActionListener>();
	private static int counter = 0;

	public void fireActionEvent(String command)
	{
		for(ActionListener al : actionListeners)
			al.actionPerformed(new ActionEvent(this, counter++, command));
	}
	
	public void addActionListener(ActionListener e)
	{
		actionListeners.add(e);
	}
	
	public void removeActionListener(ActionListener e)
	{
		actionListeners.remove(e);
	}
}
