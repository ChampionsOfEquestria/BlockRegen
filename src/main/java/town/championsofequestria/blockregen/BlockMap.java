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

    public static ReplaceSetting get(BlockType type) {
        try {
            return settings.get(types.indexOf(type));
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public static void clear() {
        types.removeAll(types);
        settings.removeAll(settings);
    }
}
