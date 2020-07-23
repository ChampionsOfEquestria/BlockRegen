package town.championsofequestria.blockregen;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
	private final static String noPermission = ChatColor.RED + "You do not have permission.";
	private final Settings s;

	public CommandHandler(final Settings s) {

		this.s = s;
	}

	private boolean hasPermission(final CommandSender sender, final String command) {
		return sender.hasPermission("coe.blockregen." + command);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (command.getName().equalsIgnoreCase("blockregen")) {
			if (args.length == 0)
				return false;
			if (args[0].equalsIgnoreCase("reload")) {
				if (!hasPermission(sender, "reload")) {
					sender.sendMessage(noPermission);
					return true;
				}
				s.reloadSettings();
				sender.sendMessage(ChatColor.YELLOW + "[CoEBlockRegen] Config reloaded.");
				return true;
			}
			return false;
		}
		return false;
	}
}
