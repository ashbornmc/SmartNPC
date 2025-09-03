package me.imperatrixstudios.smartNPCs.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.imperatrixstudios.smartNPCs.SmartNPCs;
import me.imperatrixstudios.smartNPCs.data.ChatMessage;
import me.imperatrixstudios.smartNPCs.data.SmartNPCData;
import me.imperatrixstudios.smartNPCs.managers.AIService;
import me.imperatrixstudios.smartNPCs.managers.ConversationManager;
import me.imperatrixstudios.smartNPCs.managers.MemoryManager;
import me.imperatrixstudios.smartNPCs.managers.NPCManager;
import me.imperatrixstudios.smartNPCs.utils.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class ChatListener implements Listener {

    private final SmartNPCs plugin;
    private final ConversationManager conversationManager;
    private final MemoryManager memoryManager;
    private final NPCManager npcManager;
    private final AIService aiService;

    public ChatListener() {
        this.plugin = SmartNPCs.getInstance();
        this.conversationManager = plugin.getConversationManager();
        this.memoryManager = plugin.getMemoryManager();
        this.npcManager = plugin.getNpcManager();
        this.aiService = plugin.getAiService();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // This listener now ONLY handles conversations.
        if (!conversationManager.isInConversation(player)) {
            return;
        }

        event.setCancelled(true);
        handleConversation(player, PlainTextComponentSerializer.plainText().serialize(event.originalMessage()));
    }

    private void handleConversation(Player player, String message) {
        Integer npcId = conversationManager.getConversationPartnerId(player);
        if (npcId == null) return;

        NPC npc = plugin.getNpcRegistry().getById(npcId);
        if (npc == null) {
            conversationManager.endConversation(player);
            player.sendMessage(Utils.colorize("&cThe NPC you were talking to has disappeared."));
            return;
        }

        if (message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("quit")) {
            conversationManager.endConversation(player);
            memoryManager.clearHistory(player, npc.getId());
            player.sendMessage(Utils.colorize("&eYou have ended the conversation with " + npc.getName() + "&e."));
            return;
        }

        player.sendMessage(Utils.colorize("&7You say: &f" + message));
        player.sendMessage(Utils.colorize(npc.getName() + " &fis thinking..."));

        SmartNPCData npcData = npcManager.getSmartNPC(npcId);
        if (npcData == null) return;

        List<ChatMessage> history = memoryManager.getHistory(player.getUniqueId(), npcData);

        aiService.getResponse(npcData, history, message).whenCompleteAsync((response, throwable) -> {
            if (throwable != null) {
                player.sendMessage(Utils.colorize("&cAn API error occurred. Please check the console."));
                return;
            }

            memoryManager.addMessage(player.getUniqueId(), npcData, new ChatMessage("user", message));
            memoryManager.addMessage(player.getUniqueId(), npcData, new ChatMessage("assistant", response));

            String formattedMessage = Utils.colorize(npc.getName() + "&7 says: &f" + response);
            player.sendMessage(formattedMessage);
        }, runnable -> plugin.getServer().getScheduler().runTask(plugin, runnable));
    }
}