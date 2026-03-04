package com.kyrem.core.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtils {

    public static <K, V> boolean hasPersistentData(ItemStack item, NamespacedKey namespacedKey, PersistentDataType<K, V> persistentDataType) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.has(namespacedKey, persistentDataType);
    }

    public static <K, V> boolean hasPersistentDataValue(ItemStack item, NamespacedKey namespacedKey, PersistentDataType<K, V> persistentDataType, K value) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return hasPersistentData(item, namespacedKey, persistentDataType) && container.get(namespacedKey, persistentDataType).equals(value);
    }

    public static <K, V> V getPersistentData(ItemStack item, NamespacedKey namespacedKey, PersistentDataType<K, V> persistentDataType) {
        if (!hasPersistentData(item, namespacedKey, persistentDataType)) return null;
        if (item.getItemMeta() == null) return null;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(namespacedKey, persistentDataType);
    }

    public static void removePersistentData(ItemStack item, NamespacedKey namespacedKey) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.remove(namespacedKey);
        item.setItemMeta(meta);
    }
}
