package town.championsofequestria.blockregen;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.palmergames.bukkit.towny.object.TownBlock;
import net.milkbowl.vault.economy.Economy;

public class EventManager implements Listener {

    private final Settings s;
    private Data d;
    private BlockRegenPlugin p;
    private boolean hasEconomy;
    private RegisteredServiceProvider<?> economy;
    private ThreadLocalRandom random;
    private boolean hasTowny;

    public EventManager(final BlockRegenPlugin p, final Settings s, final Data d, boolean hasEconomy, RegisteredServiceProvider<?> economy, boolean hasTowny) {
        this.p = p;
        this.s = s;
        this.d = d;
        this.hasEconomy = hasEconomy;
        this.economy = economy;
        this.random = ThreadLocalRandom.current();
        this.hasTowny = hasTowny;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onBlockBreak(final BlockBreakEvent pEvent) {
        Block block = pEvent.getBlock();
        Player player = pEvent.getPlayer();
        BlockType type = new BlockType(block.getType(), block.getData());
        if (s.debug)
            p.getLogger().info("Checking if the BlockMap has " + type.toString());
        ReplaceSetting rs = BlockMap.get(type);
        if (rs != null && !rs.worlds.contains(block.getWorld())) {
            if (player.hasPermission("coe.blockregen.bypass.remove")) {
                player.sendMessage(ChatColor.YELLOW + "[BlockRegen] You removed a normally regenerating block in admin mode. This block will no longer regenerate.");
                p.getLogger().info(player.getName() + " removed " + type.toString() + " at " + p.locToString(block.getLocation()) + " in admin mode.");
                return;
            }
            if (hasTowny) {
                Optional<TownBlock> oTownBlock = TownyHandler.getTownBlock(block.getLocation());
                if (oTownBlock.isPresent()) {
                    TownBlock townBlock = oTownBlock.get();
                    if (townBlock.hasTown()) {
                        if (s.blacklistedTowns.contains(TownyHandler.getTownFromTownBlockIgnoreExceptions(townBlock).getName()))
                            return;
                    }
                }
            }
            if (!rs.tools.contains(player.getInventory().getItemInMainHand().getType())) {
                player.sendMessage(ChatColor.RED + "You lack the tool required to mine this block.");
                pEvent.setCancelled(true);
                return;
            }
            if (d.isPlayerPlaced(block.getLocation())) {
                if (s.debug)
                    p.getLogger().info("Removing player placed block flag at " + p.locToString(block.getLocation()));
                d.removePlayerPlacedBlock(block.getLocation());
                return;
            }
            if (hasEconomy) {
                ((Economy) economy.getProvider()).depositPlayer(player, block.getWorld().getName(), rs.money);
            }
            p.scheduleTask(block, rs);
            if (rs.max > 1) {
                pEvent.setDropItems(false);
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(type.material, getRandomNumber(rs.min, rs.max)));
            }
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
        ReplaceSetting rs = BlockMap.get(type);
        if (rs != null && rs.trackPlayers && !rs.worlds.contains(block.getWorld())) {
            Player player = pEvent.getPlayer();
            if (player.hasPermission("coe.blockregen.bypass.place")) {
                player.sendMessage(ChatColor.YELLOW + "[BlockRegen] You placed a normally regenerating block in admin mode. This block will regenerate.");
                p.getLogger().info(player.getName() + " placed " + type.toString() + " at " + p.locToString(block.getLocation()) + " in admin mode.");
                return;
            }
            if (s.debug)
                p.getLogger().info("Setting player placed block flag at " + p.locToString(block.getLocation()));
            d.addPlayerPlacedBlock(block.getLocation());
        }
    }

    private int getRandomNumber(int min, int max) {
        return random.nextInt(min, max + 1);
    }
}
