package com.modencore.miner.item;

import com.modencore.miner.MinerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

/**
 * Handles creation and validation of the custom Miner item.
 */
public class MinerItem {

    private static final String MINER_KEY = "imperial_miner";
    private static final byte MINER_VALUE = 1;

    private static NamespacedKey minerKey;

    /**
     * Gets/creates the NamespacedKey for miner identification.
     */
    private static NamespacedKey getMinerKey() {
        if (minerKey == null) {
            minerKey = new NamespacedKey(MinerPlugin.getPlugin(), MINER_KEY);
        }
        return minerKey;
    }

    /**
     * Creates a new Miner item
     */
    public static ItemStack createMinerItem() {
        return createMinerItem(1);
    }

    /**
     * Creates a new Miner item with the specified amount.
     */
    public static ItemStack createMinerItem(int amount) {
        ItemStack item = new ItemStack(Material.STONECUTTER, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return item;

        // Set display name
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Auto Miner");

        // Set lore
        List<String> lore = Arrays.asList(
                "",
                ChatColor.GRAY + "An automated mining machine.",
                ChatColor.GRAY + "Place it down to start mining!",
                "",
                ChatColor.DARK_GRAY + "▸ " + ChatColor.YELLOW + "Right-click " + ChatColor.GRAY + "to open menu",
                ChatColor.DARK_GRAY + "▸ " + ChatColor.YELLOW + "Break " + ChatColor.GRAY + "to pick up",
                "",
                ChatColor.DARK_PURPLE + "Imperial Roma Mining Co.");
        meta.setLore(lore);

        // Add enchanted glint effect
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Add PDC tag for validation
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(getMinerKey(), PersistentDataType.BYTE, MINER_VALUE);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Checks if an ItemStack is a valid Miner item
     */
    public static boolean isMinerItem(ItemStack item) {
        if (item == null || item.getType() != Material.STONECUTTER) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(getMinerKey(), PersistentDataType.BYTE);
    }
}
