package de.devflare.CraftGuard.utils;

import de.devflare.CraftGuard.config.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.devflare.CraftGuard.CraftGuard;

import java.util.List;

public class GUIManager {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final ConfigManager cfg;

    // Workstation types in display order
    private static final List<String> WORKSTATION_TYPES = List.of(
            "crafting", "anvil", "furnace", "blast-furnace", "smoker", "enchanting",
            "brewing", "smithing", "loom", "cartography", "grindstone", "stonecutter");

    // Portal types in display order
    private static final List<String> PORTAL_TYPES = List.of(
            "nether-portal", "end-portal");

    public GUIManager(CraftGuard plugin) {
        this.cfg = plugin.getConfigManager();
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27,
                MM.deserialize(cfg.getGuiTitle("main-menu")));

        // Fill background
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, GUIUtil.getPlaceholder());
        }

        String clickToOpen = cfg.getGuiLabel("click-to-open");

        inv.setItem(11, GUIUtil.createItem(Material.CRAFTING_TABLE,
                cfg.getGuiCategoryName("workstations"),
                "",
                cfg.getGuiCategoryLore("workstations"),
                "",
                clickToOpen));

        inv.setItem(15, GUIUtil.createItem(Material.ENDER_EYE,
                cfg.getGuiCategoryName("portals"),
                "",
                cfg.getGuiCategoryLore("portals"),
                "",
                clickToOpen));

        player.openInventory(inv);
    }

    public void openSubMenu(Player player, String category, int page) {
        String worldName = player.getWorld().getName();

        if (category.equalsIgnoreCase("Workstations")) {
            openWorkstationMenu(player, worldName);
        } else if (category.equalsIgnoreCase("Portals")) {
            openPortalMenu(player, worldName);
        }
    }

    private void openWorkstationMenu(Player player, String worldName) {
        Inventory inv = Bukkit.createInventory(null, 54,
                MM.deserialize(cfg.getGuiTitle("workstations")));

        // Fill entire background
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, GUIUtil.getPlaceholder());
        }

        // Row 2: workstation icons (slots 10–16), Row 3: status indicators (slots
        // 19–25)
        // Row 4: overflow icons (slots 28–34), Row 5: overflow indicators (slots 37–43)
        int[] iconSlotsRow1 = { 10, 11, 12, 13, 14, 15, 16 };
        int[] statusSlotsRow1 = { 19, 20, 21, 22, 23, 24, 25 };
        int[] iconSlotsRow2 = { 28, 29, 30, 31, 32, 33, 34 };
        int[] statusSlotsRow2 = { 37, 38, 39, 40, 41, 42, 43 };

        for (int i = 0; i < WORKSTATION_TYPES.size(); i++) {
            String type = WORKSTATION_TYPES.get(i);
            boolean enabled = cfg.isFeatureEnabled(worldName, type);

            if (i < 7) {
                inv.setItem(iconSlotsRow1[i], GUIUtil.getWorkstationIcon(type, enabled, cfg));
                inv.setItem(statusSlotsRow1[i], GUIUtil.getStatusIndicator(type, enabled, cfg));
            } else {
                int j = i - 7;
                inv.setItem(iconSlotsRow2[j], GUIUtil.getWorkstationIcon(type, enabled, cfg));
                inv.setItem(statusSlotsRow2[j], GUIUtil.getStatusIndicator(type, enabled, cfg));
            }
        }

        // Navigation — bottom row
        inv.setItem(49, GUIUtil.createItem(Material.BARRIER, cfg.getGuiLabel("back")));

        player.openInventory(inv);
    }

    private void openPortalMenu(Player player, String worldName) {
        Inventory inv = Bukkit.createInventory(null, 27,
                MM.deserialize(cfg.getGuiTitle("portals")));

        // Fill background
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, GUIUtil.getPlaceholder());
        }

        // Centered: slots 11 and 15 for two portals
        for (int i = 0; i < PORTAL_TYPES.size(); i++) {
            String type = PORTAL_TYPES.get(i);
            boolean enabled = cfg.isFeatureEnabled(worldName, type);
            int iconSlot = (i == 0) ? 11 : 15;
            int statusSlot = iconSlot + 9; // directly below

            inv.setItem(iconSlot, GUIUtil.getPortalIcon(type, enabled, cfg));
            inv.setItem(statusSlot, GUIUtil.getStatusIndicator(type, enabled, cfg));
        }

        // Navigation
        inv.setItem(22, GUIUtil.createItem(Material.BARRIER, cfg.getGuiLabel("back")));

        player.openInventory(inv);
    }
}
