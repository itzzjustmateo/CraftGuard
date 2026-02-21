package de.devflare.CraftGuard.utils;

import org.bukkit.scheduler.BukkitRunnable;

import de.devflare.CraftGuard.CraftGuard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles asynchronous audit logging for CraftGuard
 */
public class AsyncAuditLogger {

    private final CraftGuard plugin;
    private final Queue<LogEntry> queue = new ConcurrentLinkedQueue<>();
    private final File logFile;
    private BukkitRunnable flushTask;

    public AsyncAuditLogger(CraftGuard plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), plugin.getConfigManager().getLogFileName());

        if (!logFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create audit log file: " + e.getMessage());
            }
        }

        startFlushTask();
    }

    /**
     * Log a blocked interaction
     */
    public void log(LogEntry entry) {
        if (!plugin.getConfigManager().isLoggingEnabled())
            return;

        if (plugin.getConfigManager().isLoggingAsync()) {
            queue.add(entry);
        } else {
            writeSynchronously(entry);
        }
    }

    private void startFlushTask() {
        flushTask = new BukkitRunnable() {
            @Override
            public void run() {
                flush();
            }
        };
        // Flush every 40 seconds (800 ticks)
        flushTask.runTaskTimerAsynchronously(plugin, 800L, 800L);
    }

    /**
     * Flush the queue to disk
     */
    public void flush() {
        if (queue.isEmpty())
            return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            LogEntry entry;
            while ((entry = queue.poll()) != null) {
                writer.write(entry.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error writing to audit log: " + e.getMessage());
        }
    }

    private void writeSynchronously(LogEntry entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(entry.toString());
            writer.newLine();
        } catch (IOException e) {
            plugin.getLogger().severe("Error writing to audit log: " + e.getMessage());
        }
    }

    /**
     * Shutdown the logger and flush any remaining entries
     */
    public void shutdown() {
        if (flushTask != null) {
            flushTask.cancel();
        }
        flush();
    }
}
