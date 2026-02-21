package de.devflare.CraftGuard.utils;

import org.bukkit.Location;
import org.bukkit.Material;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a log entry for an interaction block
 */
public record LogEntry(LocalDateTime timestamp, String world, String player, String action, Material material,
        Location location) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        return String.format("[%s] [%s] [%s] BLOCKED_ACTION: %s (%s) at %.0f, %.0f, %.0f",
                timestamp.format(FORMATTER),
                world,
                player,
                action,
                material != null ? material.name() : "N/A",
                location.getX(),
                location.getY(),
                location.getZ());
    }
}
