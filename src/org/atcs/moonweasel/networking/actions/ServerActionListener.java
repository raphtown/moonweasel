package org.atcs.moonweasel.networking.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.Moonweasel;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.Client;
import org.atcs.moonweasel.networking.Server;

import static org.atcs.moonweasel.networking.actions.ActionMessages.*;

public class ServerActionListener implements ActionListener
{
	private final EntityManager entityManager;
	private final Map<String, Player> playerMap;
	private final Map<Player, Long> playerCommandMap = new HashMap<Player, Long>();
	private final Client client;
	private final Moonweasel s;
	
	public ServerActionListener(EntityManager entityManager, Map<String, Player> playerMap, Client client, Moonweasel s)
	{
		this.entityManager = entityManager;
		this.playerMap = playerMap;
		this.client = client;
		this.s = s;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String actionCommand = e.getActionCommand();
		System.out.println("Action performed: " + actionCommand);
		String[] parts = actionCommand.split(" ");
		if (parts.length > 0)
		{
			if (parts[0].equals(CLIENT_CONNECT))
			{
				String clientHostname = parts[1];
				
				Player plr = entityManager.create("player");
				plr.spawn();
				
				playerMap.put(clientHostname, plr);
			}
			else if (parts[0].equals(CHOOSE_SHIP))
			{
				ShipType st = ShipType.valueOf(parts[1]);
				String clientHostname = parts[2];
				if (!clientHostname.equals(client.getIP()))
				{
					String shipTypeName = st.typeName;
					Player plr = playerMap.get(clientHostname);
					Ship ship = this.entityManager.create(shipTypeName);
					ship.setPilot(plr);
					ship.spawn();
					plr.setShip(ship);
				}
			}
			else if (parts[0].equals(COMMAND_RECEIVED))
			{
				short command = Short.parseShort(parts[1]);
				float mouseX = Float.parseFloat(parts[2]);
				float mouseY = Float.parseFloat(parts[3]);
				String clientHostname = parts[4];
				if (!clientHostname.equals(client.getIP()))
				{
					Player plr = playerMap.get(clientHostname);
					if (playerCommandMap.get(plr) != null)
						if (playerCommandMap.get(plr).compareTo(new Long(command)) == 0)
							return;
					UserCommand ucommand = new UserCommand();
					ucommand.setKeysAsBitmask(command);
					ucommand.setMouse(mouseX, mouseY);
					ucommand.setTime(s.getT());
					plr.addCommand(ucommand);
					playerCommandMap.put(plr, new Long(command));
				}
			}
			else if (parts[0].equals(CLIENT_DISCONNECT))
			{
				String hostname = parts[1];
				Player plr = playerMap.get(hostname);
				this.entityManager.delete(plr);
				playerMap.remove(hostname);
				plr.getShip().destroy();
				plr.destroy();
				((Server)(e.getSource())).forceUpdateAllClients();
			}
		}
	}
}
