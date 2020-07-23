package town.championsofequestria.blockregen.nbt;

import java.lang.reflect.Constructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import town.championsofequestria.blockregen.exceptions.ReflectionException;

public class SpawnerData {

    private static final Gson gson = new Gson();
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    public static SpawnerData fromString(String json) {
        return gson.fromJson(json, SpawnerData.class);
    }

    @SuppressWarnings("unchecked")
    public static CreatureSpawner getCreatureSpawner(Block block) throws ReflectionException {
        try {
            Class<? extends CreatureSpawner> clazz = (Class<? extends CreatureSpawner>) Class.forName("org.bukkit.craftbukkit." + version + ".block.CraftCreatureSpawner");
            Constructor<? extends CreatureSpawner> con = clazz.getConstructor(Block.class);
            return con.newInstance(block);
        } catch (Exception ex) {
            throw new ReflectionException(ex);
        }
    }

    public static CreatureSpawner getCreatureSpawnerFromSpawnerData(Block block, SpawnerData data) throws ReflectionException, IllegalArgumentException {
        Preconditions.checkArgument(block.getType().equals(Material.SPAWNER));
        CreatureSpawner spawner = getCreatureSpawner(block);
        spawner.setMaxNearbyEntities(data.MaxNearbyEntities);
        spawner.setRequiredPlayerRange(data.RequiredPlayerRange);
        spawner.setSpawnCount(data.SpawnCount);
        spawner.setSpawnedType(EntityType.valueOf(data.id));
        spawner.setMaxSpawnDelay(data.MaxSpawnDelay);
        spawner.setDelay(data.Delay);
        spawner.setSpawnRange(data.SpawnRange);
        spawner.setMinSpawnDelay(data.MinSpawnDelay);

        return spawner;
    }

    public static SpawnerData getSpawnerDataFromBlock(Block block) throws IllegalArgumentException {
        Preconditions.checkArgument(block.getType().equals(Material.SPAWNER));
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        SpawnerData data = new SpawnerData();
        
        data.MaxNearbyEntities = spawner.getMaxNearbyEntities();
        data.RequiredPlayerRange = spawner.getRequiredPlayerRange();
        data.SpawnCount = spawner.getSpawnCount();

        String id = spawner.getSpawnedType().getKey().getKey().toUpperCase();

        data.MaxSpawnDelay = spawner.getMaxSpawnDelay();

        data.Delay = spawner.getDelay();

        data.id = id;
        data.SpawnRange = spawner.getSpawnRange();
        data.MinSpawnDelay = spawner.getMinSpawnDelay();

        return data;
    }

    public int Delay;
    public String id;
    public int MaxNearbyEntities;
    public int MaxSpawnDelay;
    public int MinSpawnDelay;
    public int RequiredPlayerRange;
    public int SpawnCount;
    public int SpawnRange;

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
