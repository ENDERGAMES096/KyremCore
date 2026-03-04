package com.kyrem.core.menu;

import com.kyrem.core.util.InventoryUtils;
import com.kyrem.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuBorder(int referenceSlot) {
        InventoryUtils.fillBorder(inventory, getSlots() / 9, FILLER_GLASS);

        ItemStack backButton = new ItemBuilder(Material.OAK_BUTTON)
                .setDisplayName("&fBack")
                .build();
        ItemStack nextButton = new ItemBuilder(Material.OAK_BUTTON)
                .setDisplayName("&fNext")
                .build();

        inventory.setItem(referenceSlot, backButton);
        inventory.setItem(referenceSlot + 1, nextButton);
    }

    public abstract int getMaxItemsPerPage();
}
