package com.kyrem.core.commands;

import com.kyrem.core.util.ChatUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link SubCommand} that delegates to a list of child {@link SubCommand}s.
 * Extend this when a top-level argument (e.g. "mafia") has its own sub-arguments
 * (e.g. "mafia create", "mafia leave").
 */
public abstract class ParentCommand implements SubCommand {

    private final List<SubCommand> subCommands = new ArrayList<>();

    protected void addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public List<String> getTabCompletation(Player player, String[] args) {
        if (args.length == 2) {
            return subCommands.stream()
                    .map(SubCommand::getName)
                    .toList();
        } else if (args.length > 2) {
            for (SubCommand sub : subCommands) {
                if (sub.getName().equalsIgnoreCase(args[1])) {
                    return sub.getTabCompletation(player, args);
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendPlainMessage(ChatUtils.formatMsg("&cCorrect usage: " + getSyntax()));
            return;
        }

        for (SubCommand sub : subCommands) {
            if (sub.getName().equalsIgnoreCase(args[1])) {
                if (sub.getArgumentsQuantity() != -1 && args.length != sub.getArgumentsQuantity() + 2) {
                    player.sendPlainMessage(ChatUtils.formatMsg("&cCorrect usage: " + sub.getSyntax()));
                    return;
                }
                sub.perform(player, args);
                return;
            }
        }

        player.sendPlainMessage(ChatUtils.formatMsg("&cCorrect usage: " + getSyntax()));
    }
}
