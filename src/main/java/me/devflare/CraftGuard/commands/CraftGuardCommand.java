package me.devflare.CraftGuard.commands;

import me.devflare.CraftGuard.CraftGuard;
import me.devflare.CraftGuard.config.ConfigManager;
import me.devflare.CraftGuard.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Command handler for /craftguard (aliases: /cguard, /cg)
 * Manages crafting permissions per world
 */
public class CraftGuardCommand implements CommandExecutor, TabCompleter {

    private final CraftGuard plugin;
    private final ConfigManager configManager;

    public CraftGuardCommand(CraftGuard plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        // Check permission
        if (!sender.hasPermission(configManager.getAdminPermission())) {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("no-permission"));
            sender.sendMessage(message);
            return true;
        }

        // Determine world and action
        String worldName;
        String action;

        if (args.length == 0) {
            // /cg - show help
            sendHelpMessage(sender);
            return true;
        } else if (args.length == 1) {
            // Check if it's help command
            if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) {
                sendHelpMessage(sender);
                return true;
            }

            // /cg <action> - apply to current world
            // /cg <world> - toggle specified world
            if (sender instanceof Player player && configManager.isValidAction(args[0])) {
                worldName = player.getWorld().getName();
                action = args[0].toLowerCase();
            } else {
                worldName = args[0];
                action = "toggle";
            }
        } else if (args.length == 2) {
            // /cg <world> <action>
            worldName = args[0];
            action = args[1].toLowerCase();
        } else {
            // Too many arguments
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("invalid-usage"));
            sender.sendMessage(message);
            return true;
        }

        // Validate world exists
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("world-not-found"),
                    Map.of("world", worldName));
            sender.sendMessage(message);
            return true;
        }

        // Execute action
        boolean newState;
        String messageKey;

        if (configManager.isEnableAction(action)) {
            configManager.setCraftingEnabled(worldName, true);
            newState = true;
            messageKey = "crafting-enabled";
            configManager.debug("Enabled crafting in world: " + worldName);
        } else if (configManager.isDisableAction(action)) {
            configManager.setCraftingEnabled(worldName, false);
            newState = false;
            messageKey = "crafting-disabled";
            configManager.debug("Disabled crafting in world: " + worldName);
        } else if (configManager.isToggleAction(action)) {
            newState = configManager.toggleCrafting(worldName);
            messageKey = "crafting-toggled";
            configManager.debug("Toggled crafting in world: " + worldName + " to " + newState);
        } else {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("invalid-usage"));
            sender.sendMessage(message);
            return true;
        }

        // Send feedback message
        String stateText = newState ? configManager.getMessage("status-enabled")
                : configManager.getMessage("status-disabled");

        Component message = MessageUtil.format(
                configManager.getMessageWithPrefix(messageKey),
                Map.of(
                        "world", worldName,
                        "state", stateText,
                        "player", sender.getName()),
                sender instanceof Player ? (Player) sender : null);
        sender.sendMessage(message);

        return true;
    }

    /**
     * Send help message to the command sender
     */
    private void sendHelpMessage(CommandSender sender) {
        String version = plugin.getPluginMeta().getVersion();

        // Get help messages from config
        List<String> helpLines = configManager.getHelpMessages();

        // Send each line with placeholders replaced
        for (String line : helpLines) {
            String formatted = line
                    .replace("{version}", version)
                    .replace("{admin_permission}", configManager.getAdminPermission())
                    .replace("{bypass_permission}", configManager.getBypassPermission());

            sender.sendMessage(MessageUtil.format(formatted));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(configManager.getAdminPermission())) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            // Suggest world names and actions
            List<String> suggestions = new ArrayList<>();
            suggestions.add("help");
            suggestions.addAll(configManager.getTabCompleteActions());

            if (configManager.shouldTabCompleteWorlds()) {
                suggestions.addAll(Bukkit.getWorlds().stream()
                        .map(World::getName)
                        .collect(Collectors.toList()));
            }

            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            // Suggest actions for specified world
            return configManager.getTabCompleteActions().stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
