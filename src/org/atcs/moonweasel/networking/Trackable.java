package org.atcs.moonweasel.networking;

import java.util.List;

public interface Trackable
{
	public boolean hasRecentlyChanged();
	public void sent();
	public List<String> getRecentChanges();
}
