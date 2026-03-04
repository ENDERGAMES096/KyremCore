package com.kyrem.core.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class CustomConfig {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File folder;
    private File file;
    private FileConfiguration config;

    public CustomConfig(JavaPlugin plugin, String fileName, File folder) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.folder = folder;
        create();
    }

    public CustomConfig(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, plugin.getDataFolder());
    }

    private void create() {
        if (!folder.exists()) folder.mkdirs();
        file = new File(folder, fileName);
        if (!file.exists()) {
            saveResourceTo(folder, fileName, false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "[CustomConfig] Error saving the configuration file " + fileName, e);
        }
    }

    public void saveResourceTo(File folder, String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in the plugin jar.");
        }

        File outFile = new File(folder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(folder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                java.nio.file.Files.copy(in, outFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().info("[CustomConfig] Saved " + resourcePath + " to " + folder.getPath());
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "[CustomConfig] Error saving " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
        }
    }
}
