package com.kyrem.core.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemBuilder {

    private final ItemStack item;
    private String displayName;
    private List<String> lore;
    private int customModelData;
    private int quantity;
    private int durability;
    private boolean unbreakable;
    private boolean glowing;

    private final Map<Enchantment, Integer> enchantments;
    private final List<ItemFlag> itemFlags;
    private final Map<NamespacedKey, PersistentDataPair<?, ?>> persistentData;

    private record PersistentDataPair<K, V>(PersistentDataType<K, V> type, V value) {}

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.lore = new ArrayList<>();
        this.enchantments = new HashMap<>();
        this.itemFlags = new ArrayList<>();
        this.persistentData = new HashMap<>();
        this.quantity = 1;
    }

    public ItemBuilder(ItemStack baseItem) {
        this.item = baseItem.clone();
        this.enchantments = new HashMap<>();
        this.itemFlags = new ArrayList<>();
        this.persistentData = new HashMap<>();
        this.lore = new ArrayList<>();

        ItemMeta meta = baseItem.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) this.displayName = meta.getDisplayName();
            if (meta.hasLore()) this.lore = new ArrayList<>(meta.getLore());
            if (meta.hasCustomModelData()) this.customModelData = meta.getCustomModelData();
            if (meta.hasEnchants()) this.enchantments.putAll(meta.getEnchants());
            this.itemFlags.addAll(meta.getItemFlags());
            this.unbreakable = meta.isUnbreakable();
            if (meta instanceof Damageable dmg) this.durability = dmg.getDamage();
        }

        this.quantity = baseItem.getAmount();
    }

    public ItemBuilder setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        this.customModelData = data;
        return this;
    }

    public ItemBuilder setQuantity(int qty) {
        this.quantity = Math.max(1, qty);
        return this;
    }

    public ItemBuilder setDurability(int value) {
        this.durability = Math.max(0, value);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean value) {
        this.unbreakable = value;
        return this;
    }

    public ItemBuilder setGlowing(boolean value) {
        this.glowing = value;
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment ench, int level) {
        this.enchantments.put(ench, level);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }

    public <K, V> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<K, V> type, V value) {
        this.persistentData.put(key, new PersistentDataPair<>(type, value));
        return this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <K, V> void setDataUnsafe(PersistentDataContainer container, NamespacedKey key, PersistentDataPair<K, V> pair) {
        container.set(key, (PersistentDataType) pair.type(), pair.value());
    }

    public ItemStack build() {
        ItemMeta meta = item.getItemMeta();

        if (displayName != null && !displayName.isEmpty())
            meta.setDisplayName(ChatUtils.formatMsg(displayName));

        if (lore != null && !lore.isEmpty()) {
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore)
                formattedLore.add(ChatUtils.formatMsg(line));
            meta.setLore(formattedLore);
        }

        if (customModelData != 0) meta.setCustomModelData(customModelData);

        meta.setUnbreakable(unbreakable);
        if (meta instanceof Damageable dmg) dmg.setDamage(durability);

        enchantments.forEach((ench, lvl) -> meta.addEnchant(ench, lvl, true));
        itemFlags.forEach(meta::addItemFlags);

        if (glowing && meta.getEnchants().isEmpty()) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        for (Map.Entry<NamespacedKey, PersistentDataPair<?, ?>> entry : persistentData.entrySet()) {
            setDataUnsafe(data, entry.getKey(), entry.getValue());
        }

        item.setItemMeta(meta);
        item.setAmount(quantity);
        return item;
    }
}
