package com.kyrem.core.language;

import com.kyrem.core.util.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Loads and serves translations from a YAML file inside the plugin's data folder.
 *
 * <pre>{@code
 * LanguageManager lang = new LanguageManager(this, "lang/messages.yml");
 * lang.get("welcome_message");
 * lang.get("welcome_message", Map.of("PLAYER", player.getName()));
 * }</pre>
 */
public class LanguageManager {

    private final JavaPlugin plugin;
    private final String filePath;
    private FileConfiguration config;

    public LanguageManager(JavaPlugin plugin, String filePath) {
        this.plugin = plugin;
        this.filePath = filePath;
        loadConfig();
    }

    private void loadConfig() {
        File file = new File(plugin.getDataFolder(), filePath);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            InputStream resource = plugin.getResource(filePath);
            if (resource != null) {
                plugin.saveResource(filePath, false);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        loadConfig();
    }

    /**
     * Returns the translated string for the given key, with color codes applied.
     * Returns a red error string if the key is missing.
     */
    public String get(String key) {
        String value = config.getString(key);
        if (value == null) return "§cMissing translation: " + key;
        return ChatUtils.formatMsg(value);
    }

    /**
     * Returns the translated string with placeholder substitution.
     * Placeholders in the YAML value should be written as {KEY} and
     * the map should contain the corresponding replacement values.
     *
     * <pre>{@code
     * lang.get("welcome", Map.of("PLAYER", player.getName()));
     * // YAML: welcome: "&aWelcome, {PLAYER}!"
     * }</pre>
     */
    public String get(String key, Map<String, String> placeholders) {
        String value = get(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
