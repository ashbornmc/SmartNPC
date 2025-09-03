package me.imperatrixstudios.smartNPCs.database;

import me.imperatrixstudios.smartNPCs.SmartNPCs;
import me.imperatrixstudios.smartNPCs.data.SmartNPCData;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class DatabaseManager {

    private final SmartNPCs plugin;
    private Connection connection;

    public DatabaseManager() {
        this.plugin = SmartNPCs.getInstance();
    }

    public void initialize() {
        File dbFile = new File(plugin.getDataFolder(), "npcs.db");
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            plugin.getLogger().info("Successfully connected to the SQLite database.");
            createTables();
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to the SQLite database!", e);
        }
    }

    private void createTables() throws SQLException {
        // Updated the table to include the ai_model column
        String sql = "CREATE TABLE IF NOT EXISTS npcs (" +
                "npc_id INTEGER PRIMARY KEY, " +
                "uuid TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "personality TEXT, " +
                "memory_enabled BOOLEAN NOT NULL, " +
                "max_memory INTEGER NOT NULL, " +
                "ai_model TEXT" + // <-- ADDED THIS
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveNpc(SmartNPCData data) {
        // Updated the SQL query to include the new field
        String sql = "INSERT OR REPLACE INTO npcs(npc_id, uuid, name, personality, memory_enabled, max_memory, ai_model) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, data.getNpcId());
            pstmt.setString(2, data.getUuid().toString());
            pstmt.setString(3, data.getName());
            pstmt.setString(4, data.getPersonality());
            pstmt.setBoolean(5, data.isMemoryEnabled());
            pstmt.setInt(6, data.getMaxMemory());
            pstmt.setString(7, data.getAiModel()); // <-- ADDED THIS
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save NPC with ID " + data.getNpcId(), e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not close the database connection!", e);
        }
    }
}