package org.atcs.moonweasel.entities.players;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.ranges.TimeRange;


public class Player extends Entity {
	public enum Status {
		DEAD, PILOT, GUNNER;
	}
	
	private Status status;
	private Ship ship;
	
	private int kills;
	private int assists;
	private int deaths;
	
	private PriorityQueue<UserCommand> commands;
	
	private Player() {
		this.status = Status.DEAD;
		this.ship = null;
		
		this.kills = 0;
		this.assists = 0;
		this.deaths = 0;
		
		this.commands = new PriorityQueue<UserCommand>(5,
				new Comparator<UserCommand>() {
					@Override
					public int compare(UserCommand o1, UserCommand o2) {
						return (int)(o1.getTime() - o2.getTime());
					}
				});
	}
	
	public void addCommand(UserCommand command) {
		this.commands.add(command);
	}
	
	public void clearCommandsBefore(long t) {
		while (!commands.isEmpty() &&
				commands.peek().getTime() < t) {
			commands.remove();
		}
	}
	
	public void died() {
		deaths++;
	}
	
	public Range<UserCommand> getCommandsBefore(long t) {
		return new TimeRange<UserCommand>(0, t, commands.iterator());
	}
	
	public void killedPlayer() {
		kills++;
	}
	
	public void killedPlayerAssist() {
		assists++;
	}
	
	public void spawn() {
		this.commands.clear();
	}
	
	public void setShip(Ship ship) {
		this.ship = ship;
	}
	
	public Ship getShip() {
		return ship;
	}
}
