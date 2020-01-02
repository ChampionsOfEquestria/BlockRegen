package town.championsofequestria.blockregen;

import org.bukkit.Material;

class ReplaceSetting {

    /**
     * Target refers to the material that is to be regenerated. For example, IRON_ORE
     */
    BlockType target;
    /**
     * Replace refers to what the temporary block that is placed until the block is regenerated.
     */
    BlockType replace;
    /**
     * Time, in seconds, until the material is regenerated.
     */
    int regenerateTime;
    /**
     * Time, in ticks, until the broken block is replaced with the replace block.
     */
    int replaceTime;

    ReplaceSetting(Material target, byte targetData, Material replaceWith, byte replaceWithData, int regenerateTime, int replaceTime) {
        this.target = new BlockType(target, targetData);
        this.replace = new BlockType(replaceWith, replaceWithData);
        this.regenerateTime = regenerateTime;
        this.replaceTime = replaceTime;
    }
    
    @Override
    public String toString() {
        return String.format("ReplaceSetting[target=%s|repalce=%s|regenerateTime=%s|replaceTime=%s]", target.toString(), replace.toString(), regenerateTime, replaceTime);
    }
}