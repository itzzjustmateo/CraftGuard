package me.devflare.CraftGuard.listeners;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.utils.GUIManager;
import me.devflare.CraftGuard.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GUIListener implements Listener {

    private final CraftGuard plugin;
    private final GUIManager guiManager;

    public GUIListener(CraftGuard plugin, GUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.startsWith("CraftGuard"))
            return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR
                || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE)
            return;

        if (title.equals("CraftGuard Control")) {
            if (clicked.getType() == Material.CRAFTING_TABLE) {
                guiManager.openSubMenu(player, "Workstations", 0);
            } else if (clicked.getType() == Material.ENDER_EYE) {
                guiManager.openSubMenu(player, "Portals", 0);
            }
        } else if (title.startsWith("CraftGuard: ")) {
            String category = title.substring("CraftGuard: ".length());

            if (clicked.getType() == Material.BARRIER) {
                guiManager.openMainMenu(player);
                return;
            }

            if (clicked.getType() == Material.ARROW) {
                String name = PlainTextComponentSerializer.plainText().serialize(clicked.getItemMeta().displayName());
                int page = 0; // In a real app we'd track current page, but for now assuming 0 or simple
                              // toggle
                // TODO: Track page properly if needed, but for now we only have 1 page of items
                // based on the list
                if (name.contains("Next")) {
                    guiManager.openSubMenu(player, category, page + 1);
                } else if (name.contains("Previous")) {
                    guiManager.openSubMenu(player, category, page - 1);
                }
                return;
            }

            if (clicked.getType() == Material.GREEN_WOOL || clicked.getType() == Material.RED_WOOL) {
                String displayName = PlainTextComponentSerializer.plainText()
                        .serialize(clicked.getItemMeta().displayName());
                // Find the internal type name from the display name
                String type = findTypeByDisplayName(displayName);
                if (type != null) {
                    boolean newState = plugin.getConfigManager().toggleFeature(player.getWorld().getName(), type);

                    // Feedback
                    String stateText = newState ? plugin.getConfigManager().getMessage("status-enabled")
                            : plugin.getConfigManager().getMessage("status-disabled");
                    String typeName = plugin.getConfigManager().getTypeName(type);

                    Component message = MessageUtil.format(
                            plugin.getConfigManager().getMessageWithPrefix("feature-toggled"),
                            Map.of(
                                    "world", player.getWorld().getName(),
                                    "state", stateText,
                                    "type", typeName,
                                    "player", player.getName()),
                            player);
                    player.sendMessage(message);

                    // Refresh GUI
                    guiManager.openSubMenu(player, category, 0); // Resetting to page 0 for simplicity
                }
            }
        }
    }

    private String findTypeByDisplayName(String displayName) {
        for (String type : plugin.getConfigManager().getRegisteredTypes()) {
            if (plugin.getConfigManager().getTypeName(type).equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        return null;
    }
}
