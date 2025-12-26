package com.modencore.miner.utils;

import com.modencore.miner.MinerPlugin;
import com.modencore.miner.miner.Miner;
import com.modencore.miner.miner.MinerManager;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MinerUtils {

    public static boolean isMinerBlock(ItemStack itemStack){
        if (itemStack == null) return false;
        if (!itemStack.hasItemMeta()) return false;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;
        if (!meta.getPersistentDataContainer().has(Miner.NAMESPACED_KEY)) return false;

        return Boolean.TRUE.equals(meta.getPersistentDataContainer().get(Miner.NAMESPACED_KEY, PersistentDataType.BOOLEAN));
    }

    public static ItemStack setMinerBlock(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Miner.NAMESPACED_KEY, PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
