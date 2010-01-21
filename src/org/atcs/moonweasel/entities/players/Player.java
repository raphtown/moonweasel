package org.atcs.moonweasel.entities.players;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.ships.Ship;


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
	
	public void destroy() {
	}
	
	public UserCommand getCommandBefore(long t) {
		if (commands.peek().getTime() < t) {
			return commands.remove();
		}
		return null;
	}
	
	public void spawn() {
		this.commands.clear();
	}
}
