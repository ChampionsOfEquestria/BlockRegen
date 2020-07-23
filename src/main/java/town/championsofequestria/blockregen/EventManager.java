package town.championsofequestria.blockregen;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventManager implements Listener {

    private final Settings s;
    private BlockRegenPlugin p;

    public EventManager(final BlockRegenPlugin p, final Settings s) {
        this.p = p;
        this.s = s;

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockBreak(final BlockBreakEvent pEvent) {
        Block block = pEvent.getBlock();
        Material type = block.getType();
        if(!type.equals(s.spawnerReplaceSetting.replace) && !type.equals(s.spawnerReplaceSetting.target))
            return;
        if (!s.spawnerReplaceSetting.worlds.contains(block.getWorld()))
            return;

        if (checkRemoveAdminMode(pEvent))
            return;

        p.scheduleTask(block, s.spawnerReplaceSetting);
        Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
            block.setType(s.spawnerReplaceSetting.replace);
        }, s.spawnerReplaceSetting.replaceTime);

    }

    private boolean checkRemoveAdminMode(BlockBreakEvent pEvent) {
        Player player = pEvent.getPlayer();
        Block block = pEvent.getBlock();
        Material type = block.getType();
        Location location = block.getLocation();
        if (player.hasPermission("coe.blockregen.bypass.remove")) {
            if (type.equals(s.spawnerReplaceSetting.target)) {
                player.sendMessage(ChatColor.RED + "[BlockRegen] You removed a normally regenerating block in admin mode. This block will no longer regenerate.");
                p.getLogger().info(player.getName() + " removed " + type.toString() + " at " + BlockRegenPlugin.locationToString(block.getLocation()) + " in admin mode.");
                return true;
            }
            new HashMap<Integer, BlockRegenTask>(p.tasks).forEach((id, task) -> {
                if (task.getBlockLocation().equals(location)) {
                    Bukkit.getScheduler().cancelTask(id);
                    p.tasks.remove(id);
                }
            });
            return true;
        }
        return false;
    }
}
