package com.modencore.miner.miner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MinerHead {

    Miner miner;

    Location currentLocation;
    Material material = Material.DIAMOND_BLOCK;
    Material chainMaterial = Material.CHAIN;
    int radius = 1;
    public  MinerHead(Miner miner)
    {
        this.miner = miner;
        spawn();
    }

    public void nextBlock(){

        for (int x = getHeadLocation().getBlockX() - radius; x <= getHeadLocation().getBlockX() + radius; x++) {
            for (int z = getHeadLocation().getBlockZ() - radius; z <= getHeadLocation().getBlockZ() + radius; z++) {
                Block block = getHeadLocation().getWorld().getBlockAt(x,getHeadLocation().getBlockY(), z);
                if (block.getType() == Material.AIR) continue;
                if (block.getLocation().equals(currentLocation)) continue;
                mineBlock(block);
                return;
            }
        }
        nextRow();
    }

    public void mineBlock(Block block){
        block.breakNaturally();
    }

    public void spawn(){
        Location minerlocation = miner.getLocation().clone();
        minerlocation.setY(minerlocation.getBlockY()-1);
        this.currentLocation = minerlocation;
        mineBlock(currentLocation.getBlock());
        this.currentLocation.getBlock().setType(material);
    }

    public void nextRow(){
        currentLocation.getBlock().setType(chainMaterial);
        currentLocation = currentLocation.clone().add(0, -1, 0);
        mineBlock(currentLocation.getBlock());
        currentLocation.getBlock().setType(material);
    }

    public Location getHeadLocation(){
        return currentLocation;
    }




}
