package org.atcs.moonweasel.entities;

public interface Vulnerable {
	public void damage(int damage);
	public int getHealth();
	public int getOriginalHealth();
}
