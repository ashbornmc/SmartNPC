package me.imperatrixstudios.smartNPCs.managers;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the state of active conversations between players and NPCs.
 */
public class ConversationManager {

    // Maps a player's UUID to the ID of the NPC they are currently talking to.
    private final Map<UUID, Integer> conversationMap = new HashMap<>();

    /**
     * Starts a new conversation for a player with a specific NPC.
     * @param player The player starting the conversation.
     * @param npc The NPC the player is talking to.
     */
    public void startConversation(Player player, NPC npc) {
        conversationMap.put(player.getUniqueId(), npc.getId());
    }

    /**
     * Ends an active conversation for a player.
     * @param player The player ending the conversation.
     */
    public void endConversation(Player player) {
        conversationMap.remove(player.getUniqueId());
    }

    /**
     * Checks if a player is currently in any conversation.
     * @param player The player to check.
     * @return true if the player is in a conversation, false otherwise.
     */
    public boolean isInConversation(Player player) {
        return conversationMap.containsKey(player.getUniqueId());
    }

    /**
     * Gets the ID of the NPC a player is currently talking to.
     * @param player The player in the conversation.
     * @return The NPC's ID, or null if the player is not in a conversation.
     */
    public Integer getConversationPartnerId(Player player) {
        return conversationMap.get(player.getUniqueId());
    }
}