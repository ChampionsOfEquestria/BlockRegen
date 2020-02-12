package town.championsofequestria.blockregen;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

/**
 * Helper class to handle Towny's stupid decision to throw exceptions when values are null instead of returning Optionals like smart people. Like come on, do you want people to write spaghetti code??? Assholes
 *
 */
public class TownyHandler {

    /**
     * All of our methods are static.
     */
    private TownyHandler() {
    }

    /**
     * Check if Towny even exists.
     *
     * @return
     */
    public static boolean checkTowny() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Towny") != null)
            return true;
        return false;
    }

    /**
     * Gets a resident from its name
     *
     * @param name
     * @return
     */
    public static Optional<Resident> getResident(String name) {
        try {
            return Optional.<Resident>ofNullable(TownyUniverse.getDataSource().getResident(name));
        } catch (NotRegisteredException e) {
            return Optional.<Resident>empty();
        }
    }

    /**
     * Gets a town from its resident.
     *
     * @param resident
     * @return
     */
    public static Optional<Town> getTown(Resident resident) {
        try {
            return Optional.<Town>ofNullable(resident.getTown());
        } catch (NotRegisteredException e) {
            return Optional.<Town>empty();
        }
    }

    /**
     * Gets a town from its name.
     *
     * @param name
     * @return
     */
    public static Optional<Town> getTown(String name) {
        try {
            return Optional.<Town>ofNullable(TownyUniverse.getDataSource().getTown(name));
        } catch (NotRegisteredException e) {
            return Optional.<Town>empty();
        }
    }

    /**
     * Gets a TownBlock from a location
     *
     * @param loc
     * @return
     */
    public static Optional<TownBlock> getTownBlock(Location loc) {
        return Optional.<TownBlock>ofNullable(TownyUniverse.getTownBlock(loc));
    }

    /**
     * Gets a Town from a TownBlock. Will return null if the block isn't apart of a town!
     *
     * @param block
     * @return either the town, or null
     */
    public static Town getTownFromTownBlockIgnoreExceptions(TownBlock block) {
        try {
            return block.getTown();
        } catch (NotRegisteredException ignored) {
            return null;
        }
    }
}
