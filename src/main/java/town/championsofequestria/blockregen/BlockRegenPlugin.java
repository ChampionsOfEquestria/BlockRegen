package town.championsofequestria.blockregen;

import java.util.HashMap;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockRegenPlugin extends JavaPlugin {

    public static final String locationToString(Location location) {
        Objects.requireNonNull(location);
        World world = location.getWorld();
        return String.format("%s:%d,%d,%d", world == null ? "%UNLOADED_WORLD%" : world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static final Location stringToLocation(String location) {
        if (location == null || location.trim().isEmpty())
            return new Location(null, 0, 0, 0);
        String[] parts = location.split(":");
        String[] coords = parts[1].split(",");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
    }

    private CommandHandler ch;
    private Settings s;
    static BlockRegenPlugin p;
    public HashMap<Integer, BlockRegenTask> tasks;

    @Override
    public void onEnable() {
        p = this;
        tasks = new HashMap<Integer, BlockRegenTask>(0);
        s = new Settings(this);
        ch = new CommandHandler(s);
        getCommand("blockregen").setExecutor(ch);
        getServer().getPluginManager().registerEvents(new EventManager(this, s), this);
        saveDefaultConfig();
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
        scheduleTask(new BlockRegenTask(block, rs.regenerateTime));
    }

    public void scheduleTask(BlockRegenTask task) {
        task.setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(this, task, task.getTimeToRegenerate() * 20L));
        tasks.put(task.taskid, task);
    }

}
