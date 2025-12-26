package com.modencore.miner.miner.fuel;

import com.liba.utils.ItemUtil;
import com.modencore.miner.miner.FuelTank;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum FuelTypes {
    COAL(ItemUtil.create(new ItemStack(Material.COAL), "Coal fuel"), 10),
    LAVA(ItemUtil.create(new ItemStack(Material.LAVA_BUCKET),"Lava fuel"), 500);


    final ItemStack material;
    final int fuelcount;
    FuelTypes(ItemStack material, int fuelcount){
        this.material = material;
        this.fuelcount = fuelcount;
    }

    public ItemStack getMaterial() {
        return material;
    }

    public int getFuelcount() {
        return fuelcount;
    }
}
