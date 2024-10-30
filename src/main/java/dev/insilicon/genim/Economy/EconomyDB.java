package dev.insilicon.genim.Economy;

import dev.insilicon.genim.Genim;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

public class EconomyDB {

    private final Genim plugin;
    private final String dbName = "economy.db";
    private String dbPath;

    public EconomyDB(Genim plugin) {
        this.plugin = plugin;
        initializeDatabase();
    }

    private void initializeDatabase() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        dbPath = dataFolder.getAbsolutePath() + File.separator + dbName;

        try (Connection connection = getConnection()) {
            createTables(connection);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private void createTables(Connection connection) throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS player_economy (
                player_uuid TEXT PRIMARY KEY,
                cash DOUBLE NOT NULL DEFAULT 0,
                crystals DOUBLE NOT NULL DEFAULT 0
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public synchronized void createPlayer(Player player) {
        try (Connection connection = getConnection()) {
            // First check if player exists
            String checkSql = "SELECT player_uuid FROM player_economy WHERE player_uuid = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
                pstmt.setString(1, player.getUniqueId().toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return;
                }
            }

            // If player doesn't exist, create them
            String sql = "INSERT INTO player_economy (player_uuid, cash, crystals) VALUES (?, 0, 0)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, player.getUniqueId().toString());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create player entry", e);
        }
    }

    public synchronized double getCash(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT cash FROM player_economy WHERE player_uuid = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, playerUUID.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("cash");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player cash", e);
        }
        return 0.0;
    }

    public synchronized void setCash(UUID playerUUID, double amount) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = "UPDATE player_economy SET cash = ? WHERE player_uuid = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, playerUUID.toString());
                    pstmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set player cash", e);
        }
    }

    public synchronized void addCash(UUID playerUUID, double amount) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                double currentCash = getCash(playerUUID);
                String sql = "UPDATE player_economy SET cash = ? WHERE player_uuid = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setDouble(1, currentCash + amount);
                    pstmt.setString(2, playerUUID.toString());
                    pstmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add player cash", e);
        }
    }

    public synchronized void removeCash(UUID playerUUID, double amount) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                double currentCash = getCash(playerUUID);
                String sql = "UPDATE player_economy SET cash = ? WHERE player_uuid = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setDouble(1, Math.max(0, currentCash - amount));
                    pstmt.setString(2, playerUUID.toString());
                    pstmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove player cash", e);
        }
    }

    // Crystal methods similar to cash methods...
    public synchronized double getCrystals(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT crystals FROM player_economy WHERE player_uuid = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, playerUUID.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("crystals");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player crystals", e);
        }
        return 0.0;
    }

    public synchronized void setCrystals(UUID playerUUID, double amount) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = "UPDATE player_economy SET crystals = ? WHERE player_uuid = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, playerUUID.toString());
                    pstmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set player crystals", e);
        }
    }

    public synchronized void addCrystals(UUID playerUUID, double amount) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                double currentCrystals = getCrystals(playerUUID);
                String sql = "UPDATE player_economy SET crystals = ? WHERE player_uuid = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setDouble(1, currentCrystals + amount);
                    pstmt.setString(2, playerUUID.toString());
                    pstmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add player crystals", e);
        }
    }

    public synchronized void removeCrystals(UUID playerUUID, double amount) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                double currentCrystals = getCrystals(playerUUID);
                String sql = "UPDATE player_economy SET crystals = ? WHERE player_uuid = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setDouble(1, Math.max(0, currentCrystals - amount));
                    pstmt.setString(2, playerUUID.toString());
                    pstmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove player crystals", e);
        }
    }
}