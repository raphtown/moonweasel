package org.atcs.moonweasel;

import static org.atcs.moonweasel.networking.actions.ActionMessages.CHOOSE_SHIP;
import static org.atcs.moonweasel.networking.actions.ActionMessages.CLIENT_CONNECT;
import static org.atcs.moonweasel.networking.actions.ActionMessages.CLIENT_DISCONNECT;
import static org.atcs.moonweasel.networking.actions.ActionMessages.COMMAND_RECEIVED;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.entities.ships.ShipType;
import org.atcs.moonweasel.networking.Server;


public class Artemis extends Moonweasel implements ActionListener {
	
	private final Map<Player, Long> playerCommandMap = new HashMap<Player, Long>();
	
	public Artemis(int width, int height, boolean fullscreen) {
		super(width, height, fullscreen);
		
//		System.out.print("Enter server name: ");
//		String serverName = new java.util.Scanner(System.in).nextLine();
		server = new Server("Moonweasel Server");

		server.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String actionCommand = e.getActionCommand();
		Debug.print("Action performed: " + actionCommand);
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
				String shipTypeName = st.typeName;
				Player plr = playerMap.get(clientHostname);
				Ship ship = this.entityManager.create(shipTypeName);
				ship.setPilot(plr);
				ship.spawn();
				plr.setShip(ship);
			}
			else if (parts[0].equals(COMMAND_RECEIVED))
			{
				Debug.print("Artemis to Lycanthrope ... we have lift-off");
				short command = Short.parseShort(parts[1]);
				float mouseX = Float.parseFloat(parts[2]);
				float mouseY = Float.parseFloat(parts[3]);
				String clientHostname = parts[4];
				Player plr = playerMap.get(clientHostname);
				if (playerCommandMap.get(plr) != null)
					if (playerCommandMap.get(plr).compareTo(new Long(command)) == 0)
						return;
				UserCommand ucommand = new UserCommand();
				ucommand.setKeysAsBitmask(command);
				ucommand.setMouse(mouseX, mouseY);
				ucommand.setTime(this.getT());
				plr.addCommand(ucommand);
				playerCommandMap.put(plr, new Long(command));

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
