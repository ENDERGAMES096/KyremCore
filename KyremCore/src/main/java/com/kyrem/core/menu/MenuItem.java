package com.kyrem.core.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a clickable item inside a {@link Menu}.
 */
public class MenuItem {

    private final ItemStack itemStack;
    private final ClickHandler clickHandler;

    @FunctionalInterface
    public interface ClickHandler {
        void onClick(InventoryClickEvent event);
    }

    public MenuItem(ItemStack itemStack, ClickHandler clickHandler) {
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
    }

    public MenuItem(ItemStack itemStack) {
        this(itemStack, event -> {});
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void onClick(InventoryClickEvent event) {
        if (clickHandler != null) clickHandler.onClick(event);
    }
}
