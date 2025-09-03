package me.imperatrixstudios.smartNPCs.managers;

import me.imperatrixstudios.smartNPCs.SmartNPCs;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final SmartNPCs plugin;
    private FileConfiguration config;

    // AI Settings
    private String provider;
    private String apiKey;
    private String model;
    private int maxTokens;
    private double temperature;

    // NPC Default Settings
    private String defaultPersonality;
    private boolean defaultMemoryEnabled;
    private int defaultMaxMemory;

    public ConfigManager() {
        this.plugin = SmartNPCs.getInstance();
    }

    /**
     * Loads the configuration from the config.yml file.
     * If the file does not exist, it creates it from the resource template.
     */
    public void loadConfig() {
        // This ensures the default config.yml is created if it doesn't exist
        plugin.saveDefaultConfig();

        // Get the config object
        config = plugin.getConfig();

        // Load AI settings, providing default values as a fallback
        this.provider = config.getString("ai.provider", "openai");
        this.apiKey = config.getString("ai.api_key", "YOUR_API_KEY");
        this.model = config.getString("ai.model", "gpt-4o-mini");
        this.maxTokens = config.getInt("ai.max_tokens", 200);
        this.temperature = config.getDouble("ai.temperature", 0.7);

        // Load NPC default settings
        this.defaultPersonality = config.getString("npc_defaults.personality", "You are a friendly villager.");
        this.defaultMemoryEnabled = config.getBoolean("npc_defaults.memory_enabled", true);
        this.defaultMaxMemory = config.getInt("npc_defaults.max_memory", 10);
    }

    /**
     * Reloads the configuration file and updates the stored values.
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }

    // --- Getters for accessing config values ---

    public String getProvider() {
        return provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDefaultPersonality() {
        return defaultPersonality;
    }

    public boolean isDefaultMemoryEnabled() {
        return defaultMemoryEnabled;
    }

    public int getDefaultMaxMemory() {
        return defaultMaxMemory;
    }
}