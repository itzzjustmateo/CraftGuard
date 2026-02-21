package me.devflare.CraftGuard.commands;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.utils.GUIManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class CraftGuardCommandTest {

    private CraftGuard plugin;
    private ConfigManager configManager;
    private GUIManager guiManager;
    private CraftGuardCommand command;
    private Player player;
    private Command cmd;

    @BeforeEach
    void setUp() {
        plugin = mock(CraftGuard.class);
        configManager = mock(ConfigManager.class);
        guiManager = mock(GUIManager.class);
        player = mock(Player.class);
        cmd = mock(Command.class);

        when(plugin.getConfigManager()).thenReturn(configManager);
        command = new CraftGuardCommand(plugin, guiManager);

        when(configManager.getAdminPermission()).thenReturn("craftguard.admin");
        when(player.hasPermission("craftguard.admin")).thenReturn(true);
    }

    @Test
    void testNoArgsOpensGUIForPlayer() {
        command.onCommand(player, cmd, "cg", new String[0]);
        verify(guiManager).openMainMenu(player);
    }

    @Test
    void testInvalidUsageShowsMessage() {
        when(configManager.getMessageWithPrefix("invalid-usage")).thenReturn("Invalid usage!");
        command.onCommand(player, cmd, "cg", new String[] { "world1" });
        verify(player).sendMessage(any(Component.class));
    }

    @Test
    void testWorldNotFound() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getWorld("nonexistent")).thenReturn(null);
            when(configManager.getMessageWithPrefix("world-not-found")).thenReturn("World not found!");

            command.onCommand(player, cmd, "cg", new String[] { "nonexistent", "crafting", "on" });
            verify(player).sendMessage(any(Component.class));
        }
    }

    @Test
    void testReloadSupport() {
        when(configManager.getMessageWithPrefix("config-reloaded")).thenReturn("Reloaded!");
        command.onCommand(player, cmd, "cg", new String[] { "reload" });
        verify(configManager).reloadConfigurations();
        verify(player).sendMessage(any(Component.class));
    }
}
