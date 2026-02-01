package me.devflare.CraftGuard.config;

import me.devflare.CraftGuard.CraftGuard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages configuration files for CraftGuard plugin
 * Handles both config.yml (messages/settings) and worlds.yml (world states)
 */
public class ConfigManager {

    private final CraftGuard plugin;
    private FileConfiguration config;
    private FileConfiguration worldsConfig;
    private File worldsFile;

    // Cache for world states (thread-safe for concurrent access)
    private final Map<String, Boolean> worldStatesCache = new ConcurrentHashMap<>();

    public ConfigManager(CraftGuard plugin) {
        this.plugin = plugin;
        loadConfigurations();
    }

    /**
     * Load or create both configuration files
     */
    public void loadConfigurations() {
        // Load main config.yml
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        // Load worlds.yml
        worldsFile = new File(plugin.getDataFolder(), "worlds.yml");
        if (!worldsFile.exists()) {
            plugin.saveResource("worlds.yml", false);
        }
        worldsConfig = YamlConfiguration.loadConfiguration(worldsFile);

        // Load world states into cache
        loadWorldStatesCache();
    }

    /**
     * Reload both configuration files
     */
    public void reloadConfigurations() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        worldsConfig = YamlConfiguration.loadConfiguration(worldsFile);
        loadWorldStatesCache();
    }

    /**
     * Load world states from worlds.yml into cache
     */
    private void loadWorldStatesCache() {
        worldStatesCache.clear();
        for (String world : worldsConfig.getKeys(false)) {
            worldStatesCache.put(world, worldsConfig.getBoolean(world, getDefaultCraftingState()));
        }
    }

    /**
     * Get crafting state for a specific world
     * 
     * @param worldName The name of the world
     * @return true if crafting is enabled, false if disabled
     */
    public boolean isCraftingEnabled(String worldName) {
        return worldStatesCache.getOrDefault(worldName, getDefaultCraftingState());
    }

    /**
     * Set crafting state for a specific world
     * 
     * @param worldName The name of the world
     * @param enabled   true to enable crafting, false to disable
     */
    public void setCraftingEnabled(String worldName, boolean enabled) {
        worldStatesCache.put(worldName, enabled);
        worldsConfig.set(worldName, enabled);
        saveWorldsConfig();
    }

    /**
     * Toggle crafting state for a specific world
     * 
     * @param worldName The name of the world
     * @return The new state (true if now enabled, false if now disabled)
     */
    public boolean toggleCrafting(String worldName) {
        boolean currentState = isCraftingEnabled(worldName);
        boolean newState = !currentState;
        setCraftingEnabled(worldName, newState);
        return newState;
    }

    /**
     * Save worlds.yml to disk
     */
    private void saveWorldsConfig() {
        try {
            worldsConfig.save(worldsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save worlds.yml: " + e.getMessage());
        }
    }

    /**
     * Get a message from config.yml
     * 
     * @param path The path to the message
     * @return The message string
     */
    public String getMessage(String path) {
        return config.getString("messages." + path, "&cMessage not found: " + path);
    }

    /**
     * Get a message with prefix applied (if enabled)
     * 
     * @param path The path to the message
     * @return The message string with prefix
     */
    public String getMessageWithPrefix(String path) {
        String message = getMessage(path);

        // Don't add prefix to console messages or status messages
        if (path.startsWith("plugin-") || path.startsWith("config-") ||
                path.startsWith("commands-") || path.startsWith("listeners-") ||
                path.startsWith("placeholderapi-") || path.startsWith("command-registration-") ||
                path.startsWith("status-")) {
            return message;
        }

        // Add prefix if enabled
        if (isPrefixEnabled()) {
            return getPrefix() + message;
        }

        return message;
    }

    /**
     * Check if message prefix is enabled
     * 
     * @return true if prefix should be added to messages
     */
    public boolean isPrefixEnabled() {
        return config.getBoolean("prefix.enabled", true);
    }

    /**
     * Get the message prefix
     * 
     * @return The prefix string
     */
    public String getPrefix() {
        return config.getString("prefix.text", "&a[CraftGuard] &7");
    }

    /**
     * Get help messages list
     * 
     * @return List of help message lines
     */
    public List<String> getHelpMessages() {
        return config.getStringList("messages.help");
    }

    /**
     * Get a setting from config.yml
     * 
     * @param path         The path to the setting
     * @param defaultValue The default value if not found
     * @return The setting value
     */
    public boolean getSetting(String path, boolean defaultValue) {
        return config.getBoolean("settings." + path, defaultValue);
    }

    /**
     * Get the default crafting state for new worlds
     * 
     * @return true if crafting should be enabled by default
     */
    public boolean getDefaultCraftingState() {
        return getSetting("default-crafting-state", true);
    }

    /**
     * Check if players should be notified when trying to craft in disabled world
     * 
     * @return true if notification is enabled
     */
    public boolean shouldNotifyOnCraftAttempt() {
        return getSetting("notify-on-craft-attempt", true);
    }

    // ==================== Permission Methods ====================

    /**
     * Get the admin permission node
     * 
     * @return The permission string for admin commands
     */
    public String getAdminPermission() {
        return config.getString("permissions.admin", "craftguard.admin");
    }

    /**
     * Get the bypass permission node
     * 
     * @return The permission string for bypassing restrictions
     */
    public String getBypassPermission() {
        return config.getString("permissions.bypass", "craftguard.bypass");
    }

    // ==================== Command Configuration Methods ====================

    /**
     * Get list of enable action aliases
     * 
     * @return List of aliases for enabling crafting
     */
    public List<String> getEnableAliases() {
        return config.getStringList("commands.enable-aliases");
    }

    /**
     * Get list of disable action aliases
     * 
     * @return List of aliases for disabling crafting
     */
    public List<String> getDisableAliases() {
        return config.getStringList("commands.disable-aliases");
    }

    /**
     * Get list of toggle action aliases
     * 
     * @return List of aliases for toggling crafting
     */
    public List<String> getToggleAliases() {
        return config.getStringList("commands.toggle-aliases");
    }

    /**
     * Get all action aliases combined
     * 
     * @return List of all valid action strings
     */
    public List<String> getAllActionAliases() {
        List<String> allAliases = new ArrayList<>();
        allAliases.addAll(getEnableAliases());
        allAliases.addAll(getDisableAliases());
        allAliases.addAll(getToggleAliases());
        return allAliases;
    }

    /**
     * Check if a string is an enable action
     * 
     * @param action The action string to check
     * @return true if it's an enable action
     */
    public boolean isEnableAction(String action) {
        boolean caseSensitive = config.getBoolean("commands.case-sensitive", false);
        return getEnableAliases().stream()
                .anyMatch(alias -> caseSensitive ? alias.equals(action) : alias.equalsIgnoreCase(action));
    }

    /**
     * Check if a string is a disable action
     * 
     * @param action The action string to check
     * @return true if it's a disable action
     */
    public boolean isDisableAction(String action) {
        boolean caseSensitive = config.getBoolean("commands.case-sensitive", false);
        return getDisableAliases().stream()
                .anyMatch(alias -> caseSensitive ? alias.equals(action) : alias.equalsIgnoreCase(action));
    }

    /**
     * Check if a string is a toggle action
     * 
     * @param action The action string to check
     * @return true if it's a toggle action
     */
    public boolean isToggleAction(String action) {
        boolean caseSensitive = config.getBoolean("commands.case-sensitive", false);
        return getToggleAliases().stream()
                .anyMatch(alias -> caseSensitive ? alias.equals(action) : alias.equalsIgnoreCase(action));
    }

    /**
     * Check if a string is any valid action
     * 
     * @param action The action string to check
     * @return true if it's a valid action
     */
    public boolean isValidAction(String action) {
        return isEnableAction(action) || isDisableAction(action) || isToggleAction(action);
    }

    /**
     * Get tab completion action suggestions
     * 
     * @return List of action strings for tab completion
     */
    public List<String> getTabCompleteActions() {
        return config.getStringList("commands.tab-complete-actions");
    }

    /**
     * Check if world names should be included in tab completion
     * 
     * @return true if worlds should be suggested
     */
    public boolean shouldTabCompleteWorlds() {
        return config.getBoolean("commands.tab-complete-worlds", true);
    }

    // ==================== Event Configuration Methods ====================

    /**
     * Get the event priority for the crafting listener
     * 
     * @return EventPriority enum value
     */
    public org.bukkit.event.EventPriority getEventPriority() {
        String priority = config.getString("settings.event-priority", "HIGHEST");
        try {
            return org.bukkit.event.EventPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid event priority '" + priority + "', using HIGHEST");
            return org.bukkit.event.EventPriority.HIGHEST;
        }
    }

    /**
     * Check if cancelled events should be ignored
     * 
     * @return true if cancelled events should be ignored
     */
    public boolean shouldIgnoreCancelledEvents() {
        return config.getBoolean("settings.ignore-cancelled-events", true);
    }

    // ==================== Advanced Settings Methods ====================

    /**
     * Check if debug mode is enabled
     * 
     * @return true if debug logging is enabled
     */
    public boolean isDebugMode() {
        return config.getBoolean("advanced.debug-mode", false);
    }

    /**
     * Check if world states should be cached
     * 
     * @return true if caching is enabled
     */
    public boolean shouldCacheWorldStates() {
        return config.getBoolean("advanced.cache-world-states", true);
    }

    /**
     * Get auto-save interval in seconds
     * 
     * @return Interval in seconds (0 = immediate, -1 = disabled)
     */
    public int getAutoSaveInterval() {
        return config.getInt("advanced.auto-save-interval", 0);
    }

    /**
     * Log debug message if debug mode is enabled
     * 
     * @param message The message to log
     */
    public void debug(String message) {
        if (isDebugMode()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
}
