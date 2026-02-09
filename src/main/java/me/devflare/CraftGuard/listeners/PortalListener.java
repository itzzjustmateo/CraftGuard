package me.devflare.CraftGuard.listeners;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Listener for portal events
 * Blocks portal usage in worlds where it is disabled
 */
public class PortalListener implements Listener {

    private final ConfigManager configManager;

    public PortalListener(CraftGuard plugin) {
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortalEnter(PlayerPortalEvent event) {
        handlePortal(event.getPlayer(), event, event.getCause());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        handlePortal(event.getPlayer(), event, event.getCause());
    }

    private void handlePortal(Player player, PlayerTeleportEvent event, PlayerTeleportEvent.TeleportCause cause) {
        String type;
        if (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            type = "nether-portal";
        } else if (cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            type = "end-portal";
        } else {
            return;
        }

        // Check for bypass
        if (player.hasPermission(configManager.getBypassPermission(type)) ||
                player.hasPermission(configManager.getBypassAllPermission())) {
            return;
        }

        // Check if enabled
        String worldName = player.getWorld().getName();
        if (!configManager.isFeatureEnabled(worldName, type)) {
            event.setCancelled(true);
            configManager.debug("Blocked portal (" + type + ") for " + player.getName() + " in world: " + worldName);

            if (configManager.shouldNotifyOnBlock()) {
                String typeName = configManager.getTypeName(type);
                String messageStr = configManager.getMessageWithPrefix("feature-blocked")
                        .replace("{type}", typeName);

                Component message = MessageUtil.format(messageStr, null, player);
                player.sendMessage(message);
            }
        }
    }
}
