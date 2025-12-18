package com.modencore.miner.miner;

import com.modencore.miner.MinerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages all active miners in the server.
 */
public class MinerManager {

    private static final Map<Location, Miner> minersByLocation = new HashMap<>();
    private static final Map<UUID, Miner> minersByUuid = new HashMap<>();
    private static MinerStorage storage;

    public static void initialize() {
        storage = new MinerStorage();
        loadMiners();
    }

    public static void shutdown() {
        stopAllMiners();
        saveMiners();
        minersByLocation.clear();
        minersByUuid.clear();
    }

    public static void addMiner(Miner miner) {
        minersByLocation.put(normalizeLocation(miner.getLocation()), miner);
        minersByUuid.put(miner.getUuid(), miner);
        
        saveMiners();
        
        // Notify owner
        var owner = Bukkit.getPlayer(miner.getOwner());
        if (owner != null && owner.isOnline()) {
            owner.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("miner-placed"));
        }
    }

    /**
     * Gets a miner by its location.
     */
    public static Miner getMiner(Location location) {
        return minersByLocation.get(normalizeLocation(location));
    }

    /**
     * Gets a miner by its UUID.
     */
    public static Miner getMiner(UUID uuid) {
        return minersByUuid.get(uuid);
    }

    /**
     * Checks if a location has a miner
     */
    public static boolean hasMiner(Location location) {
        return minersByLocation.containsKey(normalizeLocation(location));
    }

    public static void removeMiner(Location location) {
        Location normalized = normalizeLocation(location);
        Miner miner = minersByLocation.remove(normalized);
        if (miner != null) {
            miner.stop();
            minersByUuid.remove(miner.getUuid());
            saveMiners();
        }
    }

    public static void removeMiner(Miner miner) {
        if (miner == null) return;
        
        miner.stop();
        minersByLocation.remove(normalizeLocation(miner.getLocation()));
        minersByUuid.remove(miner.getUuid());
        saveMiners();
    }

    public static List<Miner> getAllMiners() {
        return new ArrayList<>(minersByLocation.values());
    }

    /**
     * Gets all miners owned by a specific player.
     */
    public static List<Miner> getMinersByOwner(UUID ownerUuid) {
        List<Miner> owned = new ArrayList<>();
        for (Miner miner : minersByLocation.values()) {
            if (miner.getOwner().equals(ownerUuid)) {
                owned.add(miner);
            }
        }
        return owned;
    }

    /**
     * Stops all active miners.
     */
    public static void stopAllMiners() {
        for (Miner miner : minersByLocation.values()) {
            miner.stop();
        }
    }

    /**
     * Saves all miners to storage.
     */
    public static void saveMiners() {
        if (storage != null) {
            storage.saveMiners(getAllMiners());
        }
    }

    /**
     * Loads miners from storage.
     */
    private static void loadMiners() {
        if (storage == null) return;
        
        List<Miner> loaded = storage.loadMiners();
        for (Miner miner : loaded) {
            minersByLocation.put(normalizeLocation(miner.getLocation()), miner);
            minersByUuid.put(miner.getUuid(), miner);
        }
        
        MinerPlugin.getPlugin().getLogger().info("Loaded " + loaded.size() + " miners from storage.");
    }

    private static Location normalizeLocation(Location location) {
        return new Location(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()
        );
    }
}
