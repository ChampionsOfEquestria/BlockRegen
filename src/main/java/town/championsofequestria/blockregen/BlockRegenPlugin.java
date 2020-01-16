package town.championsofequestria.blockregen;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

public class BlockRegenPlugin extends JavaPlugin {

    private CommandHandler ch;
    private Settings s;
    private Data d;
    static BlockRegenPlugin p;
    public static HashMap<Integer, BlockRegenTask> tasks;

    @Override
    public void onEnable() {
        p = this;
        tasks = new HashMap<Integer, BlockRegenTask>(0);
        s = new Settings(this);
        d = new Data(this, s);
        ch = new CommandHandler(d, s);
        getCommand("blockregen").setExecutor(ch);
        boolean hasEconomy = false;
        RegisteredServiceProvider<?> economy = null;
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            economy = getServer().getServicesManager().getRegistration(Economy.class);
            if (economy != null)
                hasEconomy = true;
        }
        getServer().getPluginManager().registerEvents(new EventManager(this, s, d, hasEconomy, economy), this);
        if (s.debug)
            getLogger().info("Startup complete.");
    }

    @Override
    public void onDisable() {
        for (BlockRegenTask task : tasks.values()) {
            task.cancelMyTask();
            s.saveTask(task.taskid, task);
            Bukkit.getScheduler().cancelTask(task.taskid);
            if (s.debug)
                getLogger().info("Removed task " + task.taskid);
        }
        tasks.clear();
    }

    public void scheduleTask(Block block, ReplaceSetting rs) {
        scheduleTask(new BlockRegenTask(block, block.getType(), rs.regenerateTime));
    }

    public void scheduleTask(BlockRegenTask task) {
        task.setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(this, task, task.getTimeToRegenerate() * 20L));
        tasks.put(task.taskid, task);
    }

    public String locToString(Location loc) {
        return String.format("%d,%d,%d:%s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }
}
