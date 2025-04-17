package com.jellypudding.offlineStore.data;

import com.jellypudding.offlineStore.OfflineStore;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ColorOwnershipManager {

    private final OfflineStore plugin;
    private Connection connection;
    private final String databaseUrl;

    public ColorOwnershipManager(OfflineStore plugin) {
        this.plugin = plugin;
        File databaseFile = new File(plugin.getDataFolder(), "owned_colors.db");
        this.databaseUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(databaseUrl);
            initializeDatabase();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to SQLite database for owned colours: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLite JDBC driver not found.", e);
        }
    }

    private void initializeDatabase() throws SQLException {
        // Use try-with-resources for automatic closing of the statement
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS player_owned_colors (" +
                              "uuid TEXT NOT NULL, " +
                              "color_name TEXT NOT NULL, " +
                              "PRIMARY KEY (uuid, color_name)" +
                              ");");
            plugin.getLogger().info("Database table 'player_owned_colors' initialised successfully.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create 'player_owned_colors' database table: " + e.getMessage(), e);
            throw e; // Re-throw to indicate initialization failure
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    plugin.getLogger().info("Owned colours database connection closed.");
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error closing owned colours database connection: " + e.getMessage(), e);
            }
        }
    }

    public boolean hasColor(UUID playerUUID, String colorName) {
        String sql = "SELECT 1 FROM player_owned_colors WHERE uuid = ? AND color_name = ? LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, colorName.toLowerCase()); // Store/check lowercase
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a row exists, false otherwise
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not check colour ownership for " + playerUUID + ": " + e.getMessage(), e);
            return false; // Assume not owned on error
        }
    }

    public boolean addColorOwnership(UUID playerUUID, String colorName) {
        String sql = "INSERT OR IGNORE INTO player_owned_colors (uuid, color_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, colorName.toLowerCase());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Returns true if a new row was inserted
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not add colour ownership for " + playerUUID + ": " + e.getMessage(), e);
            return false;
        }
    }

    public Set<String> getOwnedColors(UUID playerUUID) {
        Set<String> ownedColors = new HashSet<>();
        String sql = "SELECT color_name FROM player_owned_colors WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ownedColors.add(rs.getString("color_name"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not retrieve owned colours for " + playerUUID + ": " + e.getMessage(), e);
            // Return potentially empty set on error
        }
        return ownedColors;
    }
} 