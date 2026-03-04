package com.kyrem.core.commands;

import org.bukkit.entity.Player;

import java.util.List;

public interface SubCommand {

    String getName();

    String getDescription();

    String getSyntax();

    String getPermission();

    /**
     * The exact number of extra arguments expected (excluding the sub-command name itself),
     * or {@code -1} to disable the argument count check.
     */
    int getArgumentsQuantity();

    void perform(Player player, String[] args);

    List<String> getTabCompletation(Player player, String[] args);
}
