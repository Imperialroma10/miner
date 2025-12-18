package com.modencore.miner.listener;

import com.modencore.miner.MinerPlugin;
import com.modencore.miner.item.MinerItem;
import com.modencore.miner.miner.Miner;
import com.modencore.miner.miner.MinerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Handles all miner-related events including placement, interaction, and
 * breaking.
 */
public class MinerListener implements Listener {

    /**
     * Handles player interaction with miner blocks.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onMinerInteract(PlayerInteractEvent event) {
        // Only handle when right-clicking the block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        // Check if this block is a miner
        Miner miner = MinerManager.getMiner(block.getLocation());
        if (miner == null) {
            return;
        }

        Player player = event.getPlayer();

        // Cancel the event to prevent block default action
        event.setCancelled(true);

        // Check permission to interact
        if (!player.hasPermission("miner.use")) {
            player.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("no-permission"));
            return;
        }

        // Open the miner menu
        miner.getMenu().open(player);
    }

    /**
     * Handles placement of miner items.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMinerPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();

        // Check if the placed item is a valid miner item
        if (!MinerItem.isMinerItem(itemInHand)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check permission to place
        if (!player.hasPermission("miner.place")) {
            event.setCancelled(true);
            player.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("no-permission"));
            return;
        }

        // Ensure the block type is the miner block
        if (block.getType() != Material.STONECUTTER) {
            return;
        }

        // Check if there's already a miner at this location
        if (MinerManager.hasMiner(block.getLocation())) {
            return;
        }

        // Create and register the new miner
        Miner miner = new Miner(block.getLocation(), player);
        MinerManager.addMiner(miner);
    }

    /**
     * Handles breaking of miner blocks.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMinerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if this block is a miner
        Miner miner = MinerManager.getMiner(block.getLocation());
        if (miner == null) {
            return;
        }

        Player player = event.getPlayer();

        // Check if player is the owner or has admin permission
        if (!miner.getOwner().equals(player.getUniqueId()) && !player.hasPermission("miner.admin.break")) {
            event.setCancelled(true);
            player.sendMessage("&cYou can only remove your own miners! Use the GUI to remove this miner.");
            return;
        }

        // If the player has permission, remove the miner properly
        event.setCancelled(true);
        miner.remove();
    }

    /**
     * Prevents explosion damage to miner blocks.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplosion(org.bukkit.event.entity.EntityExplodeEvent event) {
        event.blockList().removeIf(
                block -> block.getType() == Material.STONECUTTER && MinerManager.hasMiner(block.getLocation()));
    }

    /**
     * Prevents block explosion damage to miner blocks.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplosion(org.bukkit.event.block.BlockExplodeEvent event) {
        event.blockList().removeIf(
                block -> block.getType() == Material.STONECUTTER && MinerManager.hasMiner(block.getLocation()));
    }

    /**
     * Prevents piston pushing of miner blocks.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonPush(org.bukkit.event.block.BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (MinerManager.hasMiner(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Prevents piston pulling of miner blocks.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonPull(org.bukkit.event.block.BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (MinerManager.hasMiner(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
