package town.championsofequestria.blockregen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;

/**
 * This class handles data transfer to and from the SQL server.
 *
 */
public class Data {

    private Connection connection;
    private final Settings s;
    private Logger logger;

    public Data(BlockRegenPlugin p, final Settings s) {
        logger = p.getLogger();
        this.s = s;
        createTables();
    }

    /**
     * Adds the note to the player
     *
     * @param pUUID
     *            the user
     * @param pStaffName
     *            the staff who added it
     * @param pNote
     *            the note
     */
    public void addNoteToUser(final UUID pUUID, final String pStaffName, final String pNote) {
        try {
            PreparedStatement statement = connection.prepareStatement(String.format("INSERT INTO %snotes (uuid, time, lastKnownStaffName, note) VALUES (?, ?, ?, ?);", s.dbPrefix));
            statement.setString(1, pUUID.toString());
            statement.setNull(2, Types.TIMESTAMP);
            statement.setString(3, pStaffName);
            statement.setString(4, pNote);
            statement.executeUpdate();
            statement.close();
        } catch (final SQLException e) {
            error(e);
        }
    }

    /**
     * Creates a new table in the database
     *
     * @param pQuery
     *            the query
     * @return
     */
    private int createTable(final String pQuery) {
        try {
            PreparedStatement table = prepareStatement(pQuery);
            int val = table.executeUpdate();
            table.close();
            return val;
        } catch (final SQLException e) {
            error(e);
        }
        return 0;
    }

    /**
     * Create tables if needed
     */
    private final void createTables() {
        // Generate the information about the various tables
        final String blocks = "CREATE TABLE `" + s.dbPrefix + "blocks` (`id` INT NOT NULL AUTO_INCREMENT, `world` VARCHAR(50) NOT NULL, `x` INT NOT NULL, `y` INT NOT NULL, `z` INT NOT NULL, PRIMARY KEY (`id`));";
        // Generate the database tables
        if (!tableExists(s.dbPrefix + "blocks"))
            createTable(blocks);
    }

    /**
     * Method used to handle errors
     *
     * @param e
     *            Exception
     */
    public void error(final Throwable e) {
        if (s.stackTraces) {
            e.printStackTrace();
            return;
        }
        if (e instanceof SQLException) {
            logger.severe("SQLException: " + e.getMessage());
            return;
        }
        logger.severe("Unhandled Exception " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Executes an SQL query. Throws an exception to allow for other methods to handle it.
     *
     * @param query
     *            the query to execute
     * @return number of rows affected
     * @throws SQLException
     *             if an error occurred
     */
    private PreparedStatement prepareStatement(final String query) throws SQLException {
        if (s.showQuery)
            logger.info(query);
        if (connection == null || connection.isClosed()) {
            final String connect = new String("jdbc:mysql://" + s.dbHost + ":" + s.dbPort + "/" + s.dbDatabase + "?autoReconnect=true&useSSL=false");
            connection = DriverManager.getConnection(connect, s.dbUser, s.dbPass);
            logger.info("Connecting to " + s.dbUser + "@" + connect + "...");
        }
        return connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * Private method for getting an SQL connection, then submitting a query. This method throws an SQL Exception to allow another method to handle it.
     *
     * @param query
     *            the query to get data from.
     * @return the data
     * @throws SQLException
     *             if an error occurs
     */
    private ResultSet getResultSet(final String query) throws SQLException {
        if (s.showQuery)
            logger.info(query);
        if (connection == null || connection.isClosed()) {
            final String connect = new String("jdbc:mysql://" + s.dbHost + ":" + s.dbPort + "/" + s.dbDatabase + "?autoReconnect=true&useSSL=false");
            connection = DriverManager.getConnection(connect, s.dbUser, s.dbPass);
            logger.info("Connecting to " + s.dbUser + "@" + connect + "...");
        }
        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
    }

    /**
     * Forces a refresh of the connection object
     */
    public void forceConnectionRefresh() {
        try {
            if (connection == null || connection.isClosed()) {
                final String connect = new String("jdbc:mysql://" + s.dbHost + ":" + s.dbPort + "/" + s.dbDatabase + "?autoReconnect=true&useSSL=false");
                connection = DriverManager.getConnection(connect, s.dbUser, s.dbPass);
                logger.info("Connecting to " + s.dbUser + "@" + connect + "...");
            } else {
                connection.close();
                final String connect = new String("jdbc:mysql://" + s.dbHost + ":" + s.dbPort + "/" + s.dbDatabase + "?autoReconnect=true&useSSL=false");
                connection = DriverManager.getConnection(connect, s.dbUser, s.dbPass);
                logger.info("Connecting to " + s.dbUser + "@" + connect + "...");
            }
        } catch (final SQLException e) {
            error(e);
        }
    }

    /**
     * checks if a table exists
     *
     * @param pTable
     *            the table name.
     * @return true if the table exists, or false if either the table does not exists, or another error occurs.
     */
    private boolean tableExists(final String pTable) {
        try {
            return getResultSet("SELECT * FROM " + pTable) != null;
        } catch (final SQLException e) {
            // Handle both ' and "
            if (e.getMessage().equalsIgnoreCase("Table '" + s.dbDatabase + "." + pTable + "' doesn't exist") || e.getMessage().equalsIgnoreCase("Table \"" + s.dbDatabase + "." + pTable + "\" doesn't exist"))
                return false;
            error(e);
        }
        return false;
    }

    public boolean isPlayerPlaced(Location block) {
        try {
            ResultSet rs = getResultSet(String.format("SELECT * FROM %sblocks WHERE world = \"%s\" AND x = %d AND y = %d AND z = %d;", s.dbPrefix, block.getWorld().getName(), block.getBlockX(), block.getBlockY(), block.getBlockZ()));
            return rs.next();
        } catch (SQLException e) {
            error(e);
        }
        return false;
    }

    public void removePlayerPlacedBlock(Location block) {
        try {
            PreparedStatement statement = prepareStatement(String.format("DELETE FROM %sblocks WHERE world = ? AND x = ? AND y = ? AND z = ? ;", s.dbPrefix));
            statement.setString(1, block.getWorld().getName());
            statement.setInt(2, block.getBlockX());
            statement.setInt(3, block.getBlockY());
            statement.setInt(4, block.getBlockZ());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            error(e);
        }
    }

    public void addPlayerPlacedBlock(Location block) {
        try {
            PreparedStatement statement = prepareStatement(String.format("INSERT INTO %sblocks (world, x, y, z) VALUES (?, ?, ?, ?);", s.dbPrefix));
            statement.setString(1, block.getWorld().getName());
            statement.setInt(2, block.getBlockX());
            statement.setInt(3, block.getBlockY());
            statement.setInt(4, block.getBlockZ());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            error(e);
        }
    }
}
