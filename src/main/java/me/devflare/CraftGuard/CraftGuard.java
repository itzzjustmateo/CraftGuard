package me.devflare.CraftGuard;

import me.devflare.CraftGuard.commands.CraftGuardCommand;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.listeners.CraftingListener;
import me.devflare.CraftGuard.listeners.PortalListener;
import me.devflare.CraftGuard.listeners.WorkstationListener;
import me.devflare.CraftGuard.placeholders.CraftGuardExpansion;
import me.devflare.CraftGuard.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CraftGuard - World-based crafting management for Minecraft servers
 * 
 * @author DevFlare, ItzzMateo
 * @version 1.2.2
 */
public final class CraftGuard extends JavaPlugin {

    private static CraftGuard instance;
    private ConfigManager configManager;
    private CraftGuardExpansion placeholderExpansion;
    private CraftingListener craftingListener;
    private PortalListener portalListener;
    private WorkstationListener workstationListener;

    @Override
    public void onEnable() {
        // Warn if instance already exists (reload detection)
        if (instance != null) {
            getLogger().warning("CraftGuard instance already exists! This may indicate a reload issue.");
        }

        // Set instance
        instance = this;

        // Initialize message utility (re-checks PAPI availability)
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

        // Register event listeners
        registerListeners();
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
        // Unregister event listeners
        if (craftingListener != null) {
            HandlerList.unregisterAll(craftingListener);
            craftingListener = null;
        }
        if (portalListener != null) {
            HandlerList.unregisterAll(portalListener);
            portalListener = null;
        }
        if (workstationListener != null) {
            HandlerList.unregisterAll(workstationListener);
            workstationListener = null;
        }

        // Unregister PlaceholderAPI expansion
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
            placeholderExpansion = null;
        }

        // Log shutdown message (null-safe)
        if (configManager != null) {
            getLogger().info(configManager.getMessage("plugin-disabled"));
            configManager = null;
        } else {
            getLogger().info("CraftGuard has been disabled!");
        }

        // Reset MessageUtil static state
        MessageUtil.reset();

        // Clear static instance LAST
        instance = null;
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        craftingListener = new CraftingListener(this);
        portalListener = new PortalListener(this);
        workstationListener = new WorkstationListener(this);

        Bukkit.getPluginManager().registerEvents(craftingListener, this);
        Bukkit.getPluginManager().registerEvents(portalListener, this);
        Bukkit.getPluginManager().registerEvents(workstationListener, this);
    }

    /**
     * Get the plugin instance
     * 
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
