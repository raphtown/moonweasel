package org.atcs.moonweasel.networking.changes;


public interface Trackable
{
	public boolean hasRecentlyChangedForClient(String c);
	public void sent(String c);
	public ChangeList getRecentChanges(String c);
}
