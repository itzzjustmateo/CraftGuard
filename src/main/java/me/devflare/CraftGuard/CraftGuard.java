package me.devflare.CraftGuard;

import me.devflare.CraftGuard.commands.CraftGuardCommand;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.listeners.CraftingListener;
import me.devflare.CraftGuard.placeholders.CraftGuardExpansion;
import me.devflare.CraftGuard.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CraftGuard - World-based crafting management for Minecraft servers
 * 
 * @author DevFlare, ItzzMateo
 * @version 1.21.11-1.0.0-SNAPSHOT
 */
public final class CraftGuard extends JavaPlugin {

    private static CraftGuard instance;
    private ConfigManager configManager;
    private CraftGuardExpansion placeholderExpansion;

    @Override
    public void onEnable() {
        // Set instance
        instance = this;

        // Initialize message utility
        MessageUtil.initialize();

        // Load configurations
        configManager = new ConfigManager(this);
        getLogger().info(configManager.getMessage("config-loaded"));

        // Register command
        CraftGuardCommand commandHandler = new CraftGuardCommand(this);
        PluginCommand command = getCommand("craftguard");
        if (command != null) {
            command.setExecutor(commandHandler);
            command.setTabCompleter(commandHandler);
            getLogger().info(configManager.getMessage("commands-registered"));
        } else {
            getLogger().severe(configManager.getMessage("command-registration-failed"));
        }

        // Register event listener
        Bukkit.getPluginManager().registerEvents(new CraftingListener(this), this);
        getLogger().info(configManager.getMessage("listeners-registered"));

        // Register PlaceholderAPI expansion if available
        if (MessageUtil.isPlaceholderAPIAvailable()) {
            placeholderExpansion = new CraftGuardExpansion(this);
            if (placeholderExpansion.register()) {
                getLogger().info(configManager.getMessage("placeholderapi-registered"));
            } else {
                getLogger().warning(configManager.getMessage("placeholderapi-failed"));
            }
        } else {
            getLogger().info(configManager.getMessage("placeholderapi-not-found"));
        }

        String enabledMsg = configManager.getMessage("plugin-enabled")
                .replace("{version}", getPluginMeta().getVersion());
        getLogger().info(enabledMsg);
    }

    @Override
    public void onDisable() {
        // Unregister PlaceholderAPI expansion
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }

        getLogger().info(configManager.getMessage("plugin-disabled"));
    }

    /**
     * Get the plugin instance
     * 
     * @return CraftGuard instance
     */
    public static CraftGuard getInstance() {
        return instance;
    }

    /**
     * Get the configuration manager
     * 
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
