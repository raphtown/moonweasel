package org.atcs.moonweasel.util;

import java.io.Serializable;

import org.atcs.moonweasel.Timed;

public class TimedDerivative implements Comparable<TimedDerivative>, Timed, Serializable {
	private static final long serialVersionUID = 4926297997692134730L;
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
