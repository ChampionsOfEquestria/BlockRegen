package town.championsofequestria.blockregen;

import org.bukkit.Material;

public class BlockType {
    
    public Material material;
    public byte data;
    
    public BlockType(Material material, byte data) {
        this.material = material;
        this.data = data;
    }
    
    
    @Override
    public boolean equals(Object o) {
        BlockRegenPlugin.debug("Checking to see if " + this.toString() + " equals " + o.toString());
        if(!(o instanceof BlockType)) {
            BlockRegenPlugin.debug("It does not!");
            return false;
        }
        BlockType other = (BlockType) o;
        boolean value = this.material == other.material && this.data == other.data;
        if(value) {
            BlockRegenPlugin.debug("It does!");
            return true;
        }
        BlockRegenPlugin.debug("It does not!");
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("BlockType=[material=%s|data=%d]", material.name(), data);
    }
}
