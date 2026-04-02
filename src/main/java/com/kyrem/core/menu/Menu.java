package com.kyrem.core.menu;

import com.kyrem.core.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Defines the behavior and attributes of all menus.
 */
public abstract class Menu implements InventoryHolder {

    protected PlayerMenuUtility playerMenuUtility;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("").build();

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void handleMenuClose(InventoryCloseEvent event);

    public abstract void setMenuItems();

    public abstract boolean eventCancelled();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), Component.text(getMenuName()));
        this.setMenuItems();
        playerMenuUtility.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getFillerGlass() {
        return FILLER_GLASS;
    }
}
