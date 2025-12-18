package com.modencore.miner.miner;

import com.modencore.miner.MinerPlugin;
import com.modencore.miner.gui.MinerMainMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a single miner entity in the world.
 * Handling for mining logic, animation, and state management.
 */
public class Miner {

    private final UUID uuid;
    private final UUID owner;
    private final Location location;
    private final String worldName;

    private MinerMainMenu menu;
    private BukkitTask miningTask;
    private boolean enabled;

    // Mining state
    private int currentY;
    private int blocksMinedTotal;
    private int currentBlockIndex;
    private List<Location> currentLayerBlocks;

    // Internal inventory for storing mined items
    private final List<ItemStack> inventory;

    // Drop mode: "miner", "block", or "inventory"
    private String dropMode;

    /**
     * Creates a new Miner at the specified location.
     * 
     * @param location The location where the miner is placed
     * @param owner    The player who placed the miner
     */
    public Miner(Location location, Player owner) {
        this.uuid = UUID.randomUUID();
        this.owner = owner.getUniqueId();
        this.location = location.clone();
        this.worldName = location.getWorld() != null ? location.getWorld().getName() : "world";
        this.currentY = location.getBlockY() - 1; // Start mining below the miner
        this.blocksMinedTotal = 0;
        this.currentBlockIndex = 0;
        this.currentLayerBlocks = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.dropMode = "miner"; // Default: drop above miner
        this.enabled = false;
        this.menu = new MinerMainMenu(this);
    }

    /**
     * Constructor for loading from storage.
     */
    public Miner(UUID uuid, UUID owner, Location location, int currentY, int blocksMinedTotal, String dropMode) {
        this.uuid = uuid;
        this.owner = owner;
        this.location = location.clone();
        this.worldName = location.getWorld() != null ? location.getWorld().getName() : "world";
        this.currentY = currentY;
        this.blocksMinedTotal = blocksMinedTotal;
        this.currentBlockIndex = 0;
        this.currentLayerBlocks = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.dropMode = dropMode != null ? dropMode : "miner";
        this.enabled = false;
        this.menu = new MinerMainMenu(this);
    }

