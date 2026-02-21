package me.devflare.CraftGuard;

import me.devflare.CraftGuard.commands.CraftGuardCommand;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.listeners.ContainerListener;
import me.devflare.CraftGuard.listeners.CraftingListener;
import me.devflare.CraftGuard.listeners.PortalListener;
import me.devflare.CraftGuard.listeners.WorkstationListener;
import me.devflare.CraftGuard.placeholders.CraftGuardExpansion;
import me.devflare.CraftGuard.utils.AsyncAuditLogger;
import me.devflare.CraftGuard.utils.MessageUtil;
import me.devflare.CraftGuard.utils.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CraftGuard - World-based crafting management for Minecraft servers
 * 
 * @author DevFlare, ItzzMateo
 * @version 1.4.0
 */
public final class CraftGuard extends JavaPlugin {

    private static CraftGuard instance;
    private ConfigManager configManager;
    private me.devflare.CraftGuard.utils.GUIManager guiManager;
    private me.devflare.CraftGuard.listeners.GUIListener guiListener;
    private CraftGuardExpansion placeholderExpansion;
    private CraftingListener craftingListener;
    private PortalListener portalListener;
    private WorkstationListener workstationListener;
    private ContainerListener containerListener;
    private WorldGuardHook worldGuardHook;
    private AsyncAuditLogger auditLogger;

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardHook.registerFlag();
        }
    }

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

        // Initialize GUI Manager
        guiManager = new me.devflare.CraftGuard.utils.GUIManager(this);

        // Register command
        CraftGuardCommand commandHandler = new CraftGuardCommand(this, guiManager);
        PluginCommand command = getCommand("craftguard");
        if (command != null) {
            command.setExecutor(commandHandler);
            command.setTabCompleter(commandHandler);
            getLogger().info(configManager.getMessage("commands-registered"));
        } else {
            getLogger().severe(configManager.getMessage("command-registration-failed"));
        }

        // Initialize Hooks & Utilities
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardHook = new WorldGuardHook();
        }
        auditLogger = new AsyncAuditLogger(this);

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
        if (containerListener != null) {
            HandlerList.unregisterAll(containerListener);
            containerListener = null;
        }
        if (guiListener != null) {
            HandlerList.unregisterAll(guiListener);
            guiListener = null;
        }

        // Shutdown Logger
        if (auditLogger != null) {
            auditLogger.shutdown();
            auditLogger = null;
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
        containerListener = new ContainerListener(this);
        guiListener = new me.devflare.CraftGuard.listeners.GUIListener(this, guiManager);

        Bukkit.getPluginManager().registerEvents(craftingListener, this);
        Bukkit.getPluginManager().registerEvents(portalListener, this);
        Bukkit.getPluginManager().registerEvents(workstationListener, this);
        Bukkit.getPluginManager().registerEvents(containerListener, this);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
    }

    /**
     * Get the GUI manager
     *
     * @return GUIManager instance
     */
    public me.devflare.CraftGuard.utils.GUIManager getGuiManager() {
        return guiManager;
    }

    /**
     * Get the WorldGuard hook
     *
     * @return WorldGuardHook instance
     */
    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }

    /**
     * Get the audit logger
     *
     * @return AsyncAuditLogger instance
     */
    public AsyncAuditLogger getAuditLogger() {
        return auditLogger;
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
