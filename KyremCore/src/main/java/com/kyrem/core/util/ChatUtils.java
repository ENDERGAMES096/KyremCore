package com.kyrem.core.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {

    private static final int CENTER_PX = 154;

    public static String formatMsg(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void sendUsageOfCommand(String syntax, String description, Player player) {
        TextComponent component = new TextComponent(ChatUtils.formatMsg("&a" + syntax + "&7 • &e" + description));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click to put the command in the chat").color(net.md_5.bungee.api.ChatColor.GRAY).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, syntax));
        player.spigot().sendMessage(component);
    }

    public static void sendMessageCommand(Player player, String msg, String commandSyntax, String description) {
        TextComponent component = new TextComponent(ChatUtils.formatMsg(msg));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(description).color(net.md_5.bungee.api.ChatColor.GRAY).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSyntax));
        player.spigot().sendMessage(component);
    }

    public static String centerMessage(String message) {
        String[] lines = formatMsg(message).split("\n", 40);
        StringBuilder returnMessage = new StringBuilder();

        for (String line : lines) {
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : line.toCharArray()) {
                if (c == '§') {
                    previousCode = true;
                } else if (previousCode) {
                    previousCode = false;
                    isBold = c == 'l';
                } else {
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize = isBold ? messagePxSize + dFI.getBoldLength() : messagePxSize + dFI.getLength();
                    messagePxSize++;
                }
            }

            int toCompensate = CENTER_PX - messagePxSize / 2;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }
            returnMessage.append(sb).append(line).append("\n");
        }

        return returnMessage.toString();
    }

    public static String centerMessageWithFill(String message, char fillChar, String fillFormat) {
        message = formatMsg(message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            }
            if (previousCode) {
                previousCode = false;
                isBold = (c == 'l');
                continue;
            }
            DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
            messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
            messagePxSize++;
        }

        int toCompensate = CENTER_PX - messagePxSize / 2;
        int fillLength = DefaultFontInfo.getDefaultFontInfo(fillChar).getLength() + 1;
        int compensated = 0;

        StringBuilder leftSide = new StringBuilder();
        StringBuilder rightSide = new StringBuilder();

        while (compensated + fillLength <= toCompensate) {
            leftSide.append(fillChar);
            rightSide.append(fillChar);
            compensated += fillLength;
        }

        return formatMsg(fillFormat + leftSide + "&r" + message + fillFormat + rightSide);
    }
}
