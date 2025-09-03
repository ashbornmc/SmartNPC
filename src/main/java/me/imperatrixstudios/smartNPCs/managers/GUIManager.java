package me.imperatrixstudios.smartNPCs.managers;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.imperatrixstudios.smartNPCs.SmartNPCs;
import me.imperatrixstudios.smartNPCs.data.SmartNPCData;
import me.imperatrixstudios.smartNPCs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GUIManager {

    private final SmartNPCs plugin;
    private final NPCManager npcManager;
    private final PersonalityManager personalityManager;

    public GUIManager() {
        this.plugin = SmartNPCs.getInstance();
        this.npcManager = plugin.getNpcManager();
        this.personalityManager = plugin.getPersonalityManager();
    }

    // --- Main Editor GUI ---
    public void openEditGUI(Player player, SmartNPCData npcData) {
        ChestGui gui = new ChestGui(3, Utils.colorize("&8Editing: " + npcData.getName()));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane pane = new StaticPane(1, 1, 7, 1);

        pane.addItem(createPersonalityItem(player, npcData), 0, 0);
        pane.addItem(createMemoryToggleItem(player, npcData), 3, 0);
        pane.addItem(createAIModelItem(player, npcData), 6, 0);

        gui.addPane(pane);
        gui.show(player);
    }

    // --- Personality Selector GUI ---
    public void openPersonalityGUI(Player player, SmartNPCData npcData) {
        ChestGui gui = new ChestGui(4, Utils.colorize("&8Select Personality"));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane pane = new StaticPane(0, 0, 9, 4);

        int x = 0;
        int y = 0;
        for (String templateName : personalityManager.getTemplateNames()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Utils.colorize("&b" + templateName));
            item.setItemMeta(meta);

            pane.addItem(new GuiItem(item, event -> {
                npcData.setPersonality(personalityManager.getTemplate(templateName));
                npcManager.saveNPCData(npcData);
                player.sendMessage(Utils.colorize("&aPersonality set to '" + templateName + "'."));
                openEditGUI(player, npcData);
            }), x, y);

            x++;
            if (x > 8) {
                x = 0;
                y++;
            }
        }

        // Back Button
        ItemStack backItem = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(Utils.colorize("&cBack"));
        backItem.setItemMeta(backMeta);
        pane.addItem(new GuiItem(backItem, event -> openEditGUI(player, npcData)), 4, 3);

        gui.addPane(pane);
        gui.show(player);
    }

    // --- AI Model Selector GUI ---
    public void openAIModelGUI(Player player, SmartNPCData npcData) {
        ChestGui gui = new ChestGui(3, Utils.colorize("&8Select AI Model"));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane pane = new StaticPane(0, 1, 9, 1);

        // This can be expanded in the future
        List<String> models = Arrays.asList("gpt-4o-mini", "gpt-4o", "claude-3-haiku-20240307", "gemini-1.5-flash");
        int i = 1;
        for (String model : models) {
            ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Utils.colorize("&b" + model));
            if (npcData.getAiModel().equals(model)) {
                meta.setLore(Collections.singletonList(Utils.colorize("&a✓ Currently Selected")));
            }
            item.setItemMeta(meta);

            pane.addItem(new GuiItem(item, event -> {
                npcData.setAiModel(model);
                npcManager.saveNPCData(npcData);
                player.sendMessage(Utils.colorize("&aAI Model set to '" + model + "'."));
                openEditGUI(player, npcData);
            }), i, 0);
            i += 2;
        }

        gui.addPane(pane);
        gui.show(player);
    }


    // --- GUI Item Creation Methods ---
    private GuiItem createPersonalityItem(Player player, SmartNPCData npcData) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colorize("&eChange Personality"));
        meta.setLore(Arrays.asList(" ", Utils.colorize("&7Click to select a new personality.")));
        item.setItemMeta(meta);
        return new GuiItem(item, event -> openPersonalityGUI(player, npcData));
    }

    private GuiItem createMemoryToggleItem(Player player, SmartNPCData npcData) {
        boolean isEnabled = npcData.isMemoryEnabled();
        ItemStack item = new ItemStack(isEnabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colorize("&aMemory: " + (isEnabled ? "&aEnabled" : "&cDisabled")));
        meta.setLore(Arrays.asList(" ", Utils.colorize("&7Click to toggle.")));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            npcData.setMemoryEnabled(!isEnabled);
            npcManager.saveNPCData(npcData);
            openEditGUI(player, npcData); // Refresh
        });
    }

    private GuiItem createAIModelItem(Player player, SmartNPCData npcData) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colorize("&bChange AI Model"));
        meta.setLore(Arrays.asList(
                " ",
                Utils.colorize("&7Current: &f" + npcData.getAiModel()),
                Utils.colorize("&7Click to change the AI model.")
        ));
        item.setItemMeta(meta);
        return new GuiItem(item, event -> openAIModelGUI(player, npcData));
    }
}