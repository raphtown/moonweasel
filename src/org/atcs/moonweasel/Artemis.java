package org.atcs.moonweasel;

import static org.atcs.moonweasel.networking.actions.ActionMessages.COMMAND_RECEIVED;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.players.UserCommand;
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
			if (parts[0].equals(COMMAND_RECEIVED))
			{
				Debug.print("Artemis to Lycanthrope ... we have lift-off");
				short command = Short.parseShort(parts[1]);
				float mouseX = Float.parseFloat(parts[2]);
				float mouseY = Float.parseFloat(parts[3]);
				String clientHostname = parts[4];
				Player plr = server.playerMap.get(clientHostname);
			
				if (playerCommandMap.get(plr) != null)
					if (playerCommandMap.get(plr).compareTo(new Long(command)) == 0)
						return;
				System.out.println("Command received from client: " + clientHostname + "  command: " + command  + "Player: " + plr);
				UserCommand ucommand = new UserCommand();
				ucommand.setKeysAsBitmask(command);
				ucommand.setMouse(mouseX, mouseY);
				ucommand.setTime(t - 5);
				plr.addCommand(ucommand);
				System.out.println("Wow: " + plr.getID() + "   " + plr.getCommandsBefore(t));
				playerCommandMap.put(plr, new Long(command));

			}
		}
	}
}
