package town.championsofequestria.blockregen;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import com.google.common.base.Preconditions;
import town.championsofequestria.blockregen.exceptions.ReflectionException;
import town.championsofequestria.blockregen.nbt.SpawnerData;

public class BlockRegenTask implements Runnable {

    private Block block;
    private SpawnerData data;
    /**
     * Time in seconds.
     */
    private int seconds;
    int taskid;
    private int myTaskId;

    public BlockRegenTask(Block block, int seconds) {
        this(block, SpawnerData.getSpawnerDataFromBlock(block), seconds);
    }

    public BlockRegenTask(Block block, SpawnerData data, int seconds) {
        this.block = Preconditions.checkNotNull(block);
        this.data = Preconditions.checkNotNull(data);
        Preconditions.checkArgument(seconds > 0, "Seconds cannot be negative.");
        this.seconds = seconds;
        this.myTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(BlockRegenPlugin.p, () -> {
            decrementSeconds();
        }, 20, seconds * 20);
    }

    private void decrementSeconds() {
        seconds--;
    }

    public void cancelMyTask() {
        Bukkit.getScheduler().cancelTask(myTaskId);
    }

    @Override
    public void run() {
        block.setType(Material.SPAWNER);
        CreatureSpawner spawner;
        try {
            spawner = SpawnerData.getCreatureSpawnerFromSpawnerData(block, data);
        } catch (ReflectionException e) {
            e.printStackTrace();
            return;
        }
        if(!spawner.update(true)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Invalid Spawner type.");
            builder.append(System.lineSeparator());
            builder.append("Block = ").append(block.toString());
            builder.append(System.lineSeparator());
            builder.append("SpawnerData = ").append(data.toString());
            throw new IllegalStateException(builder.toString());
        }
        BlockRegenPlugin.p.tasks.remove(taskid);
    }

    public int getTimeToRegenerate() {
        return seconds;
    }

    public Location getBlockLocation() {
        return block.getLocation();
    }

    public String getMaterial() {
        return Material.SPAWNER.toString();
    }

    public void setTaskId(int taskId) {
        this.taskid = taskId;
    }

    public String getData() {
        return data.toString();
    }

    @Override
    public String toString() {
        return String.format("BlockBR[block=%s|type=%s|data=%s|seconds=%d|taskid=%d]", block.toString(), Material.SPAWNER.name(), data.toString(), seconds, taskid);
    }

}
