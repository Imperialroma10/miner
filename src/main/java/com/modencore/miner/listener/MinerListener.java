package com.modencore.miner.listener;

import com.modencore.miner.miner.Miner;
import com.modencore.miner.miner.MinerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MinerListener implements Listener {

    @EventHandler
    public void openMiner(PlayerInteractEvent e){

        Block block = e.getClickedBlock();
        if (block == null) return;
        Miner miner = MinerManager.getMiner(block.getLocation());
        if (miner == null) return;

        miner.getMenu().open(e.getPlayer());

    }

    @EventHandler
    public void placeMiner(BlockPlaceEvent e){
        if (e.getBlock().getType() == Material.IRON_BLOCK){ // This is temporary for testing purposes, we will change it later.
            MinerManager.addMiner(new Miner(e.getBlock().getLocation(), e.getPlayer()));
        }

    }

    @EventHandler
    public void minerRemove(BlockBreakEvent e){
        Block block = e.getBlock();

        Miner miner = MinerManager.getMiner(block.getLocation());
        if (miner == null) return;

        MinerManager.removeMiner(miner.getLocation());
    }


}
