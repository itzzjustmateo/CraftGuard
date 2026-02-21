package de.devflare.CraftGuard.utils;

import de.devflare.CraftGuard.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class GUIUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    // ── Material mapping for workstation icons ──
    private static final Map<String, Material> TYPE_MATERIALS = Map.ofEntries(
            Map.entry("crafting", Material.CRAFTING_TABLE),
            Map.entry("anvil", Material.ANVIL),
            Map.entry("furnace", Material.FURNACE),
            Map.entry("blast-furnace", Material.BLAST_FURNACE),
            Map.entry("smoker", Material.SMOKER),
            Map.entry("enchanting", Material.ENCHANTING_TABLE),
            Map.entry("brewing", Material.BREWING_STAND),
            Map.entry("smithing", Material.SMITHING_TABLE),
            Map.entry("loom", Material.LOOM),
            Map.entry("cartography", Material.CARTOGRAPHY_TABLE),
            Map.entry("grindstone", Material.GRINDSTONE),
            Map.entry("stonecutter", Material.STONECUTTER));

    private static final Map<String, Material> PORTAL_MATERIALS = Map.of(
            "nether-portal", Material.OBSIDIAN,
            "end-portal", Material.ENDER_EYE);

    /**
     * Get the workstation material for a type
     */
    public static Material getTypeMaterial(String type) {
        return TYPE_MATERIALS.getOrDefault(type, Material.BARRIER);
    }

    /**
     * Get the portal material for a type
     */
    public static Material getPortalMaterial(String type) {
        return PORTAL_MATERIALS.getOrDefault(type, Material.BARRIER);
    }

    /**
     * Create a workstation icon item — reads name from config
     */
    public static ItemStack getWorkstationIcon(String type, boolean enabled, ConfigManager cfg) {
        Material material = TYPE_MATERIALS.getOrDefault(type, Material.BARRIER);
        return buildIconItem(material, cfg.getGuiItemName(type), enabled, cfg);
    }

    /**
     * Create a portal icon item — reads name from config
     */
    public static ItemStack getPortalIcon(String type, boolean enabled, ConfigManager cfg) {
        Material material = PORTAL_MATERIALS.getOrDefault(type, Material.BARRIER);
        return buildIconItem(material, cfg.getGuiItemName(type), enabled, cfg);
    }

    /**
     * Build an icon item with MiniMessage styling from config
     */
    private static ItemStack buildIconItem(Material material, String label, boolean enabled, ConfigManager cfg) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(noItalic(MM.deserialize(label)));

            String statusText = cfg.getGuiStatus(enabled);
            String statusPrefix = cfg.getGuiLabel("status-prefix");
            String clickHint = cfg.getGuiLabel("click-to-toggle");

            meta.lore(List.of(
                    Component.empty(),
                    noItalic(MM.deserialize(statusPrefix + statusText)),
                    Component.empty(),
                    noItalic(MM.deserialize(clickHint))));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create a status indicator item (below the icon) — reads from config
     */
    public static ItemStack getStatusIndicator(String type, boolean enabled, ConfigManager cfg) {
        Material material = enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String statusText = cfg.getGuiStatus(enabled);
            String itemName = cfg.getGuiItemName(type);
            String clickHint = cfg.getGuiLabel("click-to-toggle");

            meta.displayName(noItalic(MM.deserialize(statusText)));

            meta.lore(List.of(
                    Component.empty(),
                    noItalic(MM.deserialize("<gray>| </gray>" + itemName)),
                    noItalic(MM.deserialize(clickHint))));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create a simple MiniMessage-styled item
     */
    public static ItemStack createItem(Material material, String miniMessageName, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(noItalic(MM.deserialize(miniMessageName)));
            if (loreLines.length > 0) {
                meta.lore(java.util.Arrays.stream(loreLines)
                        .map(line -> noItalic(MM.deserialize(line)))
                        .toList());
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Dark background placeholder pane
     */
    public static ItemStack getPlaceholder() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(noItalic(Component.empty()));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Remove italic decoration from a component
     */
    private static Component noItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }
}
