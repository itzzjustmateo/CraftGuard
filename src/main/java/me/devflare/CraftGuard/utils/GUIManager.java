package me.devflare.CraftGuard.utils;

import me.devflare.CraftGuard.CraftGuard;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {

    private final CraftGuard plugin;

    public GUIManager(CraftGuard plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("CraftGuard Control"));

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, GUIUtil.getPlaceholder());
        }

        inv.setItem(11,
                GUIUtil.createItem(Material.CRAFTING_TABLE, "§6Workstations", "§7Manage crafting and table access"));
        inv.setItem(15, GUIUtil.createItem(Material.ENDER_EYE, "§5Portals", "§7Manage portal interaction"));

        player.openInventory(inv);
    }

    public void openSubMenu(Player player, String category, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("CraftGuard: " + category));
        String worldName = player.getWorld().getName();

        List<String> categoryTypes = new ArrayList<>();
        if (category.equalsIgnoreCase("Workstations")) {
            categoryTypes.addAll(List.of("crafting", "anvil", "furnace", "blast-furnace", "smoker", "enchanting",
                    "brewing", "smithing", "loom", "cartography", "grindstone", "stonecutter"));
        } else if (category.equalsIgnoreCase("Portals")) {
            categoryTypes.addAll(List.of("nether-portal", "end-portal"));
        }

        // Pagination logic
        int itemsPerPage = 28;
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, categoryTypes.size());

        // Fill background
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, GUIUtil.getPlaceholder());
            }
        }

        // Center items (slots 10-16, 19-25, 28-34, 37-43)
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };

        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            String type = categoryTypes.get(i);
            boolean enabled = plugin.getConfigManager().isFeatureEnabled(worldName, type);
            String displayName = plugin.getConfigManager().getTypeName(type);
            inv.setItem(slots[slotIndex++], GUIUtil.getToggleItem(displayName, enabled));
        }

        // Navigation
        if (page > 0) {
            inv.setItem(48, GUIUtil.createItem(Material.ARROW, "§7Previous Page"));
        }
        inv.setItem(49, GUIUtil.createItem(Material.BARRIER, "§cBack to Main Menu"));
        if (endIndex < categoryTypes.size()) {
            inv.setItem(50, GUIUtil.createItem(Material.ARROW, "§7Next Page"));
        }

        player.openInventory(inv);
    }
}
