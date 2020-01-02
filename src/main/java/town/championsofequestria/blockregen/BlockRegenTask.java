package town.championsofequestria.blockregen;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockRegenTask implements Runnable {
    
    
    private Block block;
    private Material type;
    private byte data;
    private int seconds;
    public int taskid;

    public BlockRegenTask(Block block, Material type, byte data, int seconds) {
        this.block = block;
        this.type = type;
        this.data = data;
        this.seconds = seconds;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        block.setType(type);
        block.setData(data);
        BlockRegenPlugin.tasks.remove(taskid);
    }
    
    public int getTimeToRegenerate() {
        return seconds;
    }
    
    public Location getBlockLocation() {
        return block.getLocation();
    }
    
    public String getMaterial() {
        return type.toString();
    }
    
    public byte getData() {
        return data;
    }
    
    public void setTaskId(int taskId) {
        this.taskid = taskId;
    }
    
   @Override
   public String toString() {
       return String.format("BlockBR[block=%s|type=%s|data=%d|seconds=%d|taskid=%d]", block.toString(), type.name(), data, seconds, taskid);
   }
    
}
