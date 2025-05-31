package com.jellypudding.offlineStore.data;

import com.jellypudding.offlineStore.OfflineStore;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class MotdManager {

    private final OfflineStore plugin;
    private Connection connection;
    private final String databaseUrl;
    private final Random random = new Random();
    
    // Pattern to validate MOTD messages - allow letters, numbers, spaces, basic punctuation, and color codes
    private static final Pattern VALID_MOTD_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s&§\\-_.!?,:;()\\[\\]{}\"']+$");
    private static final String DEFAULT_SECOND_LINE = "§bLike minecraftonline except worse";
    private static final String FIRST_LINE = "§6MinecraftOffline.net §8- §cAnarchy Lifesteal Server";
    
    public MotdManager(OfflineStore plugin) {
        this.plugin = plugin;
        File databaseFile = new File(plugin.getDataFolder(), "motds.db");
        this.databaseUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(databaseUrl);
            initializeDatabase();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to SQLite database for MOTDs: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "SQLite JDBC driver not found.", e);
        }
    }

    private void initializeDatabase() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS motd_purchases (" +
                              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              "player_uuid TEXT NOT NULL, " +
                              "player_name TEXT NOT NULL, " +
                              "motd_message TEXT NOT NULL, " +
                              "purchase_time INTEGER NOT NULL, " +
                              "expiry_time INTEGER NOT NULL, " +
                              "is_active INTEGER DEFAULT 1" +
                              ");");
            
            // Create index for faster queries
            statement.execute("CREATE INDEX IF NOT EXISTS idx_motd_active_expiry ON motd_purchases(is_active, expiry_time);");
            
            plugin.getLogger().info("Database table 'motd_purchases' initialized successfully.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create 'motd_purchases' database table: " + e.getMessage(), e);
            throw e;
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    plugin.getLogger().info("MOTD database connection closed.");
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error closing MOTD database connection: " + e.getMessage(), e);
            }
        }
    }

    public boolean isValidMotdMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = message.trim();

        if (trimmed.length() > 100) {
            return false;
        }

        return VALID_MOTD_PATTERN.matcher(trimmed).matches();
    }

    public String convertColorCodes(String message) {
        if (message == null) return null;
        return message.replace('&', '§');
    }

    public String getMotdPreview(String secondLine) {
        String convertedSecondLine = convertColorCodes(secondLine);
        return FIRST_LINE + "\n" + convertedSecondLine;
    }

    public boolean addMotdPurchase(UUID playerUuid, String playerName, String motdMessage, int durationHours) {
        if (!isValidMotdMessage(motdMessage)) {
            return false;
        }

        String convertedMessage = convertColorCodes(motdMessage.trim());
        long now = Instant.now().getEpochSecond();
        long expiryTime = now + (durationHours * 3600L);

        String sql = "INSERT INTO motd_purchases (player_uuid, player_name, motd_message, purchase_time, expiry_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, playerName);
            pstmt.setString(3, convertedMessage);
            pstmt.setLong(4, now);
            pstmt.setLong(5, expiryTime);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not add MOTD purchase for " + playerUuid + ": " + e.getMessage(), e);
            return false;
        }
    }

    public List<String> getActiveMotds() {
        List<String> activeMotds = new ArrayList<>();
        long now = Instant.now().getEpochSecond();
        
        // Always include the default message
        activeMotds.add(DEFAULT_SECOND_LINE);
        
        String sql = "SELECT motd_message FROM motd_purchases WHERE is_active = 1 AND expiry_time > ? ORDER BY purchase_time DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, now);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    activeMotds.add(rs.getString("motd_message"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not retrieve active MOTDs: " + e.getMessage(), e);
        }
        
        return activeMotds;
    }

    public String getRandomMotd() {
        List<String> activeMotds = getActiveMotds();
        if (activeMotds.isEmpty()) {
            return FIRST_LINE + "\n" + DEFAULT_SECOND_LINE;
        }

        String selectedSecondLine = activeMotds.get(random.nextInt(activeMotds.size()));
        return FIRST_LINE + "\n" + selectedSecondLine;
    }

    public void cleanupExpiredMotds() {
        long now = Instant.now().getEpochSecond();
        String sql = "UPDATE motd_purchases SET is_active = 0 WHERE is_active = 1 AND expiry_time <= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, now);
            int expiredCount = pstmt.executeUpdate();
            if (expiredCount > 0) {
                plugin.getLogger().info("Marked " + expiredCount + " MOTDs as expired.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not cleanup expired MOTDs: " + e.getMessage(), e);
        }
    }

    public List<MotdInfo> getPlayerActiveMotds(UUID playerUuid) {
        List<MotdInfo> playerMotds = new ArrayList<>();
        long now = Instant.now().getEpochSecond();
        
        String sql = "SELECT id, motd_message, purchase_time, expiry_time FROM motd_purchases WHERE player_uuid = ? AND is_active = 1 AND expiry_time > ? ORDER BY purchase_time DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setLong(2, now);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MotdInfo info = new MotdInfo(
                        rs.getInt("id"),
                        rs.getString("motd_message"),
                        rs.getLong("purchase_time"),
                        rs.getLong("expiry_time")
                    );
                    playerMotds.add(info);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not retrieve player MOTDs for " + playerUuid + ": " + e.getMessage(), e);
        }
        
        return playerMotds;
    }

    public static class MotdInfo {
        private final int id;
        private final String message;
        private final long purchaseTime;
        private final long expiryTime;

        public MotdInfo(int id, String message, long purchaseTime, long expiryTime) {
            this.id = id;
            this.message = message;
            this.purchaseTime = purchaseTime;
            this.expiryTime = expiryTime;
        }

        public int getId() { return id; }
        public String getMessage() { return message; }
        public long getPurchaseTime() { return purchaseTime; }
        public long getExpiryTime() { return expiryTime; }
        
        public long getTimeUntilExpiry() {
            return expiryTime - Instant.now().getEpochSecond();
        }
        
        public String getTimeUntilExpiryFormatted() {
            long seconds = getTimeUntilExpiry();
            if (seconds <= 0) return "Expired";
            
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            long minutes = (seconds % 3600) / 60;
            
            if (days > 0) {
                return days + "d " + hours + "h";
            } else if (hours > 0) {
                return hours + "h " + minutes + "m";
            } else {
                return minutes + "m";
            }
        }
    }
}
