package me.imperatrixstudios.smartNPCs.data;

import java.util.UUID;

public class SmartNPCData {

    private final int npcId;
    private final UUID uuid;
    private String name;
    private String personality;
    private boolean memoryEnabled;
    private int maxMemory;
    private String aiModel; // <-- ADDED THIS

    public SmartNPCData(int npcId, UUID uuid, String name, String personality, boolean memoryEnabled, int maxMemory, String aiModel) {
        this.npcId = npcId;
        this.uuid = uuid;
        this.name = name;
        this.personality = personality;
        this.memoryEnabled = memoryEnabled;
        this.maxMemory = maxMemory;
        this.aiModel = aiModel; // <-- ADDED THIS
    }

    // --- Getters ---
    public int getNpcId() { return npcId; }
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getPersonality() { return personality; }
    public boolean isMemoryEnabled() { return memoryEnabled; }
    public int getMaxMemory() { return maxMemory; }
    public String getAiModel() { return aiModel; } // <-- ADDED THIS

    // --- Setters ---
    public void setName(String name) { this.name = name; }
    public void setPersonality(String personality) { this.personality = personality; }
    public void setMemoryEnabled(boolean memoryEnabled) { this.memoryEnabled = memoryEnabled; }
    public void setMaxMemory(int maxMemory) { this.maxMemory = maxMemory; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; } // <-- ADDED THIS
}