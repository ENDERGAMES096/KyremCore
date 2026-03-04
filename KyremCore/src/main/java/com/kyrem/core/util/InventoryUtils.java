package com.kyrem.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InventoryUtils {

    public static ItemStack getPlayerHead(UUID playerID, String headName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            if (playerID != null && !playerID.toString().isEmpty()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);
                meta.setOwningPlayer(player);
            }
            meta.setDisplayName(ChatUtils.formatMsg(headName));
            head.setItemMeta(meta);
        }

        return head;
    }

    public static void addItemFromCenter(Inventory inventory, int rows, ItemStack item) {
        List<Integer> notEmptySlots = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int s = (9 * i); s < (9 * (i + 1)); s++) {
                if (inventory.getItem(s) != null)
                    notEmptySlots.add(s);

                if (notEmptySlots.isEmpty() && s <= (9 * rows)) {
                    int centerSlot;
                    if (CustomMath.isPrime(inventory.getSize()))
                        centerSlot = (inventory.getSize() / 2) - 1;
                    else
                        centerSlot = inventory.getSize() / 2;

                    inventory.setItem(centerSlot, item);
                    return;
                }

                Optional<Integer> optionalFirstNum = notEmptySlots.stream().findFirst();
                if (!optionalFirstNum.isPresent()) break;

                if (!CustomMath.isPrime(notEmptySlots.size())) {
                    int rowStart = 9 * i;
                    for (int itemIndex : notEmptySlots) {
                        if (itemIndex >= rowStart && itemIndex < rowStart + 9) {
                            inventory.setItem(itemIndex - 9, inventory.getItem(itemIndex));
                            inventory.setItem(itemIndex, null);
                        } else {
                            inventory.setItem(itemIndex + 1, inventory.getItem(itemIndex));
                        }
                    }
                }
                inventory.setItem(optionalFirstNum.get(), item);
            }
        }
    }

    public static boolean hasInventorySpace(Inventory inv, boolean stackable, ItemStack item, int itemAmount) {
        int index = 0;
        for (ItemStack i : inv.getStorageContents()) {
            index++;
            if (i == null || i.getType() == Material.AIR) return true;
            if (stackable && i.isSimilar(item)) {
                if ((i.getAmount() + itemAmount) <= i.getMaxStackSize()) return true;
                else if (index >= inv.getStorageContents().length) return false;
            } else {
                if (index >= inv.getStorageContents().length) return false;
            }
        }
        return false;
    }

    public static boolean hasEnoughItems(Player player, ItemStack item, int amount) {
        if (item == null) return false;
        int count = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot == null || !slot.isSimilar(item)) continue;
            count += slot.getAmount();
        }
        return count >= amount;
    }

    public static int getItemAmount(Player player, ItemStack item) {
        if (item == null) return 0;
        int count = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot == null || !slot.isSimilar(item)) continue;
            count += slot.getAmount();
        }
        return count;
    }

    public static void fillBorder(Inventory inv, int rows, ItemStack fillItem) {
        for (int row = 0; row < rows; row++) {
            int index = row * 9;
            for (int slot = index; slot < index + 9; slot++)
                if (row == 0 || row == rows - 1 || ((row > 0 && row < rows - 1) && (index == slot || slot == index + 8)))
                    inv.setItem(slot, fillItem);
        }
    }

    public static void fillInventoryExcept(Inventory inv, ItemStack fillItem, ArrayList<Integer> slotsToSkip) {
        for (int i = 0; i < inv.getSize(); i++)
            if (!slotsToSkip.contains(i) && inv.getItem(i) == null)
                inv.setItem(i, fillItem);
    }

    public static boolean isBorderSlot(int rows, int slot) {
        if (slot < 0 || slot >= rows * 9) return false;
        int row = slot / 9;
        int col = slot % 9;
        return row == 0 || row == rows - 1 || col == 0 || col == 8;
    }

    public static String checkAndFixMaterialStr(String materialStr) {
        if (materialStr.isEmpty()) return null;
        materialStr = materialStr.toUpperCase();
        materialStr = materialStr.replaceAll(" ", "_");
        return materialStr;
    }
}
