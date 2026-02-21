package de.devflare.CraftGuard.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.devflare.CraftGuard.CraftGuard;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    // Hardcoded permissions
    private static final String ADMIN_PERMISSION = "craftguard.admin";
    private static final String BYPASS_PREFIX = "craftguard.bypass.";
    private static final String BYPASS_ALL = "craftguard.bypass.*";

    // Cache for world states: WorldName -> (FeatureType -> Enabled)
    private final Map<String, ConcurrentHashMap<String, Boolean>> worldStatesCache = new ConcurrentHashMap<>();

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
        boolean migrated = false;

        for (String worldName : worldsConfig.getKeys(false)) {
            if (worldsConfig.isBoolean(worldName)) {
                // Old format migration: world: true -> world: { crafting: true }
                boolean oldState = worldsConfig.getBoolean(worldName);
                ConcurrentHashMap<String, Boolean> features = new ConcurrentHashMap<>();
                features.put("crafting", oldState);
                worldStatesCache.put(worldName, features);

                // Update worldsConfig to new format
                worldsConfig.set(worldName, null); // Clear old boolean
                worldsConfig.set(worldName + ".crafting", oldState);
                migrated = true;
                debug("Migrated world '" + worldName + "' to new format.");
            } else if (worldsConfig.isConfigurationSection(worldName)) {
                ConfigurationSection section = worldsConfig.getConfigurationSection(worldName);
                if (section != null) {
                    ConcurrentHashMap<String, Boolean> features = new ConcurrentHashMap<>();
                    for (String feature : section.getKeys(false)) {
                        features.put(feature.toLowerCase(), section.getBoolean(feature));
                    }
                    worldStatesCache.put(worldName, features);
                }
            }
        }

        if (migrated) {
            saveWorldsConfig();
        }
    }

    /**
     * Check if a feature is enabled in a world
     * 
     * @param worldName   The name of the world
     * @param featureType The type of feature (crafting, anvil, etc.)
     * @return true if enabled, false if disabled
     */
    public boolean isFeatureEnabled(String worldName, String featureType) {
        ConcurrentHashMap<String, Boolean> features = worldStatesCache.get(worldName);
        if (features == null || !features.containsKey(featureType.toLowerCase())) {
            return getDefaultState();
        }
        return features.get(featureType.toLowerCase());
    }

    /**
     * Set state for a specific feature in a world
     * 
     * @param worldName   The name of the world
     * @param featureType The type of feature
     * @param enabled     true to enable, false to disable
     */
    public void setFeatureEnabled(String worldName, String featureType, boolean enabled) {
        ConcurrentHashMap<String, Boolean> features = worldStatesCache.computeIfAbsent(worldName,
                k -> new ConcurrentHashMap<>());
        features.put(featureType.toLowerCase(), enabled);

        worldsConfig.set(worldName + "." + featureType.toLowerCase(), enabled);
        saveWorldsConfig();
    }

    /**
     * Toggle a feature state for a specific world
     * 
     * @param worldName   The name of the world
     * @param featureType The type of feature
     * @return The new state
     */
    public boolean toggleFeature(String worldName, String featureType) {
        boolean newState = !isFeatureEnabled(worldName, featureType);
        setFeatureEnabled(worldName, featureType, newState);
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
                path.startsWith("placeholderapi-") || path.startsWith("status-")) {
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
     */
    public boolean isPrefixEnabled() {
        return config.getBoolean("prefix.enabled", true);
    }

    /**
     * Get the message prefix
     */
    public String getPrefix() {
        return config.getString("prefix.text", "<green>CraftGuard</green> ");
    }

    /**
     * Get help messages list
     */
    public List<String> getHelpMessages() {
        return config.getStringList("messages.help");
    }

    /**
     * Get the display name for a feature type
     */
    public String getTypeName(String type) {
        return config.getString("messages.type-names." + type.toLowerCase(), type);
    }

    /**
     * Get all registered type keys
     */
    public Set<String> getRegisteredTypes() {
        ConfigurationSection section = config.getConfigurationSection("messages.type-names");
        return section != null ? section.getKeys(false) : Set.of("crafting", "containers");
    }

    // ==================== Module Settings ====================

    /**
     * Check if the containers module is enabled
     */
    public boolean isContainersModuleEnabled() {
        return config.getBoolean("modules.containers.enabled", true);
    }

    /**
     * Get the list of blocked container types
     */
    public List<String> getBlockedContainerTypes() {
        return config.getStringList("modules.containers.blocked-types");
    }

    /**
     * Check if WorldGuard integration is enabled
     */
    public boolean isWorldGuardEnabled() {
        return config.getBoolean("modules.worldguard.use-integration", true);
    }

    /**
     * Check if interactions should be allowed by default in regions
     */
    public boolean shouldDefaultAllowInRegions() {
        return config.getBoolean("modules.worldguard.default-allow-in-regions", false);
    }

    // ==================== Update Settings ====================

    /**
     * Check if update checking is enabled
     */
    public boolean isUpdateCheckEnabled() {
        return config.getBoolean("updates.check-for-updates", true);
    }

    /**
     * Check if auto-download is enabled
     */
    public boolean isAutoDownloadEnabled() {
        return config.getBoolean("updates.auto-download", true);
    }

    // ==================== Logging Settings ====================

    /**
     * Check if audit logging is enabled
     */
    public boolean isLoggingEnabled() {
        return config.getBoolean("logging.enabled", true);
    }

    /**
     * Get the audit log file name
     */
    public String getLogFileName() {
        return config.getString("logging.file-name", "audit_log.txt");
    }

    /**
     * Check if logging should be asynchronous
     */
    public boolean isLoggingAsync() {
        return config.getBoolean("logging.async", true);
    }

    /**
     * Get the default state for any feature
     */
    public boolean getDefaultState() {
        return config.getBoolean("settings.default-state", true);
    }

    /**
     * Check if players should be notified when blocked
     */
    public boolean shouldNotifyOnBlock() {
        return config.getBoolean("settings.notify-on-block", true);
    }

    // ==================== Permission Methods ====================

    /**
     * Get the admin permission node
     */
    public String getAdminPermission() {
        return ADMIN_PERMISSION;
    }

    /**
     * Get the bypass permission node for a type
     */
    public String getBypassPermission(String type) {
        return BYPASS_PREFIX + type.toLowerCase();
    }

    /**
     * Get the bypass all permission node
     */
    public String getBypassAllPermission() {
        return BYPASS_ALL;
    }

    // ==================== Event Configuration Methods ====================

    /**
     * Get the event priority
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
     */
    public boolean shouldIgnoreCancelledEvents() {
        return config.getBoolean("settings.ignore-cancelled-events", true);
    }

    // ==================== Advanced Settings Methods ====================

    /**
     * Check if debug mode is enabled
     */
    public boolean isDebugMode() {
        return config.getBoolean("advanced.debug-mode", false);
    }

    /**
     * Log debug message if debug mode is enabled
     */
    public void debug(String message) {
        if (isDebugMode()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    /**
     * Shutdown the manager and clear caches
     */
    public void shutdown() {
        worldStatesCache.clear();
        config = null;
        worldsConfig = null;
    }
}
