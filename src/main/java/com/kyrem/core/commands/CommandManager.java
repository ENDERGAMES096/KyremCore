package com.kyrem.core.commands;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility for registering {@link BaseCommand} instances with the Bukkit command system.
 */
public class CommandManager {

    private CommandManager() {}

    /**
     * Registers a {@link BaseCommand} as both the executor and tab-completer
     * for the given command name. The command must already be declared in
     * the consuming plugin's {@code plugin.yml}.
     *
     * @param plugin      the owning plugin
     * @param commandName the name of the command as declared in plugin.yml
     * @param handler     the command handler
     * @throws IllegalArgumentException if the command is not found in plugin.yml
     */
    public static void register(JavaPlugin plugin, String commandName, BaseCommand handler) {
        PluginCommand cmd = plugin.getCommand(commandName);
        if (cmd == null) {
            throw new IllegalArgumentException("Command '" + commandName + "' not found in plugin.yml");
        }
        cmd.setExecutor(handler);
        cmd.setTabCompleter(handler);
    }
}
