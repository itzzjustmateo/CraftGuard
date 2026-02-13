package me.devflare.CraftGuard.listeners;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.utils.LogEntry;
import me.devflare.CraftGuard.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Blocks container interactions in restricted worlds
 */
public class ContainerListener implements Listener {

    private final CraftGuard plugin;

    public ContainerListener(CraftGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Material material = block.getType();
        if (!plugin.getConfigManager().isContainersModuleEnabled())
            return;

        List<String> blockedMaterials = plugin.getConfigManager().getBlockedContainerTypes();
        if (!blockedMaterials.contains(material.name()))
            return;

        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();

        // 1. Check for bypass permissions
        if (player.hasPermission("craftguard.bypass.containers") ||
                player.hasPermission("craftguard.bypass.*")) {
            return;
        }

        // 2. Check WorldGuard integration
        if (plugin.getConfigManager().isWorldGuardEnabled()
                && plugin.getWorldGuardHook().isBypassed(player, block.getLocation())) {
            return;
        }

        // 3. Check if containers are disabled in this world (Using "containers" as
        // feature type)
        if (!plugin.getConfigManager().isFeatureEnabled(worldName, "containers")) {
            event.setCancelled(true);

            // Log interaction
            plugin.getAuditLogger().log(new LogEntry(
                    LocalDateTime.now(),
                    worldName,
                    player.getName(),
                    "OPEN_CONTAINER",
                    material,
                    block.getLocation()));

            // Notify player
            if (plugin.getConfigManager().shouldNotifyOnBlock()) {
                String typeName = plugin.getConfigManager().getTypeName("containers");
                String messageStr = plugin.getConfigManager().getMessageWithPrefix("feature-blocked");

                Component message = MessageUtil.format(messageStr, Map.of(
                        "world", worldName,
                        "type", typeName), player);
                player.sendMessage(message);
            }
        }
    }
}
