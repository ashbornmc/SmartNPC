package me.imperatrixstudios.smartNPCs.managers;

import me.imperatrixstudios.smartNPCs.SmartNPCs;
import me.imperatrixstudios.smartNPCs.data.SmartNPCData;
import me.imperatrixstudios.smartNPCs.database.DatabaseManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCManager {

    private final SmartNPCs plugin;
    private final DatabaseManager databaseManager;
    private final Map<Integer, SmartNPCData> npcCache = new HashMap<>();

    public NPCManager() {
        this.plugin = SmartNPCs.getInstance();
        this.databaseManager = new DatabaseManager();
    }

    public void initializeDatabase() {
        databaseManager.initialize();
    }

    public void shutdownDatabase() {
        databaseManager.closeConnection();
    }

    public NPC createNPC(Player creator, String name) {
        String formattedName = ChatColor.translateAlternateColorCodes('&', name);
        Location location = creator.getLocation();

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, formattedName);
        npc.spawn(location);

        ConfigManager config = plugin.getConfigManager();
        String personality = config.getDefaultPersonality();
        boolean memoryEnabled = config.isDefaultMemoryEnabled();
        int maxMemory = config.getDefaultMaxMemory();
        String aiModel = config.getModel(); // <-- GET THE DEFAULT MODEL

        int npcId = npc.getId();
        UUID uuid = npc.getUniqueId();
        // Updated constructor call
        SmartNPCData npcData = new SmartNPCData(npcId, uuid, name, personality, memoryEnabled, maxMemory, aiModel);

        databaseManager.saveNpc(npcData);
        npcCache.put(npcId, npcData);

        return npc;
    }

    public void saveNPCData(SmartNPCData data) {
        databaseManager.saveNpc(data);
    }

    public SmartNPCData getSmartNPC(int npcId) {
        return npcCache.get(npcId);
    }
}