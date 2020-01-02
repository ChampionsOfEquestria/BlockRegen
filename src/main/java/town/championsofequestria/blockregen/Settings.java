package town.championsofequestria.blockregen;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {

    String dbDatabase;
    String dbHost;
    String dbPass;
    String dbPort;
    String dbPrefix;
    String dbUser;
    private final BlockRegenPlugin plugin;
    boolean stackTraces;
    boolean showQuery;
    boolean debug;
    private YamlConfiguration eventConfig;
    private File eventConfigFile;

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
        stackTraces = config.getBoolean("general.printStackTraces");
        showQuery = config.getBoolean("general.showQueries");
        dbHost = config.getString("database.host");
        dbPort = config.getString("database.port");
        dbUser = config.getString("database.username");
        dbPass = config.getString("database.password");
        dbDatabase = config.getString("database.database");
        dbPrefix = config.getString("database.prefix");
        ConfigurationSection blocks = config.getConfigurationSection("blocks");
        if (blocks != null)
            for (String name : blocks.getValues(false).keySet()) {
                Material target = Material.getMaterial(name);
                byte targetData = (byte) config.getInt("blocks." + name + "data");
                BlockMap.add(new BlockType(target, targetData), new ReplaceSetting(target, targetData, Material.getMaterial(config.getString("blocks." + name + ".replace")), (byte) config.getInt("blocks." + name + "replace-data"), config.getInt("blocks." + name + ".regenerateTime"), config.getInt("blocks." + name + ".replaceTime")));
            }
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
            plugin.scheduleTask(new BlockRegenTask(loc.getBlock(), Material.getMaterial(event.getString("target-material")), (byte) event.getInt("target-data"), event.getInt("time")));
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
        BlockMap.clear();
        readSettings(plugin.getConfig());
    }

    void saveTask(int id, BlockRegenTask task) {
        ConfigurationSection section = eventConfig.createSection("events." + id);
        section.set("world", task.getBlockLocation().getWorld().getName());
        section.set("x", task.getBlockLocation().getBlockX());
        section.set("y", task.getBlockLocation().getBlockY());
        section.set("z", task.getBlockLocation().getBlockZ());
        section.set("target-material", task.getMaterial());
        section.set("target-data", task.getData());
        section.set("time", task.getTimeToRegenerate());
        saveEventConfig();
    }
}
