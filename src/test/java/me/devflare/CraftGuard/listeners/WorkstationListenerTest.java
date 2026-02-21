package me.devflare.CraftGuard.listeners;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.utils.AsyncAuditLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.mockito.Mockito.*;

class WorkstationListenerTest {

    private CraftGuard plugin;
    private ConfigManager configManager;
    private AsyncAuditLogger auditLogger;
    private WorkstationListener listener;
    private Player player;
    private World world;

    @BeforeEach
    void setUp() {
        plugin = mock(CraftGuard.class);
        configManager = mock(ConfigManager.class);
        auditLogger = mock(AsyncAuditLogger.class);
        player = mock(Player.class);
        world = mock(World.class);

        when(plugin.getConfigManager()).thenReturn(configManager);
        when(plugin.getAuditLogger()).thenReturn(auditLogger);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        listener = new WorkstationListener(plugin);
    }

    @Test
    void testBlockedWorkstationOnlyNotifiesOncePerTick() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getCurrentTick).thenReturn(100);

            when(configManager.isFeatureEnabled("world", "anvil")).thenReturn(false);
            when(configManager.shouldNotifyOnBlock()).thenReturn(true);
            when(configManager.getTypeName("anvil")).thenReturn("Anvil");
            when(configManager.getMessageWithPrefix("feature-blocked")).thenReturn("Blocked!");

            Block block = mock(Block.class);
            when(block.getType()).thenReturn(Material.ANVIL);

            PlayerInteractEvent event1 = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, block, null);
            PlayerInteractEvent event2 = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, block, null);

            // First interaction
            listener.onInteract(event1);
            // Second interaction in same tick
            listener.onInteract(event2);

            // Verify notification sent once
            verify(player, times(1)).sendMessage(any(net.kyori.adventure.text.Component.class));
            // Verify audit log once
            verify(auditLogger, times(1)).log(any());
        }
    }
}
