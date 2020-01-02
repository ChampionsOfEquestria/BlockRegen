package town.championsofequestria.blockregen;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class EventManager implements Listener {

    private final Settings s;
    private Data d;
    private BlockRegenPlugin p;

    public EventManager(final BlockRegenPlugin p, final Settings s, final Data d) {
        this.p = p;
        this.s = s;
        this.d = d;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    void onBlockBreak(final BlockBreakEvent pEvent) {
        Block block = pEvent.getBlock();
        BlockType type = new BlockType(block.getType(), block.getData());
        if (s.debug)
            p.getLogger().info("Checking if the BlockMap has " + type.toString());
        if (BlockMap.has(type)) {
            if (d.isPlayerPlaced(block.getLocation())) {
                if (s.debug)
                    p.getLogger().info("Removing player placed block flag at " + BlockRegenPlugin.locToString(block.getLocation()));
                d.removePlayerPlacedBlock(block.getLocation());
                return;
            }
            ReplaceSetting rs = BlockMap.get(type);
            p.scheduleTask(block, rs);
            Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {

                @Override
                public void run() {
                    block.setType(rs.replace.material);
                    block.setData(rs.replace.data);
                }
            }, rs.replaceTime);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onBlockPlace(final BlockPlaceEvent pEvent) {
        Block block = pEvent.getBlock();
        BlockType type = new BlockType(block.getType(), block.getData());
        if (s.debug)
            p.getLogger().info("Checking if the BlockMap has " + type.toString());
        if (BlockMap.has(type)) {
            if (s.debug)
                p.getLogger().info("Setting player placed block flag at " + BlockRegenPlugin.locToString(block.getLocation()));
            d.addPlayerPlacedBlock(block.getLocation());
        }
    }
}
