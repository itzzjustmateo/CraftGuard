package me.devflare.CraftGuard.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

/**
 * Handles integration with WorldGuard API
 */
public class WorldGuardHook {

    public static StateFlag INTERACTION_BYPASS_FLAG;
    private final boolean enabled;

    public WorldGuardHook() {
        this.enabled = org.bukkit.Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    /**
     * Register the custom flag. MUST be called in JavaPlugin#onLoad()
     */
    public static void registerFlag() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            try {
                StateFlag flag = new StateFlag("cg-interaction-bypass", false);
                registry.register(flag);
                INTERACTION_BYPASS_FLAG = flag;
            } catch (FlagConflictException e) {
                Flag<?> existing = registry.get("cg-interaction-bypass");
                if (existing instanceof StateFlag) {
                    INTERACTION_BYPASS_FLAG = (StateFlag) existing;
                }
            }
        } catch (NoClassDefFoundError ignored) {
            // WorldGuard not installed
        }
    }

    /**
     * Check if a player is bypassed in a specific location via WorldGuard flag
     */
    public boolean isBypassed(Player player, org.bukkit.Location loc) {
        if (!enabled || INTERACTION_BYPASS_FLAG == null)
            return false;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location wgLoc = BukkitAdapter.adapt(loc);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(wgLoc);

        return set.testState(localPlayer, INTERACTION_BYPASS_FLAG);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
