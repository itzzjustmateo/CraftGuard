package de.devflare.CraftGuard.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.devflare.CraftGuard.CraftGuard;
import de.devflare.CraftGuard.utils.GUIManager;
import de.devflare.CraftGuard.utils.MessageUtil;

import java.util.List;
import java.util.Map;

public class GUIListener implements Listener {

    private final CraftGuard plugin;
    private final GUIManager guiManager;

    // Ordered type lists matching GUIManager slot layout
    private static final List<String> WORKSTATION_TYPES = List.of(
            "crafting", "anvil", "furnace", "blast-furnace", "smoker", "enchanting",
            "brewing", "smithing", "loom", "cartography", "grindstone", "stonecutter");

    private static final List<String> PORTAL_TYPES = List.of(
            "nether-portal", "end-portal");

    // Slot mappings matching GUIManager layout
    private static final int[] WS_ICON_ROW1 = { 10, 11, 12, 13, 14, 15, 16 };
    private static final int[] WS_STATUS_ROW1 = { 19, 20, 21, 22, 23, 24, 25 };
    private static final int[] WS_ICON_ROW2 = { 28, 29, 30, 31, 32, 33, 34 };
    private static final int[] WS_STATUS_ROW2 = { 37, 38, 39, 40, 41, 42, 43 };

    private static final int[] PORTAL_ICON_SLOTS = { 11, 15 };
    private static final int[] PORTAL_STATUS_SLOTS = { 20, 24 };

    public GUIListener(CraftGuard plugin, GUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        // All our GUIs contain the small-caps "ᴄʀᴀꜰᴛɢᴜᴀʀᴅ"
        if (!title.contains("ᴄʀᴀꜰᴛɢᴜᴀʀᴅ"))
            return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR
                || clicked.getType() == Material.BLACK_STAINED_GLASS_PANE)
            return;

        int slot = event.getRawSlot();

        // ── Main Menu ──
        if (!title.contains("»")) {
            if (clicked.getType() == Material.CRAFTING_TABLE) {
                guiManager.openSubMenu(player, "Workstations", 0);
            } else if (clicked.getType() == Material.ENDER_EYE) {
                guiManager.openSubMenu(player, "Portals", 0);
            }
            return;
        }

        // ── Sub Menus ──
        // Back button
        if (clicked.getType() == Material.BARRIER) {
            guiManager.openMainMenu(player);
            return;
        }

        // ── Workstation Menu ──
        if (title.contains("ᴡᴏʀᴋsᴛᴀᴛɪᴏɴs")) {
            String type = resolveWorkstationType(slot);
            if (type != null) {
                toggleAndRefresh(player, type, "Workstations");
            }
            return;
        }

        // ── Portal Menu ──
        if (title.contains("ᴘᴏʀᴛᴀʟꜱ")) {
            String type = resolvePortalType(slot);
            if (type != null) {
                toggleAndRefresh(player, type, "Portals");
            }
        }
    }

    /**
     * Resolve workstation type from a clicked slot (icon or indicator)
     */
    private String resolveWorkstationType(int slot) {
        // Row 1 icons (slots 10–16) → types 0–6
        for (int i = 0; i < WS_ICON_ROW1.length && i < WORKSTATION_TYPES.size(); i++) {
            if (slot == WS_ICON_ROW1[i] || slot == WS_STATUS_ROW1[i]) {
                return WORKSTATION_TYPES.get(i);
            }
        }
        // Row 2 icons (slots 28–34) → types 7–11
        for (int i = 0; i < WS_ICON_ROW2.length; i++) {
            int typeIndex = 7 + i;
            if (typeIndex >= WORKSTATION_TYPES.size())
                break;
            if (slot == WS_ICON_ROW2[i] || slot == WS_STATUS_ROW2[i]) {
                return WORKSTATION_TYPES.get(typeIndex);
            }
        }
        return null;
    }

    /**
     * Resolve portal type from a clicked slot
     */
    private String resolvePortalType(int slot) {
        for (int i = 0; i < PORTAL_TYPES.size(); i++) {
            if (slot == PORTAL_ICON_SLOTS[i] || slot == PORTAL_STATUS_SLOTS[i]) {
                return PORTAL_TYPES.get(i);
            }
        }
        return null;
    }

    /**
     * Toggle a feature and refresh the GUI
     */
    private void toggleAndRefresh(Player player, String type, String category) {
        boolean newState = plugin.getConfigManager().toggleFeature(player.getWorld().getName(), type);

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
        guiManager.openSubMenu(player, category, 0);
    }
}
