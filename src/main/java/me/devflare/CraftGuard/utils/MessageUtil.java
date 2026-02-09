package me.devflare.CraftGuard.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Utility class for formatting messages with color codes and placeholders
 * Supports both legacy color codes (&) and MiniMessage formatting
 */
public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static boolean placeholderAPIAvailable = false;

    /**
     * Initialize the message utility
     * Checks if PlaceholderAPI is available
     */
    public static void initialize() {
        placeholderAPIAvailable = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * Format a message with color codes and placeholders
     * 
     * @param message      The raw message string
     * @param placeholders Map of placeholders to replace
     * @param player       The player (for PlaceholderAPI support)
     * @return Formatted Adventure Component
     */
    public static Component format(String message, Map<String, String> placeholders, Player player) {
        // Replace custom placeholders (supports both {key} and <key> syntax)
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                message = message.replace("{" + key + "}", value);
                message = message.replace("<" + key + ">", value);
            }
        }

        // Process PlaceholderAPI placeholders if available
        if (placeholderAPIAvailable && player != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        // Check if message contains MiniMessage tags
        if (message.contains("<") && message.contains(">")) {
            // First convert legacy codes to MiniMessage format
            message = convertLegacyToMiniMessage(message);
            return MINI_MESSAGE.deserialize(message);
        } else {
            // Use legacy serializer for & codes
            return LEGACY_SERIALIZER.deserialize(message);
        }
    }

    /**
     * Format a message without player context
     * 
     * @param message      The raw message string
     * @param placeholders Map of placeholders to replace
     * @return Formatted Adventure Component
     */
    public static Component format(String message, Map<String, String> placeholders) {
        return format(message, placeholders, null);
    }

    /**
     * Format a message without placeholders
     * 
     * @param message The raw message string
     * @return Formatted Adventure Component
     */
    public static Component format(String message) {
        return format(message, null, null);
    }

    /**
     * Convert legacy color codes to MiniMessage format
     * This allows mixing both formats in the same message
     * 
     * @param message The message with legacy codes
     * @return Message with MiniMessage tags
     */
    private static String convertLegacyToMiniMessage(String message) {
        return message
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");
    }

    /**
     * Check if PlaceholderAPI is available
     * 
     * @return true if PlaceholderAPI is loaded
     */
    public static boolean isPlaceholderAPIAvailable() {
        return placeholderAPIAvailable;
    }

    /**
     * Reset static state on plugin disable
     * This prevents stale state after reload
     */
    public static void reset() {
        placeholderAPIAvailable = false;
    }
}
