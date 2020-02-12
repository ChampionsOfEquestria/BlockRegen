package town.championsofequestria.blockregen;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class CommandHandler implements CommandExecutor {

    private final static String noPermission = ChatColor.RED + "You do not have permission.";
    private final Settings s;
    private Data d;
    private boolean hasTowny;

    public CommandHandler(Data d, final Settings s, boolean hasTowny) {
        this.d = d;
        this.s = s;
        this.hasTowny = hasTowny;
    }

    private boolean hasPermission(final CommandSender sender, final String command) {
        return sender.hasPermission("coe.blockregen." + command);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("blockregen")) {
            if (args.length == 0)
                return false;
            if (args[0].equalsIgnoreCase("reload")) {
                if (!hasPermission(sender, "reload")) {
                    sender.sendMessage(noPermission);
                    return true;
                }
                d.forceConnectionRefresh();
                s.reloadSettings();
                sender.sendMessage(ChatColor.YELLOW + "[BlockRegen] Config reloaded.");
                return true;
            }
            if (args[0].equalsIgnoreCase("towny")) {
                if (!hasTowny) {
                    sender.sendMessage(ChatColor.RED + "[BlockRegen] Towny isn't installed!");
                    return true;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (hasPermission(player, "towny-override")) {
                        if (args.length != 2) {
                            player.sendMessage(ChatColor.YELLOW + "[BlockRegen] Because you are in admin mode, you must specify the town to toggle as a 2nd argument.");
                            return true;
                        }
                        Optional<Town> oTown = TownyHandler.getTown(args[1]);
                        if (!oTown.isPresent()) {
                            player.sendMessage(ChatColor.RED + "[BlockRegen] That town name is invalid.");
                        }
                        return toggleTown(player, oTown.get());
                    }
                    Optional<Resident> oRes = TownyHandler.getResident(player.getName());
                    if (oRes.isPresent()) {
                        Resident res = oRes.get();
                        Optional<Town> oTown = TownyHandler.getTown(res);
                        if (oTown.isPresent()) {
                            Town town = oTown.get();
                            if (town.getMayor().equals(res)) {
                                return toggleTown(player, town);
                            } // Not the mayor
                            sender.sendMessage(ChatColor.RED + "[BlockRegen] Sorry, only Town mayors can toggle this setting!");
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "[BlockRegen] It looks like you aren't apart of a town. If you think this is an error, contact an admin.");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.YELLOW + "[BlockRegen] Because you are console mode, you must specify the town to toggle as a 2nd argument.");
                    return true;
                }
                Optional<Town> oTown = TownyHandler.getTown(args[1]);
                if (!oTown.isPresent()) {
                    sender.sendMessage(ChatColor.RED + "[BlockRegen] That town name is invalid.");
                }
                return toggleTown(sender, oTown.get());
            }
            return false;
        }
        return false;
    }

    private boolean toggleTown(CommandSender sender, Town town) {
        if (s.blacklistedTowns.contains(town.getName())) {
            // Already contains it. Remove
            s.removeBlacklistedTown(town.getName());
            sender.sendMessage(ChatColor.GREEN + "[BlockRegen] Ores in " + town.getName() + " will regenerate again!");
            return true;
        }
        // Does not contain it. Add
        s.addBlacklistedTown(town.getName());
        sender.sendMessage(ChatColor.GREEN + "[BlockRegen] Ores in " + town.getName() + " will no longer regenerate!");
        return true;
    }
}
