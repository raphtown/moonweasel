package org.atcs.moonweasel.util;

import org.atcs.moonweasel.Timed;

public class TimedDerivative implements Comparable<TimedDerivative>, Timed {
	public final long time;
	public final Vector force;
	public final Vector torque;
	
	public TimedDerivative(long time, Vector force, Vector torque) {
		this.time = time;
		this.force = force;
		this.torque = torque;
	}
	
	public long getTime() {
		return time;
	}

	@Override
	public int compareTo(TimedDerivative o) {
		return (int)(time - o.time);
	}
}