    /**
     * Starts the mining operation.
     */
    public void start() {
        if (enabled)
            return;

        enabled = true;
        currentBlockIndex = 0;
        calculateCurrentLayerBlocks();

        int delayTicks = MinerPlugin.getPlugin().getMinerConfig().getDigDelayTicks();

        miningTask = Bukkit.getScheduler().runTaskTimer(
                MinerPlugin.getPlugin(),
                this::miningTick,
                0,
                delayTicks);

        // Notify owner
        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            ownerPlayer.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("miner-started"));
        }
    }

    /**
     * Stops the mining operation.
     */
    public void stop() {
        if (!enabled)
            return;

        enabled = false;
        if (miningTask != null) {
            miningTask.cancel();
            miningTask = null;
        }

        // Notify owner
        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            ownerPlayer.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("miner-stopped"));
        }

        // Log the miner stop
        MinerPlugin.getPlugin().getLogger().info("Miner stopped: " + uuid);
    }

    /**
     * Called each mining tick to process blocks.
     */
    private void miningTick() {
        World world = location.getWorld();
        if (world == null) {
            stop();
            return;
        }

        // Pause if chunk is no longer loaded
        if (!world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            stop();
            return;
        }

        // Check if we need to move to next layer
        if (currentBlockIndex >= currentLayerBlocks.size()) {
            // Move to next layer
            currentY--;
            currentBlockIndex = 0;

            // Check world limit
            if (currentY < world.getMinHeight()) {
                onMiningComplete();
                return;
            }

            calculateCurrentLayerBlocks();

            // Check if the new layer is all bedrock (meaning we've reached the bottom)
            if (isLayerAllBedrock()) {
                onMiningComplete();
                return;
            }

            // If the layer is empty or all air, skip to next iteration
            if (currentLayerBlocks.isEmpty()) {
                onMiningComplete();
                return;
            }
        }

        // Mine blocks for this tick
        int blocksPerTick = MinerPlugin.getPlugin().getMinerConfig().getBlocksPerTick();
        int blocksMined = 0;

        while (blocksMined < blocksPerTick && currentBlockIndex < currentLayerBlocks.size()) {
            Location blockLoc = currentLayerBlocks.get(currentBlockIndex);
            Block block = world.getBlockAt(blockLoc);

            // Check if we hit bedrock - skip it
            if (block.getType() == Material.BEDROCK) {
                currentBlockIndex++;
                continue;
            }

            // Skip air and unbreakable blocks
            if (block.getType() == Material.AIR || block.getType() == Material.VOID_AIR
                    || block.getType() == Material.CAVE_AIR) {
                currentBlockIndex++;
                continue;
            }

            // Mine the block
            mineBlock(block);
            blocksMined++;
            blocksMinedTotal++;
            currentBlockIndex++;
        }
    }

    /**
     * Checks if the current layer is entirely bedrock and has no minable blocks.
     */
    private boolean isLayerAllBedrock() {
        World world = location.getWorld();
        if (world == null)
            return true;

        boolean hasBedrock = false;

        for (Location loc : currentLayerBlocks) {
            Block block = world.getBlockAt(loc);
            Material type = block.getType();

            if (type != Material.BEDROCK && type != Material.AIR
                    && type != Material.VOID_AIR && type != Material.CAVE_AIR) {
                return false;
            }

            if (type == Material.BEDROCK) {
                hasBedrock = true;
            }
        }

        return hasBedrock;
    }

    /**
     * Mines a single block with effects.
     */
    private void mineBlock(Block block) {
        World world = block.getWorld();
        Location blockLoc = block.getLocation().add(0.5, 0.5, 0.5);

        for (ItemStack drop : block.getDrops()) {
            switch (dropMode.toLowerCase()) {
                case "inventory":
                    addToInventory(drop);
                    break;
                case "block":
                    world.dropItemNaturally(blockLoc, drop);
                    break;
                case "miner":
                default:
                    world.dropItemNaturally(location.clone().add(0.5, 1, 0.5), drop);
                    break;
            }
        }

        world.spawnParticle(Particle.BLOCK, blockLoc, 10, 0.3, 0.3, 0.3, 0, block.getBlockData());
        world.playSound(blockLoc, Sound.BLOCK_STONE_BREAK, 0.5f, 1.0f);

        block.setType(Material.AIR);
    }

    /**
     * Adds an item to the miner's internal inventory, stacking when possible.
     */
    private void addToInventory(ItemStack item) {
        // Try to stack with existing items
        for (ItemStack existing : inventory) {
            if (existing.isSimilar(item)) {
                int canAdd = existing.getMaxStackSize() - existing.getAmount();
                if (canAdd > 0) {
                    int toAdd = Math.min(canAdd, item.getAmount());
                    existing.setAmount(existing.getAmount() + toAdd);
                    item.setAmount(item.getAmount() - toAdd);
                    if (item.getAmount() <= 0) {
                        return;
                    }
                }
            }
        }
        // Add remaining as new stack
        if (item.getAmount() > 0) {
            inventory.add(item.clone());
        }
    }

    /**
     * Gets all items stored in the miner's inventory.
     */
    public List<ItemStack> getInventory() {
        return new ArrayList<>(inventory);
    }

    /**
     * Gets the total number of items in the inventory.
     */
    public int getInventoryItemCount() {
        int count = 0;
        for (ItemStack item : inventory) {
            count += item.getAmount();
        }
        return count;
    }

    /**
     * Collects all items from the miner's inventory to a player.
     */
    public void collectInventory(Player player) {
        if (inventory.isEmpty()) {
            return;
        }

        for (ItemStack item : inventory) {
            var leftover = player.getInventory().addItem(item);
            // Drop any items that didn't fit
            for (ItemStack dropped : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), dropped);
            }
        }
        inventory.clear();
    }

    /**
     * Drops all inventory contents at the miner's location.
     */
    public void dropInventory() {
        World world = location.getWorld();
        if (world == null || inventory.isEmpty()) {
            return;
        }

        Location dropLoc = location.clone().add(0.5, 1, 0.5);
        for (ItemStack item : inventory) {
            world.dropItemNaturally(dropLoc, item);
        }
        inventory.clear();
    }

    /**
     * Called when mining reaches bedrock or world limit.
     */
    private void onMiningComplete() {
        stop();

        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            ownerPlayer.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("miner-complete"));
        }
    }

    /**
     * Calculates the blocks to mine in the current layer
     */
    private void calculateCurrentLayerBlocks() {
        currentLayerBlocks.clear();

        World world = location.getWorld();
        if (world == null)
            return;

        int radius = MinerPlugin.getPlugin().getMinerConfig().getMinerRadius();
        int halfRadius = radius / 2;

        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();

        // Create a square area centered on the miner
        for (int x = centerX - halfRadius; x <= centerX + halfRadius; x++) {
            for (int z = centerZ - halfRadius; z <= centerZ + halfRadius; z++) {
                currentLayerBlocks.add(new Location(world, x, currentY, z));
            }
        }
    }

    /**
     * Removes the miner and drops the miner item.
     */
    public void remove() {
        stop();

        dropInventory();

        World world = location.getWorld();
        if (world != null) {
            Block minerBlock = world.getBlockAt(location);
            if (minerBlock.getType() == Material.STONECUTTER) {
                minerBlock.setType(Material.AIR);

                world.dropItemNaturally(
                        location.clone().add(0.5, 0.5, 0.5),
                        com.modencore.miner.item.MinerItem.createMinerItem());
            }
        }

        // Notify owner
        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            ownerPlayer.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("miner-removed"));
        }

        MinerManager.removeMiner(this);
    }

    // Getters

    public UUID getUuid() {
        return uuid;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location.clone();
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getCurrentY() {
        return currentY;
    }

    public int getBlocksMinedTotal() {
        return blocksMinedTotal;
    }

    public MinerMainMenu getMenu() {
        return menu;
    }

    public String getDropMode() {
        return dropMode;
    }

    public void setDropMode(String dropMode) {
        this.dropMode = dropMode;
        MinerManager.saveMiners(); // Save when drop mode changes
    }

    /**
     * Cycles to the next drop mode
     */
    public void cycleDropMode() {
        switch (dropMode.toLowerCase()) {
            case "miner":
                dropMode = "block";
                break;
            case "block":
                dropMode = "inventory";
                break;
            case "inventory":
            default:
                dropMode = "miner";
                break;
        }
        MinerManager.saveMiners();
    }

    /**
     * Gets a display-friendly name for the current drop mode.
     */
    public String getDropModeDisplayName() {
        return switch (dropMode.toLowerCase()) {
            case "block" -> "At Block";
            case "inventory" -> "Internal Storage";
            default -> "Above Miner";
        };
    }

    public void refreshMenu() {
        this.menu = new MinerMainMenu(this);
    }
}
