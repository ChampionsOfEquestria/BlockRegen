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
        if(!(o instanceof BlockType)) 
            return false;
        BlockType other = (BlockType) o;
        return this.material == other.material && this.data == other.data;
    }
    
    @Override
    public String toString() {
        return String.format("BlockType=[material=%s|data=%d]", material.name(), data);
    }
}
