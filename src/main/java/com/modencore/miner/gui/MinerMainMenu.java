package com.modencore.miner.gui;

import com.liba.menu.Menu;
import com.liba.menu.MenuSlot;
import com.liba.menu.buttons.TextButton;
import com.liba.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MinerMainMenu extends Menu {
    public MinerMainMenu(String title, int size) {
        super(title, size);
    }

    @Override
    public void setItems() {

        addSlot(12, new MenuSlot(new TextButton(ItemUtil.create(new ItemStack(Material.BOOK), "ItemTitle")), (p, e) -> {

           p.sendMessage("Hi");

           e.setCancelled(true);
            // action
        }));

    }
}
