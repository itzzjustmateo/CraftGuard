package me.devflare.CraftGuard.listeners;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
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
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener for workstation events
 * Blocks usage of specific workstations in worlds where they are disabled
 */
public class WorkstationListener implements Listener {

    private final CraftGuard plugin;
    private final ConfigManager configManager;
    private final Map<Material, String> materialToType = new HashMap<>();
    private final Map<InventoryType, String> inventoryToType = new HashMap<>();

    public WorkstationListener(CraftGuard plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        initializeMappings();
    }

    private void initializeMappings() {
        // Material to type mapping for PlayerInteractEvent
        materialToType.put(Material.ANVIL, "anvil");
        materialToType.put(Material.CHIPPED_ANVIL, "anvil");
        materialToType.put(Material.DAMAGED_ANVIL, "anvil");
        materialToType.put(Material.FURNACE, "furnace");
        materialToType.put(Material.BLAST_FURNACE, "blast-furnace");
        materialToType.put(Material.SMOKER, "smoker");
        materialToType.put(Material.CRAFTING_TABLE, "crafting");
        materialToType.put(Material.ENCHANTING_TABLE, "enchanting");
        materialToType.put(Material.BREWING_STAND, "brewing");
        materialToType.put(Material.SMITHING_TABLE, "smithing");
        materialToType.put(Material.LOOM, "loom");
        materialToType.put(Material.CARTOGRAPHY_TABLE, "cartography");
        materialToType.put(Material.GRINDSTONE, "grindstone");
        materialToType.put(Material.STONECUTTER, "stonecutter");

        // InventoryType to type mapping for InventoryOpenEvent
        inventoryToType.put(InventoryType.ANVIL, "anvil");
        inventoryToType.put(InventoryType.FURNACE, "furnace");
        inventoryToType.put(InventoryType.BLAST_FURNACE, "blast-furnace");
        inventoryToType.put(InventoryType.SMOKER, "smoker");
        inventoryToType.put(InventoryType.WORKBENCH, "crafting");
        inventoryToType.put(InventoryType.ENCHANTING, "enchanting");
        inventoryToType.put(InventoryType.BREWING, "brewing");
        inventoryToType.put(InventoryType.SMITHING, "smithing");
        inventoryToType.put(InventoryType.LOOM, "loom");
        inventoryToType.put(InventoryType.CARTOGRAPHY, "cartography");
        inventoryToType.put(InventoryType.GRINDSTONE, "grindstone");
        inventoryToType.put(InventoryType.STONECUTTER, "stonecutter");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        String type = materialToType.get(block.getType());
        if (type != null) {
            checkAndBlock(event.getPlayer(), event, type);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player))
            return;

        String type = inventoryToType.get(event.getInventory().getType());
        if (type != null) {
            checkAndBlock(player, event, type);
        }
    }

    private void checkAndBlock(Player player, org.bukkit.event.Cancellable event, String type) {
        // Check for bypass
        if (player.hasPermission(configManager.getBypassPermission(type)) ||
                player.hasPermission(configManager.getBypassAllPermission())) {
            return;
        }

        // Check WorldGuard bypass
        if (plugin.getConfigManager().isWorldGuardEnabled()
                && plugin.getWorldGuardHook().isBypassed(player, player.getLocation())) {
            return;
        }

        // Check if enabled
        String worldName = player.getWorld().getName();
        if (!configManager.isFeatureEnabled(worldName, type)) {
            event.setCancelled(true);
            configManager
                    .debug("Blocked workstation (" + type + ") for " + player.getName() + " in world: " + worldName);

            // Audit log
            plugin.getAuditLogger().log(new LogEntry(
                    LocalDateTime.now(),
                    worldName,
                    player.getName(),
                    "USE_WORKSTATION",
                    null, // Block type is handled via "type" string in workstations
                    player.getLocation()));

            if (configManager.shouldNotifyOnBlock()) {
                String typeName = configManager.getTypeName(type);
                String messageStr = configManager.getMessageWithPrefix("feature-blocked");

                Component message = MessageUtil.format(messageStr, Map.of(
                        "world", worldName,
                        "type", typeName), player);
                player.sendMessage(message);
            }
        }
    }
}
