package town.championsofequestria.blockregen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import town.championsofequestria.blockregen.nbt.SpawnerData;

public class Settings {

    private final BlockRegenPlugin plugin;
    boolean debug;
    private YamlConfiguration eventConfig;
    private File eventConfigFile;
    HashMap<Material, ReplaceSetting> blocks = new HashMap<Material, ReplaceSetting>(0);
    ReplaceSetting spawnerReplaceSetting;

    Settings(final BlockRegenPlugin plugin) {
        this.plugin = plugin;
        eventConfigFile = new File(plugin.getDataFolder(), "events.yml");
        eventConfig = YamlConfiguration.loadConfiguration(eventConfigFile);
        readSettings(plugin.getConfig());
    }

    /**
     * Reads settings
     *
     * @param pConfig
     */
    private void readSettings(final FileConfiguration config) {
        debug = config.getBoolean("general.debugMessages");
        ArrayList<String> worldNames = (ArrayList<String>) config.getStringList("spawner.worlds");
        ArrayList<World> worlds = new ArrayList<World>(0);
        for (String worldName : worldNames) {
            Optional<World> world = Optional.ofNullable(Bukkit.getWorld(worldName));
            if (world.isPresent()) {
                worlds.add(world.get());
                continue;
            }
            plugin.getLogger().info("Tried to load unloaded world " + worldName);

        }
        spawnerReplaceSetting = new ReplaceSetting(Material.SPAWNER, Material.BEDROCK, config.getInt("spawner.regenerateTime"), config.getInt("spawner.replaceTime"), worlds);
        loadTasks();
    }

    private void loadTasks() {
        if (eventConfig.getConfigurationSection("events") == null)
            return;
        for (String eventId : eventConfig.getConfigurationSection("events").getKeys(false)) {
            ConfigurationSection event = eventConfig.getConfigurationSection("events." + eventId);
            Optional<World> oWorld = Optional.<World>ofNullable(Bukkit.getWorld(event.getString("world")));
            if (!oWorld.isPresent()) {
                plugin.getLogger().log(Level.SEVERE, "Unable to load event with id " + eventId + " due to the world it was in not being loaded!");
                continue;
            }
            Location loc = new Location(oWorld.get(), event.getDouble("x"), event.getDouble("y"), event.getDouble("z"));
            SpawnerData data = SpawnerData.fromString(event.getString("data"));
            plugin.scheduleTask(new BlockRegenTask(loc.getBlock(), data, event.getInt("time")));
            eventConfig.set("events." + eventId, null);
            saveEventConfig();
        }
    }

    private void saveEventConfig() {
        try {
            eventConfig.save(eventConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reloads settings
     */
    void reloadSettings() {
        plugin.reloadConfig();
        readSettings(plugin.getConfig());
    }

    void saveTask(int id, BlockRegenTask task) {
        ConfigurationSection section = eventConfig.createSection("events." + id);
        section.set("world", task.getBlockLocation().getWorld().getName());
        section.set("x", task.getBlockLocation().getBlockX());
        section.set("y", task.getBlockLocation().getBlockY());
        section.set("z", task.getBlockLocation().getBlockZ());
        section.set("time", task.getTimeToRegenerate());
        section.set("data", task.getData());
        saveEventConfig();
    }
}
