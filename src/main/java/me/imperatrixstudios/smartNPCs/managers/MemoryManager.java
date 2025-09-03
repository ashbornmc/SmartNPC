package me.imperatrixstudios.smartNPCs.managers;

import me.imperatrixstudios.smartNPCs.data.ChatMessage;
import me.imperatrixstudios.smartNPCs.data.SmartNPCData;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages the short-term conversation memory for each player-NPC interaction.
 */
public class MemoryManager {

    // Key: A unique string identifier for a conversation (e.g., "playerUUID-npcId")
    // Value: A queue of the most recent chat messages in that conversation.
    private final Map<String, Deque<ChatMessage>> conversationHistories = new HashMap<>();

    /**
     * Adds a message to a specific conversation's history, trimming the oldest
     * message if the history exceeds the NPC's configured memory limit.
     *
     * @param playerData The data of the player in the conversation.
     * @param npcData The data of the NPC in the conversation.
     * @param message The ChatMessage to add.
     */
    public void addMessage(UUID playerData, SmartNPCData npcData, ChatMessage message) {
        if (!npcData.isMemoryEnabled()) {
            return; // Do nothing if memory is disabled for this NPC.
        }

        String conversationKey = generateKey(playerData, npcData.getNpcId());
        Deque<ChatMessage> history = conversationHistories.computeIfAbsent(conversationKey, k -> new LinkedList<>());

        history.addLast(message);

        // Trim the history if it's too long
        while (history.size() > npcData.getMaxMemory()) {
            history.pollFirst();
        }
    }

    /**
     * Retrieves the conversation history for a specific player and NPC.
     *
     * @param playerData The data of the player in the conversation.
     * @param npcData The data of the NPC in the conversation.
     * @return An unmodifiable list of ChatMessages representing the history.
     */
    public List<ChatMessage> getHistory(UUID playerData, SmartNPCData npcData) {
        if (!npcData.isMemoryEnabled()) {
            return Collections.emptyList();
        }
        String conversationKey = generateKey(playerData, npcData.getNpcId());
        Deque<ChatMessage> history = conversationHistories.get(conversationKey);
        return (history == null) ? Collections.emptyList() : new ArrayList<>(history);
    }

    /**
     * Clears the memory for a specific conversation.
     * @param player The player whose conversation memory should be cleared.
     * @param npcId The ID of the NPC in the conversation.
     */
    public void clearHistory(Player player, int npcId) {
        String conversationKey = generateKey(player.getUniqueId(), npcId);
        conversationHistories.remove(conversationKey);
    }

    /**
     * Generates a unique, consistent key for a conversation.
     */
    private String generateKey(UUID playerUUID, int npcId) {
        return playerUUID.toString() + "-" + npcId;
    }
}