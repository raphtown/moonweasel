package org.atcs.moonweasel.entities.players;

import java.util.PriorityQueue;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.ranges.TimeRange;

public class Player extends Entity
{
	private static final long serialVersionUID = -5228344170942537611L;

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

		this.commands = new PriorityQueue<UserCommand>(5);

		scheduleThink(50);
	}

	public void addCommand(UserCommand command) {
		synchronized (commands)
		{
			System.out.println("WOW" + this + "  " + command);
			this.commands.add(command);
			addChange("add command " + command);
		}
	}

	public void clearCommandsBefore(long t) {
		synchronized (commands)
		{
			while (!commands.isEmpty() &&
					commands.peek().getTime() < t) {
				commands.remove();
			}
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
		addChange("set ship " + ship.getID());
	}

	public Ship getShip() {
		return ship;
	}

	public void think() {
		if (ship != null) {
			synchronized (commands)
			{
				for (UserCommand command : getCommandsBefore(getTime())) {
					System.out.println("WOW" + this);
					ship.apply(command);
				}
			}

		}
		clearCommandsBefore(getTime());

		scheduleThink(50);
	}

	public Status getStatus() {
		return status;
	}
}
