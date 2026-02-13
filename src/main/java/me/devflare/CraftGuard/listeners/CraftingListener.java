package me.devflare.CraftGuard.listeners;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.utils.LogEntry;
import me.devflare.CraftGuard.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Listener for crafting events
 * Blocks crafting in worlds where it is disabled
 */
public class CraftingListener implements Listener {

    private final CraftGuard plugin;
    private final ConfigManager configManager;

    public CraftingListener(CraftGuard plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        // Check for bypass permissions (Specific or All)
        if (player.hasPermission(configManager.getBypassPermission("crafting")) ||
                player.hasPermission(configManager.getBypassAllPermission())) {
            configManager.debug("Player " + player.getName() + " bypassed crafting restriction");
            return;
        }

        // Check WorldGuard bypass
        if (plugin.getConfigManager().isWorldGuardEnabled()
                && plugin.getWorldGuardHook().isBypassed(player, player.getLocation())) {
            return;
        }

        // Get player's world
        String worldName = player.getWorld().getName();

        // Check if crafting is disabled in this world
        if (!configManager.isFeatureEnabled(worldName, "crafting")) {
            // Cancel the crafting event
            event.setCancelled(true);
            configManager.debug("Blocked crafting for " + player.getName() + " in world: " + worldName);

            // Audit log
            plugin.getAuditLogger().log(new LogEntry(
                    LocalDateTime.now(),
                    worldName,
                    player.getName(),
                    "CRAFT_ITEM",
                    event.getRecipe().getResult().getType(),
                    player.getLocation()));

            // Send notification if enabled
            if (configManager.shouldNotifyOnBlock()) {
                String typeName = configManager.getTypeName("crafting");
                String messageStr = configManager.getMessageWithPrefix("feature-blocked");

                Component message = MessageUtil.format(messageStr, Map.of(
                        "world", worldName,
                        "type", typeName), player);
                player.sendMessage(message);
            }
        }
    }
}
