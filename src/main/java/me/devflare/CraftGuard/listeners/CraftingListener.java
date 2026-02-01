package me.devflare.CraftGuard.listeners;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * Listener for crafting events
 * Blocks crafting in worlds where it is disabled
 */
public class CraftingListener implements Listener {

    private final ConfigManager configManager;

    public CraftingListener(CraftGuard plugin) {
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        // Check if player has bypass permission
        if (event.getWhoClicked() instanceof Player player) {
            if (player.hasPermission(configManager.getBypassPermission())) {
                configManager.debug("Player " + player.getName() + " bypassed crafting restriction");
                return;
            }

            // Get player's world
            String worldName = player.getWorld().getName();

            // Check if crafting is disabled in this world
            if (!configManager.isCraftingEnabled(worldName)) {
                // Cancel the crafting event
                event.setCancelled(true);
                configManager.debug("Blocked crafting for " + player.getName() + " in world: " + worldName);

                // Send notification if enabled
                if (configManager.shouldNotifyOnCraftAttempt()) {
                    Component message = MessageUtil.format(
                            configManager.getMessageWithPrefix("crafting-blocked"),
                            null,
                            player);
                    player.sendMessage(message);
                }
            }
        }
    }
}
