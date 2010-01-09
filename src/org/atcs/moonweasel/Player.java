package org.atcs.moonweasel;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.Ship;

public class Player extends Entity {
	public enum Status {
		DEAD, PILOT, GUNNER;
	}
	
	private Status status;
	private Ship ship;
	
	private int kills;
	private int assists;
	private int deaths;
	
	public Player() {
		this.status = Status.DEAD;
		this.ship = null;
		
		this.kills = 0;
		this.assists = 0;
		this.deaths = 0;		
	}
	
	public void destroy() {
		
	}
	
	public void spawn() {
		
	}
}
