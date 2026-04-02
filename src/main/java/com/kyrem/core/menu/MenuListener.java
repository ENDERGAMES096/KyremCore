package com.kyrem.core.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu menu) {
            if (menu.eventCancelled())
                event.setCancelled(true);
            if (event.getCurrentItem() == null)
                return;
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (holder instanceof Menu menu) {
            menu.handleMenuClose(event);
        }
    }
}
