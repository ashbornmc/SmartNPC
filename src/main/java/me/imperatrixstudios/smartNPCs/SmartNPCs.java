package me.imperatrixstudios.smartNPCs;

import me.imperatrixstudios.smartNPCs.commands.SmartNPCCommand;
import me.imperatrixstudios.smartNPCs.listeners.ChatListener;
import me.imperatrixstudios.smartNPCs.listeners.NPCListener;
import me.imperatrixstudios.smartNPCs.managers.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class SmartNPCs extends JavaPlugin {

    private static SmartNPCs instance;
    private NPCRegistry npcRegistry;

    // Managers
    private ConfigManager configManager;
    private NPCManager npcManager;
    private AIService aiService;
    private GUIManager guiManager;
    private ConversationManager conversationManager;
    private MemoryManager memoryManager;
    private PersonalityManager personalityManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize Managers
        configManager = new ConfigManager();
        configManager.loadConfig();

        personalityManager = new PersonalityManager();
        personalityManager.loadTemplates();

        if (!setupCitizens()) {
            getLogger().severe("Citizens plugin not found! This plugin is required. Disabling SmartNPCs.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        npcManager = new NPCManager();
        aiService = new AIService();
        guiManager = new GUIManager();
        conversationManager = new ConversationManager();
        memoryManager = new MemoryManager();

        npcManager.initializeDatabase();

        // Register Commands and Listeners
        getCommand("smartnpc").setExecutor(new SmartNPCCommand());
        getServer().getPluginManager().registerEvents(new NPCListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        getLogger().info("SmartNPCs has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.shutdownDatabase();
        }
        getLogger().info("SmartNPCs has been disabled.");
    }

    public void reload() {
        configManager.reloadConfig();
        personalityManager.loadTemplates(); // Also reload templates on /snpc reload
        getLogger().info("Configuration and personality templates reloaded.");
    }

    private boolean setupCitizens() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null) {
            return false;
        }
        try {
            npcRegistry = CitizensAPI.getNPCRegistry();
            return npcRegistry != null;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while hooking into the Citizens API.", e);
            return false;
        }
    }

    // --- Public Getters for Accessing Components ---

    public static SmartNPCs getInstance() { return instance; }
    public NPCRegistry getNpcRegistry() { return npcRegistry; }
    public ConfigManager getConfigManager() { return configManager; }
    public NPCManager getNpcManager() { return npcManager; }
    public AIService getAiService() { return aiService; }
    public GUIManager getGuiManager() { return guiManager; }
    public ConversationManager getConversationManager() { return conversationManager; }
    public MemoryManager getMemoryManager() { return memoryManager; }
    public PersonalityManager getPersonalityManager() { return personalityManager; }
}