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
     * If a world is in this list, it should NOT regenerate.
     */
    ArrayList<World> worlds;
    

        
    ReplaceSetting(Material target, Material replace, int regenerateTime, int replaceTime,ArrayList<World> worlds) {
        this.target = target;
        this.replace = replace;
        this.regenerateTime = regenerateTime;
        this.replaceTime = replaceTime;
        this.worlds = worlds;
    }

    @Override
    public String toString() {
        return String.format("ReplaceSetting[target=%s|repalce=%s|regenerateTime=%s|replaceTime=%s]", target.toString(), replace.toString(), regenerateTime, replaceTime);
    }
}