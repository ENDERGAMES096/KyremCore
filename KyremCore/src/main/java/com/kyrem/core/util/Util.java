package com.kyrem.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Util {

    public static boolean isInt(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Location getSimpLocation(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, 0);
    }

    public static boolean isChunkLoaded(Location location) {
        return location.getWorld().isChunkLoaded(
                location.getBlockX() >> 4,
                location.getBlockZ() >> 4
        );
    }

    public static String locToString(Location loc) {
        return loc.getX() + " " + loc.getY() + " " + loc.getZ();
    }

    public static void log(String message, boolean prefix) {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatMsg(prefix ? ("§b[§3KyremCore§b] &f" + message) : message));
    }
}
