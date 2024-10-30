package dev.insilicon.genim.Terrian;

import dev.insilicon.genim.Genim;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Level;

public class TerrianDB {

    private final Genim plugin;
    private Connection connection;
    private final String dbName = "generationDB.db";

    public TerrianDB(Genim plugin) {
        this.plugin = plugin;
        initializeDatabase();
        initializeDefaultParams();
    }

    private void initializeDatabase() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        String dbPath = dataFolder.getAbsolutePath() + File.separator + dbName;

        try {
            Class.forName("org.sqlite.SQLITE");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database", e);
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            // Create chunks table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS chunks (" +
                            "type INTEGER NOT NULL," +
                            "x INTEGER NOT NULL," +
                            "y INTEGER NOT NULL," +
                            "z INTEGER NOT NULL," +
                            "PRIMARY KEY (x, y, z)" +
                            ")"
            );

            // Create params_int table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS params_int (" +
                            "paramType INTEGER PRIMARY KEY," +
                            "value INTEGER NOT NULL" +
                            ")"
            );
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create database tables", e);
        }
    }

    private void initializeDefaultParams() {
        try {
            // Check each parameter and insert if it doesn't exist
            for (int i = 1; i <= 4; i++) {
                String checkSql = "SELECT COUNT(*) FROM params_int WHERE paramType = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, i);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        // Parameter doesn't exist, insert it
                        String insertSql = "INSERT INTO params_int (paramType, value) VALUES (?, 0)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, i);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize default parameters", e);
        }
    }

    public void saveChunk(int type, int x, int y, int z) {
        String sql = "INSERT OR REPLACE INTO chunks (type, x, y, z) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, type);
            pstmt.setInt(2, x);
            pstmt.setInt(3, y);
            pstmt.setInt(4, z);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save chunk data", e);
        }
    }

    public void saveParam(int paramType, int value) {
        String sql = "INSERT OR REPLACE INTO params_int (paramType, value) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paramType);
            pstmt.setInt(2, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save parameter", e);
        }
    }

    public Integer getChunkType(int x, int y, int z) {
        String sql = "SELECT type FROM chunks WHERE x = ? AND y = ? AND z = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, x);
            pstmt.setInt(2, y);
            pstmt.setInt(3, z);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("type");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get chunk type", e);
        }
        return null;
    }

    public Integer getParam(int paramType) {
        String sql = "SELECT value FROM params_int WHERE paramType = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paramType);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("value");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get parameter value", e);
        }
        return null;
    }

    public boolean deleteChunk(int x, int y, int z) {
        String sql = "DELETE FROM chunks WHERE x = ? AND y = ? AND z = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, x);
            pstmt.setInt(2, y);
            pstmt.setInt(3, z);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to delete chunk", e);
            return false;
        }
    }

    public boolean deleteParam(int paramType) {
        String sql = "DELETE FROM params_int WHERE paramType = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, paramType);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to delete parameter", e);
            return false;
        }
    }

    public void removeAllChunks() {
        String sql = "DELETE FROM chunks";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove all chunks", e);
        }
    }

    // param 1 : x pos
    // param 2 : y pos
    // param 3 : z pos
    // param 4 : width
    public boolean replaceParam(int type, int replacementValue) {
        String sql = "UPDATE params_int SET value = ? WHERE paramType = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, replacementValue);
            pstmt.setInt(2, type);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to replace parameter value", e);
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error closing database connection", e);
        }
    }
}