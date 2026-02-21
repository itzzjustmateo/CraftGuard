package de.devflare.CraftGuard.utils;

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

    // ── Small-caps display names ──
    private static final Map<String, String> TYPE_SMALL_CAPS = Map.ofEntries(
            Map.entry("crafting", "ᴄʀᴀꜰᴛɪɴɢ ᴛᴀʙʟᴇ"),
            Map.entry("anvil", "ᴀɴᴠɪʟ"),
            Map.entry("furnace", "ꜰᴜʀɴᴀᴄᴇ"),
            Map.entry("blast-furnace", "ʙʟᴀꜱᴛ ꜰᴜʀɴᴀᴄᴇ"),
            Map.entry("smoker", "ꜱᴍᴏᴋᴇʀ"),
            Map.entry("enchanting", "ᴇɴᴄʜᴀɴᴛɪɴɢ ᴛᴀʙʟᴇ"),
            Map.entry("brewing", "ʙʀᴇᴡɪɴɢ ꜱᴛᴀɴᴅ"),
            Map.entry("smithing", "ꜱᴍɪᴛʜɪɴɢ ᴛᴀʙʟᴇ"),
            Map.entry("loom", "ʟᴏᴏᴍ"),
            Map.entry("cartography", "ᴄᴀʀᴛᴏɢʀᴀᴘʜʏ ᴛᴀʙʟᴇ"),
            Map.entry("grindstone", "ɢʀɪɴᴅꜱᴛᴏɴᴇ"),
            Map.entry("stonecutter", "ꜱᴛᴏɴᴇᴄᴜᴛᴛᴇʀ"));

    // ── Portal small-caps display names ──
    private static final Map<String, String> PORTAL_SMALL_CAPS = Map.of(
            "nether-portal", "ɴᴇᴛʜᴇʀ ᴘᴏʀᴛᴀʟ",
            "end-portal", "ᴇɴᴅ ᴘᴏʀᴛᴀʟ");

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
     * Create a workstation icon item (top row)
     */
    public static ItemStack getWorkstationIcon(String type, boolean enabled) {
        Material material = TYPE_MATERIALS.getOrDefault(type, Material.BARRIER);
        String label = TYPE_SMALL_CAPS.getOrDefault(type, type);
        return buildIconItem(material, label, enabled);
    }

    /**
     * Create a portal icon item (top row)
     */
    public static ItemStack getPortalIcon(String type, boolean enabled) {
        Material material = PORTAL_MATERIALS.getOrDefault(type, Material.BARRIER);
        String label = PORTAL_SMALL_CAPS.getOrDefault(type, type);
        return buildIconItem(material, label, enabled);
    }

    /**
     * Build an icon item with MiniMessage styling
     */
    private static ItemStack buildIconItem(Material material, String label, boolean enabled) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(noItalic(MM.deserialize("<bold><aqua>" + label + "</aqua></bold>")));

            String statusColor = enabled ? "<green>" : "<red>";
            String statusText = enabled ? "ᴇɴᴀʙʟᴇᴅ" : "ᴅɪꜱᴀʙʟᴇᴅ";

            meta.lore(List.of(
                    Component.empty(),
                    noItalic(MM.deserialize("<gray>| sᴛᴀᴛᴜs: " + statusColor + statusText + "</"
                            + (enabled ? "green" : "red") + "></gray>")),
                    Component.empty(),
                    noItalic(MM.deserialize("<dark_gray>ᴄʟɪᴄᴋ ᴛᴏ ᴛᴏɢɢʟᴇ</dark_gray>"))));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create a status indicator item (bottom row, directly below the icon)
     */
    public static ItemStack getStatusIndicator(String type, boolean enabled) {
        Material material = enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String statusColor = enabled ? "<green>" : "<red>";
            String statusText = enabled ? "ᴇɴᴀʙʟᴇᴅ" : "ᴅɪꜱᴀʙʟᴇᴅ";

            String label = TYPE_SMALL_CAPS.getOrDefault(type, PORTAL_SMALL_CAPS.getOrDefault(type, type));
            meta.displayName(
                    noItalic(MM.deserialize(statusColor + statusText + "</" + (enabled ? "green" : "red") + ">")));

            meta.lore(List.of(
                    Component.empty(),
                    noItalic(MM.deserialize("<gray>| " + label + "</gray>")),
                    noItalic(MM.deserialize("<dark_gray>ᴄʟɪᴄᴋ ᴛᴏ ᴛᴏɢɢʟᴇ</dark_gray>"))));
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
     * Remove italic decoration from a component (Minecraft adds italic to custom
     * names by default)
     */
    private static Component noItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }
}
