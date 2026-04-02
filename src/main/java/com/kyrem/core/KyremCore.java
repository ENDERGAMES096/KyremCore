package com.kyrem.core;

import com.kyrem.core.menu.MenuListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class KyremCore {

    private static JavaPlugin plugin;

    private KyremCore() {}

    public static void init(JavaPlugin plugin) {
        KyremCore.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new MenuListener(), plugin);
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
