package town.championsofequestria.blockregen;

import java.util.ArrayList;

public class BlockMap {
    
    private static ArrayList<BlockType> types;
    private static ArrayList<ReplaceSetting> settings;
    static {
        types = new ArrayList<BlockType>(0);
        settings = new ArrayList<ReplaceSetting>(0);
    }
    
    public static void add(BlockType type, ReplaceSetting setting) {
        types.add(type);
        settings.add(setting);
    }
    
    public static boolean has(BlockType type) {
        return types.contains(type);
    }
    
    public static ReplaceSetting get(BlockType type) {
        return settings.get(types.indexOf(type));
    }
    
    public static void clear() {
        types.removeAll(types);
        settings.removeAll(settings);
    }
}
