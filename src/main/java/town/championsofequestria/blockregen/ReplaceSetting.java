package town.championsofequestria.blockregen;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;

class ReplaceSetting {

    /**
     * Target refers to the material that is to be regenerated. For example, IRON_ORE
     */
    Material target;
    /**
     * Replace refers to what the temporary block that is placed until the block is regenerated.
     */
    Material replace;
    /**
     * Time, in seconds, until the material is regenerated.
     */
    int regenerateTime;
    /**
     * Time, in ticks, until the broken block is replaced with the replace block.
     */
    int replaceTime;
    /**
     * List of tools that the block can be broken with.
     */
    ArrayList<Material> tools;
    /**
     * Money to be paid once the block is broken.
     */
    double money;
    /**
     * Minimum amount of the block to drop.
     */
    int min;
    /**
     * Maximum amount of the block to drop.
     */
    int max;
    /**
     * If a world is in this list, it should NOT regenerate.
     */
    ArrayList<World> worlds;
    
    /**
     * If true, player-placed blocks of this type should be tracked. False if not.
     */
    boolean trackPlayers;

        
    ReplaceSetting(Material target, Material replace, int regenerateTime, int replaceTime, ArrayList<Material> tools, double money, int min, int max, ArrayList<World> worlds, boolean trackPlayers) {
        this.target = target;
        this.replace = replace;
        this.regenerateTime = regenerateTime;
        this.replaceTime = replaceTime;
        this.tools = tools;
        this.money = money;
        this.min = min;
        this.max = max;
        this.worlds = worlds;
        this.trackPlayers = trackPlayers;
    }

    @Override
    public String toString() {
        return String.format("ReplaceSetting[target=%s|repalce=%s|regenerateTime=%s|replaceTime=%s]", target.toString(), replace.toString(), regenerateTime, replaceTime);
    }
}