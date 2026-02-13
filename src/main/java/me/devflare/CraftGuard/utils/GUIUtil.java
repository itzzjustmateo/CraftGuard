package me.devflare.CraftGuard.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIUtil {

    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents
                        .add(Component.text(line).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(loreComponents);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack getToggleItem(String type, boolean enabled) {
        Material material = enabled ? Material.GREEN_WOOL : Material.RED_WOOL;
        String state = enabled ? "ENABLED" : "DISABLED";
        NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(
                    Component.text(type).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Status: ").color(NamedTextColor.GRAY).append(Component.text(state).color(color))
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Click to toggle").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC,
                    false));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack getPlaceholder() {
        return createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    }
}
