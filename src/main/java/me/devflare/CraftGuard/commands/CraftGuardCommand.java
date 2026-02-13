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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Command handler for /craftguard (aliases: /cguard, /cg)
 * Manages feature permissions per world
 */
public class CraftGuardCommand implements CommandExecutor, TabCompleter {

    private final CraftGuard plugin;
    private final ConfigManager configManager;
    private final me.devflare.CraftGuard.utils.GUIManager guiManager;

    public CraftGuardCommand(CraftGuard plugin, me.devflare.CraftGuard.utils.GUIManager guiManager) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            if (sender instanceof Player player) {
                guiManager.openMainMenu(player);
            } else {
                sender.sendMessage(MessageUtil.format(configManager.getMessage("console-must-specify-world")));
            }
            return true;
        }

        // Handle subcommands
        String firstArg = args[0].toLowerCase();
        if (firstArg.equals("help") || firstArg.equals("?")) {
            sendHelpMessage(sender);
            return true;
        }

        // Check permission for administrative actions
        if (!sender.hasPermission(configManager.getAdminPermission())) {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("no-permission"));
            sender.sendMessage(message);
            return true;
        }

        if (firstArg.equals("reload")) {
            configManager.reloadConfigurations();
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("config-reloaded"));
            sender.sendMessage(message);
            return true;
        }

        // New syntax: /cg <world> <type> <action>
        if (args.length < 2) {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("invalid-usage"));
            sender.sendMessage(message);
            return true;
        }

        String worldName = args[0];
        String type = args[1].toLowerCase();
        String action = args.length >= 3 ? args[2].toLowerCase() : "toggle";

        // Validate world
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("world-not-found"),
                    Map.of("world", worldName));
            sender.sendMessage(message);
            return true;
        }

        // Validate type
        if (!configManager.getRegisteredTypes().contains(type)) {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("invalid-type"));
            sender.sendMessage(message);
            return true;
        }

        // Execute action
        boolean newState;
        if (isEnableAction(action)) {
            configManager.setFeatureEnabled(worldName, type, true);
            newState = true;
        } else if (isDisableAction(action)) {
            configManager.setFeatureEnabled(worldName, type, false);
            newState = false;
        } else if (isToggleAction(action)) {
            newState = configManager.toggleFeature(worldName, type);
        } else {
            Component message = MessageUtil.format(
                    configManager.getMessageWithPrefix("invalid-usage"));
            sender.sendMessage(message);
            return true;
        }

        // Send feedback
        String stateText = newState ? configManager.getMessage("status-enabled")
                : configManager.getMessage("status-disabled");
        String typeName = configManager.getTypeName(type);

        String messageKey = action.contains("toggle") ? "feature-toggled"
                : (newState ? "feature-enabled" : "feature-disabled");

        Component message = MessageUtil.format(
                configManager.getMessageWithPrefix(messageKey),
                Map.of(
                        "world", worldName,
                        "state", stateText,
                        "type", typeName,
                        "player", sender.getName()),
                sender instanceof Player ? (Player) sender : null);
        sender.sendMessage(message);

        return true;
    }

    private boolean isEnableAction(String action) {
        return action.equals("on") || action.equals("enable") || action.equals("true");
    }

    private boolean isDisableAction(String action) {
        return action.equals("off") || action.equals("disable") || action.equals("false");
    }

    private boolean isToggleAction(String action) {
        return action.equals("toggle");
    }

    private void sendHelpMessage(CommandSender sender) {
        String version = plugin.getPluginMeta().getVersion();
        List<String> helpLines = configManager.getHelpMessages();

        for (String line : helpLines) {
            String formatted = line
                    .replace("{version}", version);
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
            List<String> suggestions = new ArrayList<>(Arrays.asList("help", "reload"));
            suggestions.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
            return filterSuggestions(suggestions, args[0]);
        }

        // Don't suggest feature types if first arg is a subcommand
        String firstArg = args[0].toLowerCase();
        if (firstArg.equals("help") || firstArg.equals("reload") || firstArg.equals("?")) {
            return new ArrayList<>();
        }

        if (args.length == 2) {
            List<String> suggestions = new ArrayList<>(configManager.getRegisteredTypes());
            return filterSuggestions(suggestions, args[1]);
        } else if (args.length == 3) {
            return filterSuggestions(Arrays.asList("on", "off", "toggle"), args[2]);
        }

        return new ArrayList<>();
    }

    private List<String> filterSuggestions(List<String> suggestions, String input) {
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
