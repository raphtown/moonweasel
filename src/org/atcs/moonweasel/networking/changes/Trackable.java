package org.atcs.moonweasel.networking.changes;


public interface Trackable
{
	public boolean hasRecentlyChanged();
	public void sent();
	public ChangeList getRecentChanges();
}
