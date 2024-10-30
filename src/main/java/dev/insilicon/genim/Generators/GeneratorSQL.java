package dev.insilicon.genim.Generators;

import dev.insilicon.genim.Genim;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class GeneratorSQL {
    private final Genim plugin;
    private Connection connection;
    private File databaseFile;

    public GeneratorSQL(Genim plugin) {
        this.plugin = plugin;
        initializeDatabase();
    }

    private void initializeDatabase() {
        // Create plugin directory if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Initialize database file
        databaseFile = new File(plugin.getDataFolder(), "genDB.db");

        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create database file: " + e.getMessage());
                return;
            }
        }

        // Initialize SQLite connection
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            plugin.getLogger().info("Successfully connected to SQLite database!");
            createTables();

        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to SQLite database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite JDBC class not found: " + e.getMessage());
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            // Create generations table with correct columns
            statement.execute("CREATE TABLE IF NOT EXISTS generations (" +
                    "world TEXT NOT NULL," +
                    "location TEXT NOT NULL," +
                    "owner TEXT NOT NULL," +
                    "type INTEGER NOT NULL" +
                    ")");

            plugin.getLogger().info("Successfully created/verified generations table!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create tables: " + e.getMessage());
        }
    }

    public void storeGenerator(GeneratorTypes generatorType, String owner, int x, int y, int z) {
        try (Statement statement = connection.createStatement()) {
            // Fixed query to match table structure
            String query = String.format(
                    "INSERT INTO generations (world, location, owner, type) VALUES ('%s', '%d,%d,%d', '%s', %d)",
                    Bukkit.getWorlds().get(0).getName(),
                    x, y, z,
                    owner,
                    generatorType.ordinal()
            );
            statement.execute(query);
            plugin.getLogger().info("Successfully stored generator in database");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to store generator in database: " + e.getMessage());
        }
    }

    public void loadGenerators(GeneratorManager manager) {
        try (Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery("SELECT * FROM generations");

            while (resultSet.next()) {
                String[] location = resultSet.getString("location").split(",");
                int x = Integer.parseInt(location[0]);
                int y = Integer.parseInt(location[1]);
                int z = Integer.parseInt(location[2]);

                String owner = resultSet.getString("owner");
                GeneratorTypes type = GeneratorTypes.values()[resultSet.getInt("type")];

                // Create generator instance and display
                manager.addGenerator(new GeneratorInstance(type, x, y, z, UUID.fromString(owner)));
            }

            plugin.getLogger().info("Successfully loaded all generators from database!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load generators: " + e.getMessage());
        }
    }

    public void removeGenerator(int x, int y, int z) {
        try (Statement statement = connection.createStatement()) {
            String location = x + "," + y + "," + z;
            statement.execute("DELETE FROM generations WHERE location = '" + location + "'");
            plugin.getLogger().info("Successfully removed generator from database");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to remove generator from database: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initializeDatabase();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking database connection: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing database connection: " + e.getMessage());
        }
    }
}