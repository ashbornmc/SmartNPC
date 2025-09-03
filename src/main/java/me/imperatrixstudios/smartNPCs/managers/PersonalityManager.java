package me.imperatrixstudios.smartNPCs.managers;

import me.imperatrixstudios.smartNPCs.SmartNPCs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class PersonalityManager {

    private final SmartNPCs plugin;
    private final Map<String, String> templates = new HashMap<>();

    public PersonalityManager() {
        this.plugin = SmartNPCs.getInstance();
    }

    public void loadTemplates() {
        templates.clear();
        File templatesDir = new File(plugin.getDataFolder(), "personality_presets");

        if (!templatesDir.exists()) {
            templatesDir.mkdirs();
        }

        // Save default templates from the JAR if they don't exist
        saveDefaultTemplate("friendly_blacksmith.txt");
        saveDefaultTemplate("angry_pirate.txt");
        saveDefaultTemplate("quest_master.txt");

        // Load all .txt files from the directory
        File[] files = templatesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null) return;

        for (File file : files) {
            try {
                String content = Files.readString(file.toPath());
                String name = file.getName().replace(".txt", "");
                templates.put(name, content);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not load personality template: " + file.getName(), e);
            }
        }
        plugin.getLogger().info("Loaded " + templates.size() + " personality templates.");
    }

    private void saveDefaultTemplate(String fileName) {
        File templateFile = new File(plugin.getDataFolder(), "personality_presets/" + fileName);
        if (!templateFile.exists()) {
            plugin.saveResource("personality_presets/" + fileName, false);
        }
    }

    public String getTemplate(String name) {
        return templates.get(name);
    }

    public Set<String> getTemplateNames() {
        return Collections.unmodifiableSet(templates.keySet());
    }
}