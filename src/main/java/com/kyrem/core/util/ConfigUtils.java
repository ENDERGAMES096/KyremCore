package com.kyrem.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    public static ArrayList<PotionEffect> getPotionEffects(FileConfiguration config, String path) {
        ArrayList<PotionEffect> potionEffects = new ArrayList<>();

        for (String potionEffect : config.getStringList(path)) {
            String[] parts = potionEffect.split(";");

            if (parts.length >= 3) {
                String effectName = parts[0].trim();
                int amplifier = Integer.parseInt(parts[1].trim());
                int duration = Integer.parseInt(parts[2].trim());

                PotionEffectType type = PotionEffectType.getByName(effectName.toUpperCase());

                if (type != null) {
                    potionEffects.add(new PotionEffect(type, duration * 20, amplifier, false, false, false));
                } else {
                    Util.log("Effect not valid: " + effectName + " in: " + path, true);
                }
            }
        }
        return potionEffects;
    }

    public static Location getLoc(String path, FileConfiguration configuration) {
        ConfigurationSection locSec = configuration.getConfigurationSection(path);
        if (locSec == null) {
            Util.log("&cLocation section not found at path: " + path, true);
            return null;
        }

        String worldName = locSec.getString("world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Util.log("&cWorld '" + worldName + "' not found for path: " + path, true);
            return null;
        }

        double x = locSec.getDouble("x");
        double y = locSec.getDouble("y");
        double z = locSec.getDouble("z");
        float yaw = (float) locSec.getDouble("yaw", 0.0);
        float pitch = (float) locSec.getDouble("pitch", 0.0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Reads a list of locations from a YAML list structure.
     * Supports format:
     * locations:
     *   - world: "world"
     *     x: 10
     *     y: 65
     *     z: 5
     *     yaw: 0
     *     pitch: 0
     */
    public static List<Location> getLocations(String path, FileConfiguration configuration) {
        List<Location> locations = new ArrayList<>();

        List<?> locList = configuration.getList(path);
        if (locList == null || locList.isEmpty()) {
            Util.log("&cNo locations found at path: " + path, true);
            return locations;
        }

        for (Object obj : locList) {
            if (obj instanceof Map<?, ?> map) {
                try {
                    String worldName = (String) map.get("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        Util.log("&cWorld '" + worldName + "' not found in locations list at: " + path, true);
                        continue;
                    }

                    double x = ((Number) map.get("x")).doubleValue();
                    double y = ((Number) map.get("y")).doubleValue();
                    double z = ((Number) map.get("z")).doubleValue();
                    float yaw = map.containsKey("yaw") ? ((Number) map.get("yaw")).floatValue() : 0f;
                    float pitch = map.containsKey("pitch") ? ((Number) map.get("pitch")).floatValue() : 0f;

                    locations.add(new Location(world, x, y, z, yaw, pitch));
                } catch (Exception e) {
                    Util.log("&cError parsing location in list at: " + path + " - " + e.getMessage(), true);
                }
            }
        }

        return locations;
    }
}
