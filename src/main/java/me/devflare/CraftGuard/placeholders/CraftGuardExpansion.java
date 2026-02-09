package me.devflare.CraftGuard.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlaceholderAPI expansion for CraftGuard
 * Provides placeholders for world crafting states
 */
public class CraftGuardExpansion extends PlaceholderExpansion {

    private final CraftGuard plugin;
    private final ConfigManager configManager;

    public CraftGuardExpansion(CraftGuard plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "craftguard";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return false; // Re-register fresh expansion on each reload to avoid stale references
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        // %craftguard_world% - Current world name
        if (params.equals("world")) {
            return player != null ? player.getWorld().getName() : "";
        }

        // %craftguard_world_state% - Crafting state for current world
        if (params.equals("world_state")) {
            if (player == null)
                return "";
            String worldName = player.getWorld().getName();
            boolean enabled = configManager.isFeatureEnabled(worldName, "crafting");
            return enabled ? "enabled" : "disabled";
        }

        // %craftguard_world_state_<type>% - State for a specific type in current world
        if (params.startsWith("world_state_")) {
            if (player == null)
                return "";
            String type = params.substring(12).toLowerCase();
            String worldName = player.getWorld().getName();
            boolean enabled = configManager.isFeatureEnabled(worldName, type);
            return enabled ? "enabled" : "disabled";
        }

        // %craftguard_world_<worldname>_<type>% - Specific world and specific type
        if (params.startsWith("world_")) {
            String sub = params.substring(6);
            int lastUnderscore = sub.lastIndexOf('_');

            if (lastUnderscore != -1) {
                String worldName = sub.substring(0, lastUnderscore);
                String type = sub.substring(lastUnderscore + 1);
                boolean enabled = configManager.isFeatureEnabled(worldName, type);
                return enabled ? "enabled" : "disabled";
            } else {
                // Old format: %craftguard_world_<worldname>% defaults to crafting
                boolean enabled = configManager.isFeatureEnabled(sub, "crafting");
                return enabled ? "enabled" : "disabled";
            }
        }

        return null; // Placeholder not recognized
    }
}
