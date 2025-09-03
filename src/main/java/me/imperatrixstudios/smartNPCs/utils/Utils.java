package me.imperatrixstudios.smartNPCs.utils;

import org.bukkit.ChatColor;

public class Utils {

    /**
     * Translates a string with alternate color codes (&) into a colored string.
     * @param message The string to translate.
     * @return The translated string with color codes.
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
