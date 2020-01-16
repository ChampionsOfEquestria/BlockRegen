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
    HashMap<Material, ReplaceSetting> blocks = new HashMap<Material, ReplaceSetting>(0);

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
                Material replace = Material.getMaterial(config.getString("blocks." + name + ".replace"));
                int regenerateTime = config.getInt("blocks." + name + ".regenerate-time");
                int replaceTime = config.getInt("blocks." + name + ".replace-time");
                ArrayList<String> toolsNames = (ArrayList<String>) config.getStringList("blocks." + name + ".tools-required");
                ArrayList<Material> tools = new ArrayList<Material>(toolsNames.size());
                for(String tool : toolsNames) {
                    tools.add(Material.getMaterial(tool));
                }
                double money = config.getDouble("blocks." + name + ".money");
                int min = config.getInt("blocks." + name + ".min", 1);
                int max = config.getInt("blocks." + name + ".max", 1);
                ArrayList<String> worldsBlacklist = (ArrayList<String>) config.getStringList("blocks." + name + ".but-not-in-worlds");
                ArrayList<World> worlds = new ArrayList<World>(worldsBlacklist.size());
                for(String world : worldsBlacklist) {
                    Optional<World> oWorld = Optional.<World>ofNullable(Bukkit.getWorld(world));
                    if (!oWorld.isPresent()) {
                        plugin.getLogger().log(Level.SEVERE, "Block " + name + " wanted to add " + world + " as a blacklist, but that world isn't loaded!");
                        continue;
                    }
                    worlds.add(oWorld.get());
                }
                boolean trackPlayers = config.getBoolean("blocks." + name + ".track-players", true);
               this.blocks.put(target, new ReplaceSetting(target, replace, regenerateTime, replaceTime, tools, money, min, max, worlds, trackPlayers));
                
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
            plugin.scheduleTask(new BlockRegenTask(loc.getBlock(), Material.getMaterial(event.getString("target-material")), event.getInt("time")));
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
        section.set("target-material", task.getMaterial());
        section.set("time", task.getTimeToRegenerate());
        saveEventConfig();
    }
}
