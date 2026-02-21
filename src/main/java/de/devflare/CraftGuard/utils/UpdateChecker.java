package de.devflare.CraftGuard.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.devflare.CraftGuard.CraftGuard;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Checks Modrinth for plugin updates, notifies admins, and optionally
 * auto-downloads the JAR when the server is 2+ minor versions behind.
 */
public class UpdateChecker implements Listener {

    private static final String MODRINTH_API = "https://api.modrinth.com/v2/project/craftguard/version?loaders=[\"paper\"]&featured=true";
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final CraftGuard plugin;
    private String latestVersion;
    private String downloadUrl;
    private String downloadFileName;
    private boolean updateAvailable;
    private boolean autoDownloaded;

    public UpdateChecker(CraftGuard plugin) {
        this.plugin = plugin;
    }

    /**
     * Start the async update check schedule
     */
    public void start() {
        if (!plugin.getConfigManager().isUpdateCheckEnabled()) {
            return;
        }

        // Register join listener for admin notifications
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Initial check 5 seconds after startup, then every 6 hours (432000 ticks)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkForUpdates, 100L, 432000L);
    }

    /**
     * Perform the Modrinth API check
     */
    private void checkForUpdates() {
        try {
            URL url = URI.create(MODRINTH_API).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "DevFlare/CraftGuard/" + plugin.getPluginMeta().getVersion());
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                plugin.getConfigManager().debug("Update check failed: HTTP " + conn.getResponseCode());
                return;
            }

            String response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                response = sb.toString();
            }

            JsonArray versions = JsonParser.parseString(response).getAsJsonArray();
            if (versions.isEmpty()) {
                plugin.getConfigManager().debug("No versions found on Modrinth.");
                return;
            }

            // First element is the latest version
            JsonObject latest = versions.get(0).getAsJsonObject();
            latestVersion = latest.get("version_number").getAsString();

            // Get primary download file
            JsonArray files = latest.getAsJsonArray("files");
            for (JsonElement fileEl : files) {
                JsonObject file = fileEl.getAsJsonObject();
                boolean primary = file.has("primary") && file.get("primary").getAsBoolean();
                if (primary || files.size() == 1) {
                    downloadUrl = file.get("url").getAsString();
                    downloadFileName = file.get("filename").getAsString();
                    break;
                }
            }

            // Fallback to first file if no primary found
            if (downloadUrl == null && !files.isEmpty()) {
                JsonObject firstFile = files.get(0).getAsJsonObject();
                downloadUrl = firstFile.get("url").getAsString();
                downloadFileName = firstFile.get("filename").getAsString();
            }

            String currentVersion = plugin.getPluginMeta().getVersion();

            if (isNewerVersion(latestVersion, currentVersion)) {
                updateAvailable = true;

                // Log to console
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getLogger().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    plugin.getLogger().info("A new version of CraftGuard is available!");
                    plugin.getLogger().info("Current: v" + currentVersion + " → Latest: v" + latestVersion);
                    plugin.getLogger().info("Download: https://modrinth.com/plugin/craftguard");
                    plugin.getLogger().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                });

                // Auto-download if 2+ minor versions behind
                if (plugin.getConfigManager().isAutoDownloadEnabled()
                        && isMinorVersionsBehind(currentVersion, latestVersion, 2)) {
                    autoDownload();
                }
            } else {
                plugin.getConfigManager().debug("CraftGuard is up to date (v" + currentVersion + ").");
            }

        } catch (Exception e) {
            plugin.getConfigManager().debug("Update check failed: " + e.getMessage());
        }
    }

    /**
     * Download the latest JAR to plugins/update/
     */
    private void autoDownload() {
        if (downloadUrl == null || downloadFileName == null) {
            plugin.getConfigManager().debug("Auto-download skipped: no download URL available.");
            return;
        }

        try {
            Path updateDir = plugin.getDataFolder().getParentFile().toPath().resolve("update");
            Files.createDirectories(updateDir);
            Path target = updateDir.resolve(downloadFileName);

            plugin.getLogger().info("Auto-downloading CraftGuard v" + latestVersion + "...");

            URL fileUrl = URI.create(downloadUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setRequestProperty("User-Agent", "CraftGuard/" + plugin.getPluginMeta().getVersion());
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            autoDownloaded = true;

            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getLogger().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                plugin.getLogger().info("CraftGuard v" + latestVersion + " has been downloaded!");
                plugin.getLogger().info("Location: " + target.toAbsolutePath());
                plugin.getLogger().info("Restart the server to apply the update.");
                plugin.getLogger().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            });

        } catch (Exception e) {
            plugin.getLogger().warning("Auto-download failed: " + e.getMessage());
        }
    }

    /**
     * Notify admins on join
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!updateAvailable)
            return;

        Player player = event.getPlayer();
        if (!player.hasPermission(plugin.getConfigManager().getAdminPermission()))
            return;

        String currentVersion = plugin.getPluginMeta().getVersion();

        // Delay the message slightly so it appears after MOTD/other join messages
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(MM.deserialize(""));
            player.sendMessage(MM.deserialize(
                    "<gold><bold>ᴄʀᴀꜰᴛɢᴜᴀʀᴅ</bold></gold> <gray>» A new version is available!</gray>"));
            player.sendMessage(MM.deserialize(
                    "<gray>| ᴄᴜʀʀᴇɴᴛ: <red>v" + currentVersion + "</red> → ʟᴀᴛᴇꜱᴛ: <green>v" + latestVersion
                            + "</green></gray>"));

            if (autoDownloaded) {
                player.sendMessage(MM.deserialize(
                        "<gray>| <green>ᴀᴜᴛᴏ-ᴅᴏᴡɴʟᴏᴀᴅᴇᴅ!</green> ʀᴇsᴛᴀʀᴛ ᴛᴏ ᴀᴘᴘʟʏ.</gray>"));
            } else {
                player.sendMessage(MM.deserialize(
                        "<gray>| <click:open_url:'https://modrinth.com/plugin/craftguard'><aqua><underlined>ᴅᴏᴡɴʟᴏᴀᴅ ᴏɴ ᴍᴏᴅʀɪɴᴛʜ</underlined></aqua></click></gray>"));
            }

            player.sendMessage(MM.deserialize(""));
        }, 40L); // 2 second delay
    }

    // ── Version comparison utilities ──

    /**
     * Check if 'latest' is newer than 'current' using semver comparison
     */
    static boolean isNewerVersion(String latest, String current) {
        int[] latestParts = parseSemver(latest);
        int[] currentParts = parseSemver(current);

        for (int i = 0; i < 3; i++) {
            if (latestParts[i] > currentParts[i])
                return true;
            if (latestParts[i] < currentParts[i])
                return false;
        }
        return false; // equal
    }

    /**
     * Check if 'current' is N+ minor versions behind 'latest'
     */
    static boolean isMinorVersionsBehind(String current, String latest, int threshold) {
        int[] currentParts = parseSemver(current);
        int[] latestParts = parseSemver(latest);

        // Only compare minor when major is the same
        if (latestParts[0] != currentParts[0]) {
            // Different major → always auto-download if latest is higher
            return latestParts[0] > currentParts[0];
        }

        return (latestParts[1] - currentParts[1]) >= threshold;
    }

    /**
     * Parse a semver string "1.4.1" into [major, minor, patch]
     */
    static int[] parseSemver(String version) {
        // Strip any leading 'v' or trailing suffix like '-SNAPSHOT'
        String clean = version.replaceAll("^[vV]", "").replaceAll("-.*$", "");
        String[] parts = clean.split("\\.");
        int[] result = new int[3];
        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            try {
                result[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }
}
