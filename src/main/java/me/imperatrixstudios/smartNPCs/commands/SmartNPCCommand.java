package me.imperatrixstudios.smartNPCs.commands;

import me.imperatrixstudios.smartNPCs.SmartNPCs;
import me.imperatrixstudios.smartNPCs.data.SmartNPCData;
import me.imperatrixstudios.smartNPCs.managers.GUIManager;
import me.imperatrixstudios.smartNPCs.managers.NPCManager;
import me.imperatrixstudios.smartNPCs.utils.Utils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SmartNPCCommand implements CommandExecutor {

    private final SmartNPCs plugin;
    private final NPCManager npcManager;
    private final GUIManager guiManager;

    public SmartNPCCommand() {
        this.plugin = SmartNPCs.getInstance();
        this.npcManager = plugin.getNpcManager();
        this.guiManager = plugin.getGuiManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "create":
                handleCreate(sender, args);
                break;
            case "edit":
                handleEdit(sender, args);
                break;
            default:
                sender.sendMessage(Utils.colorize("&cUnknown command. Use /smartnpc for help."));
                break;
        }
        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("smartnpc.command.reload")) {
            sender.sendMessage(Utils.colorize("&cYou do not have permission to use this command."));
            return;
        }
        plugin.reload();
        sender.sendMessage(Utils.colorize("&aSmartNPCs configuration has been reloaded successfully."));
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.colorize("&cThis command can only be run by a player."));
            return;
        }
        if (!sender.hasPermission("smartnpc.command.create")) {
            sender.sendMessage(Utils.colorize("&cYou do not have permission to use this command."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Utils.colorize("&cUsage: /snpc create <name>"));
            return;
        }

        Player player = (Player) sender;
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            nameBuilder.append(args[i]).append(" ");
        }
        String npcName = nameBuilder.toString().trim();

        NPC createdNpc = npcManager.createNPC(player, npcName);
        if (createdNpc != null) {
            sender.sendMessage(Utils.colorize("&aSuccessfully created NPC '" + createdNpc.getName() + "&a' with ID: " + createdNpc.getId()));
        } else {
            sender.sendMessage(Utils.colorize("&cFailed to create NPC. Check the console for errors."));
        }
    }

    private void handleEdit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.colorize("&cThis command can only be run by a player."));
            return;
        }
        if (!sender.hasPermission("smartnpc.command.edit")) {
            sender.sendMessage(Utils.colorize("&cYou do not have permission to use this command."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Utils.colorize("&cUsage: /snpc edit <id>"));
            return;
        }

        int npcId;
        try {
            npcId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Utils.colorize("&cInvalid NPC ID. Please provide a number."));
            return;
        }

        SmartNPCData npcData = npcManager.getSmartNPC(npcId);
        if (npcData == null) {
            sender.sendMessage(Utils.colorize("&cNo Smart NPC with that ID was found."));
            return;
        }

        // Corrected method name below
        guiManager.openEditGUI((Player) sender, npcData);
    }

    private void sendHelpMessage(CommandSender sender) {
        if (!sender.hasPermission("smartnpc.admin")) {
            sender.sendMessage(Utils.colorize("&cYou do not have permission to use this command."));
            return;
        }
        sender.sendMessage(Utils.colorize("&8&m----------------------------------"));
        sender.sendMessage(Utils.colorize("&b&lSmartNPCs &7- &fCommand Help"));
        sender.sendMessage(Utils.colorize("&8&m----------------------------------"));
        sender.sendMessage(Utils.colorize("&b/snpc create <name> &7- Create a new Smart NPC."));
        sender.sendMessage(Utils.colorize("&b/snpc remove <id> &7- Remove a Smart NPC."));
        sender.sendMessage(Utils.colorize("&b/snpc edit <id> &7- Edit a Smart NPC."));
        sender.sendMessage(Utils.colorize("&b/snpc reload &7- Reload the configuration file."));
        sender.sendMessage(Utils.colorize("&8&m----------------------------------"));
    }
}