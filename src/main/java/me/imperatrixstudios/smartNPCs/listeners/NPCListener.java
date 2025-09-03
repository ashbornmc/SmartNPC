package me.imperatrixstudios.smartNPCs.listeners;

import me.imperatrixstudios.smartNPCs.SmartNPCs;
import me.imperatrixstudios.smartNPCs.managers.ConversationManager;
import me.imperatrixstudios.smartNPCs.managers.NPCManager;
import me.imperatrixstudios.smartNPCs.utils.Utils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

    private final NPCManager npcManager;
    private final ConversationManager conversationManager;

    public NPCListener() {
        SmartNPCs plugin = SmartNPCs.getInstance();
        this.npcManager = plugin.getNpcManager();
        this.conversationManager = plugin.getConversationManager();
    }

    @EventHandler
    public void onRightClickNPC(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();

        // If the clicked NPC is not one of ours, do nothing.
        if (npcManager.getSmartNPC(npc.getId()) == null) {
            return;
        }

        // If player is already in a conversation, don't start a new one.
        if (conversationManager.isInConversation(player)) {
            player.sendMessage(Utils.colorize("&cYou are already in a conversation. Type 'exit' to end it."));
            return;
        }

        // Start a new conversation.
        conversationManager.startConversation(player, npc);
        player.sendMessage(Utils.colorize("&aYou are now talking to " + npc.getName() + "&a."));
        player.sendMessage(Utils.colorize("&aType your message in chat, or type '&eexit&a' to end the conversation."));
    }
}