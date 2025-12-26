package com.modencore.miner.miner;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MinerHead {

    Miner miner;

    Location currentLocation;
    Location dropLocation;
    Material material = Material.DIAMOND_BLOCK;
    Material chainMaterial = Material.CHAIN;
    int radius = 1;

    List<Material> black_list = new ArrayList<>();

    public  MinerHead(Miner miner)
    {
        black_list.add(Material.BEDROCK);
        black_list.add(Material.WATER);
        this.miner = miner;
        dropLocation = miner.getLocation().clone().add(0.5,1, 0.5);
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

    public void checkChest(Block block) {

        // Если блок — сундук
        if (dropLocation.getBlock().getType() == Material.CHEST) {

            Chest chest = (Chest) dropLocation.getBlock().getState();

            Map<Integer, ItemStack> leftover =
                    chest.getInventory().addItem(block.getDrops().toArray(new ItemStack[0]));

            if (!leftover.isEmpty()) {
                Bukkit.getPlayer(miner.getOwner()).sendMessage("Chest full, miner off");
                miner.stop();
                return;
            }

        } else {
            for (ItemStack item : block.getDrops()) {
                dropLocation.getWorld().dropItemNaturally(dropLocation, item);
            }
        }
        block.setType(Material.AIR);

    }


    public void mineBlock(Block block){
        if (miner.getFueltank().takeFuel(5)){

            playEnergyBeam();
            checkChest(block);
        }else{
            miner.stop();
            Bukkit.getPlayer(miner.getOwner()).sendMessage("No fuel, miner disabled");
        }

    }

    public void spawn(){
        Location minerlocation = miner.getLocation().clone();
        minerlocation.setY(minerlocation.getBlockY()-1);
        this.currentLocation = minerlocation;
        if (!black_list.contains(minerlocation.getBlock().getType())) {
            mineBlock(currentLocation.getBlock());
            this.currentLocation.getBlock().setType(material);
        }else{
            Bukkit.getPlayer(miner.getOwner()).sendMessage("The miner's head hit the forbidden block");
            miner.stop();
        }

    }


    public void nextRow(){

        if (!black_list.contains(currentLocation.clone().add(0,-1,0).getBlock().getType())) {
            currentLocation.getBlock().setType(chainMaterial);
            currentLocation = currentLocation.clone().add(0, -1, 0);
            mineBlock(currentLocation.getBlock());
            currentLocation.getBlock().setType(material);
        }else{
            Bukkit.getPlayer(miner.getOwner()).sendMessage("The miner's head hit the forbidden block");
            miner.stop();
        }


    }

    public Location getHeadLocation(){
        return currentLocation;
    }

    public void playEnergyBeam() {
        Location from = miner.getLocation().clone().add(0.5, 0.5, 0.5);
        Location to = getHeadLocation().clone().add(0.5, 0.5, 0.5);

        World world = from.getWorld();
        if (world == null) return;

        Vector direction = to.toVector().subtract(from.toVector());
        double length = direction.length();
        direction.normalize();

        for (double i = 0; i < length; i += 0.25) {
            Location point = from.clone().add(direction.clone().multiply(i));

            world.spawnParticle(
                    Particle.DUST,
                    point,
                    1,
                    new Particle.DustOptions(Color.AQUA, 1.2F)
            );
        }
    }

    public Location getDropLocation() {
        return dropLocation;
    }
}
