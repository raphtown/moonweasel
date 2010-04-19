package org.atcs.moonweasel.networking.changes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.atcs.moonweasel.entities.Entity;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Vector;

public class ChangeCompiler
{
	public static void compile(ChangeList changes, EntityManager mgr)
	{
		assert changes != null && mgr != null;
		List<String> list = changes.getChanges();

		if (mgr.get(changes.getID()) == null)
		{
			boolean created = false;
			List<String> toRemove = new LinkedList<String>();
			for (String s : list)
			{
				if (s.startsWith("create"))
				{
					if (!created && !(changes.getTypeName().equals("Player")))
					{
						mgr.create(changes.getTypeName());
						created = true;
					}
					else if (!(changes.getTypeName().equals("Player")))
					{
						mgr.setNextID(mgr.getNextID() + 1);
					}
					toRemove.add(s);
				}
			}

			for (String s : toRemove)
				list.remove(s);
		}

		// this is NOT equivalent to else {} - do not change it!
		if (mgr.get(changes.getID()) != null)
		{
			Entity e = mgr.get(changes.getID());
			for (Iterator<String> iter = list.iterator(); iter.hasNext();)
			{
				String change = iter.next();
				if (change.startsWith("set"))
				{
					String params = change.substring(5);
					if (params.startsWith("velocity"))
					{
						if (e instanceof ModelEntity)
						{
							Vector v = getVectorFromString(params.substring(9));
							((ModelEntity)e).setVelocity(v);
						}
					}
					else if (params.startsWith("position"))
					{
						if (e instanceof ModelEntity)
						{
							Vector v = getVectorFromString(params.substring(9));
							((ModelEntity)e).setPosition(v);
						}
					}
					else if (params.startsWith("ship"))
					{
						if (e instanceof Player)
						{
							int shipID = Integer.parseInt(params.substring(5));
							Ship ship = (Ship)(mgr.get(shipID));
							((Player)e).setShip(ship);
						}
					}
					else if (params.startsWith("pilot"))
					{
						if (e instanceof Ship)
						{
							int pilotID = Integer.parseInt(params.substring(6));
							Player pilot = (Player)(mgr.get(pilotID));
							((Ship)e).setPilot(pilot);
						}
					}
				}
				else if (change.startsWith("destroy"))
				{
					e.destroy();
					break;
				}
				else if (change.startsWith("damage"))
				{
					if (e instanceof Ship)
						((Ship)e).damage(Integer.parseInt(change.substring(7)));
				}
				else if (change.startsWith("apply command "))
				{
					if (e instanceof Ship)
						((Ship)e).apply(getCommandFromString(change.substring(15)));
				}
				iter.remove();
			}
			e.clearChanges();
		}
	}

	private static Vector getVectorFromString(String substring)
	{
		String[] components = substring.split(", ");
		components[0] = components[0].substring(1);
		components[2] = components[2].substring(0, components[2].length() - 1);
		return new Vector(Float.parseFloat(components[0]), Float.parseFloat(components[1]), Float.parseFloat(components[2]));
	}
	
	private static UserCommand getCommandFromString(String substring)
	{
		String[] components = substring.split(" ");
		components[0] = components[0].substring(1);
		components[components.length - 1] = components[components.length - 1].substring(0, components[components.length - 1].length() - 1);
		UserCommand uc = new UserCommand();
		uc.setKeysAsBitmask(Short.parseShort(components[0]));
		String v = "";

		for (int i = 1; i < components.length; i++)
			v += components[i] + " ";

		v = v.substring(0, v.length() - 1);
		uc.setMouse(getVectorFromString(v));
		return uc;
	}
}
