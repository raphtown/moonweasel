package org.atcs.moonweasel.networking.actions;

import java.awt.event.ActionListener;

public interface ActionSource
{
	public void fireActionEvent(String command);
	
	public void addActionListener(ActionListener e);
	
	public void removeActionListener(ActionListener e);
}
