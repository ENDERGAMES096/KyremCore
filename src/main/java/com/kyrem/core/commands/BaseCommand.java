package com.kyrem.core.commands;

import com.kyrem.core.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Abstract base for top-level plugin commands. Extend this class, register
 * sub-commands in the constructor via {@link #addSubCommand}, then register
 * the instance with {@link CommandManager#register}.
 *
 * <pre>{@code
 * public class MyCommand extends BaseCommand {
 *     public MyCommand() {
 *         addSubCommand(new HelpSubCommand());
 *         addSubCommand(new ReloadSubCommand());
 *     }
 * }
 *
 * // in onEnable():
 * CommandManager.register(this, "mycommand", new MyCommand());
 * }</pre>
 */
public abstract class BaseCommand implements TabExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();

    /**
     * Whether this command can only be executed by players (not console).
     * Override and return {@code false} to allow console usage.
     */
    protected boolean isPlayerOnly() {
        return true;
    }

    /**
     * The top-level permission required to use this command at all,
     * or an empty string if no top-level permission is needed.
     */
    protected String getPermission() {
        return "";
    }

    protected void addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.formatMsg("&cThis command can only be used by players."));
            return true;
        }

        if (!(sender instanceof Player player)) return true;

        if (!getPermission().isEmpty() && !player.hasPermission(getPermission())) {
            player.sendPlainMessage(ChatUtils.formatMsg("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            onNoArgs(player);
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (!args[0].equalsIgnoreCase(subCommand.getName())) continue;

            if (!subCommand.getPermission().isEmpty() && !player.hasPermission(subCommand.getPermission())) {
                player.sendPlainMessage(ChatUtils.formatMsg("&cYou don't have permission to use this sub-command."));
                return true;
            }

            if (subCommand.getArgumentsQuantity() != -1 && args.length != subCommand.getArgumentsQuantity() + 1) {
                player.sendPlainMessage(ChatUtils.formatMsg("&cCorrect usage: " + subCommand.getSyntax()));
                return true;
            }

            subCommand.perform(player, args);
            return true;
        }

        player.sendPlainMessage(ChatUtils.formatMsg("&cUnknown sub-command. Use /" + label + " help."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return null;

        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (SubCommand sub : subCommands) {
                if (sub.getPermission().isEmpty() || player.hasPermission(sub.getPermission()))
                    names.add(sub.getName());
            }
            return new ArrayList<>(new HashSet<>(names));
        }

        if (args.length >= 2) {
            for (SubCommand sub : subCommands) {
                if (args[0].equalsIgnoreCase(sub.getName())) {
                    if (sub.getPermission().isEmpty() || player.hasPermission(sub.getPermission()))
                        return sub.getTabCompletation(player, args).stream()
                                .filter(s -> s.startsWith(args[args.length - 1].toLowerCase()))
                                .toList();
                }
            }
        }

        return null;
    }

    /**
     * Called when the command is run with no arguments.
     * Default implementation shows "correct usage". Override to customize.
     */
    protected void onNoArgs(Player player) {
        player.sendPlainMessage(ChatUtils.formatMsg("&cUse /" + getClass().getSimpleName().toLowerCase() + " help for a list of sub-commands."));
    }
}
