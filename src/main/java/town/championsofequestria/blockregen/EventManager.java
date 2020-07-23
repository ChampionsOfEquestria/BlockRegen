package town.championsofequestria.blockregen;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;

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
        Player player = pEvent.getPlayer();
        Material type = block.getType();
        if (type == Material.SPAWNER && s.spawnerReplaceSetting.worlds.contains(block.getWorld())) {
            if (player.hasPermission("coe.blockregen.bypass.remove")) {
                player.sendMessage(ChatColor.RED + "[BlockRegen] You removed a normally regenerating block in admin mode. This block will no longer regenerate.");
                p.getLogger().info(player.getName() + " removed " + type.toString() + " at " + BlockRegenPlugin.locationToString(block.getLocation()) + " in admin mode.");
                return;
            }

            p.scheduleTask(block, s.spawnerReplaceSetting);
            Bukkit.getScheduler().scheduleSyncDelayedTask(p, () -> {
                block.setType(s.spawnerReplaceSetting.replace);
            }, s.spawnerReplaceSetting.replaceTime);

        }

        new HashMap<Integer, BlockRegenTask>(p.tasks).forEach((id, task) -> {
            if (task.getBlockLocation().equals(block.getLocation())) {
                Bukkit.getScheduler().cancelTask(id);
                p.tasks.remove(id);
            }
        });
    }

}
