package com.modencore.miner.gui;

import com.liba.menu.Menu;
import com.liba.menu.MenuSlot;
import com.liba.menu.buttons.BooleanButton;
import com.liba.menu.buttons.ButtonType;
import com.liba.menu.buttons.TextButton;
import com.liba.utils.ItemUtil;
import com.modencore.miner.miner.Miner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MinerMainMenu extends Menu {
    Miner miner;
    public MinerMainMenu(String title, int size, Miner miner)
    {
        super(title, size);
        this.miner = miner;
    }

    @Override
    public void setItems() {

        addSlot(12, new MenuSlot(new TextButton(ItemUtil.create(new ItemStack(Material.BOOK), "ItemTitle")), (p, e) -> {

           p.sendMessage("Hi");

           e.setCancelled(true);
            // action
        }));
        BooleanButton enablebutton = new BooleanButton(
                ItemUtil.create(new ItemStack(Material.GREEN_WOOL), "Enable"),
                ItemUtil.create(new ItemStack(Material.RED_WOOL), "Disable"),
                miner.isEnable());
            enablebutton.setButtonType(ButtonType.updatable);
        addSlot(13, new MenuSlot(enablebutton, (p, e) -> {

            if (miner.isEnable()){
                miner.stop();
            }else{
                miner.start();
            }

            enablebutton.setVariable(miner.isEnable());
            e.setCancelled(true);
        }));

        addSlot(15, new MenuSlot(new TextButton(ItemUtil.create(new ItemStack(Material.LAVA_BUCKET), "Add fuel")), (p, e) -> {

            miner.getFueltank().addFuel(200);

            e.setCancelled(true);
            // action
        }));

    }

}
