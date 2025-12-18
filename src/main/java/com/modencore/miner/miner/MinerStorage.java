package com.modencore.miner.miner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.modencore.miner.MinerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles persistence of miners to JSON storage.
 */
public class MinerStorage {

    private static final String STORAGE_FILE = "miners.json";
    private final File storageFile;
    private final Gson gson;

    public MinerStorage() {
        this.storageFile = new File(MinerPlugin.getPlugin().getDataFolder(), STORAGE_FILE);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Saves all miners to the storage file.
     */
    public void saveMiners(List<Miner> miners) {
        List<MinerData> dataList = new ArrayList<>();
        
        for (Miner miner : miners) {
            MinerData data = new MinerData();
            data.uuid = miner.getUuid().toString();
            data.owner = miner.getOwner().toString();
            data.worldName = miner.getWorldName();
            data.x = miner.getLocation().getBlockX();
            data.y = miner.getLocation().getBlockY();
            data.z = miner.getLocation().getBlockZ();
            data.currentY = miner.getCurrentY();
            data.blocksMinedTotal = miner.getBlocksMinedTotal();
            data.dropMode = miner.getDropMode();
            dataList.add(data);
        }

        try {
            if (!storageFile.getParentFile().exists()) {
                storageFile.getParentFile().mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(storageFile)) {
                gson.toJson(dataList, writer);
            }
        } catch (IOException e) {
            MinerPlugin.getPlugin().getLogger().log(Level.SEVERE, "Failed to save miners to storage", e);
        }
    }

    /**
     * Loads all miners from the storage file.
     */
    public List<Miner> loadMiners() {
        List<Miner> miners = new ArrayList<>();
        
        if (!storageFile.exists()) {
            return miners;
        }

        try (FileReader reader = new FileReader(storageFile)) {
            Type listType = new TypeToken<List<MinerData>>(){}.getType();
            List<MinerData> dataList = gson.fromJson(reader, listType);
            
            if (dataList == null) {
                return miners;
            }

            for (MinerData data : dataList) {
                Miner miner = loadMiner(data);
                if (miner != null) {
                    miners.add(miner);
                }
            }
        } catch (IOException e) {
            MinerPlugin.getPlugin().getLogger().log(Level.SEVERE, "Failed to load miners from storage", e);
        } catch (Exception e) {
            MinerPlugin.getPlugin().getLogger().log(Level.SEVERE, "Failed to parse miners storage file", e);
        }

        return miners;
    }

    /**
     * Loads a single miner from data, validating the block still exists.
     */
    private Miner loadMiner(MinerData data) {
        try {
            World world = Bukkit.getWorld(data.worldName);
            if (world == null) {
                MinerPlugin.getPlugin().getLogger().warning(
                    "Could not load miner " + data.uuid + ": world '" + data.worldName + "' not found"
                );
                return null;
            }

            Location location = new Location(world, data.x, data.y, data.z);
            
            // Validate that the lodestone block still exists
            if (location.getBlock().getType() != Material.STONECUTTER) {
                MinerPlugin.getPlugin().getLogger().warning(
                    "Could not load miner " + data.uuid + ": lodestone block no longer exists at location"
                );
                return null;
            }

            UUID uuid = UUID.fromString(data.uuid);
            UUID owner = UUID.fromString(data.owner);
            
            return new Miner(uuid, owner, location, data.currentY, data.blocksMinedTotal, data.dropMode);
        } catch (Exception e) {
            MinerPlugin.getPlugin().getLogger().log(Level.WARNING, 
                "Failed to load miner data: " + data.uuid, e);
            return null;
        }
    }

    private static class MinerData {
        String uuid;
        String owner;
        String worldName;
        int x;
        int y;
        int z;
        int currentY;
        int blocksMinedTotal;
        String dropMode;
    }
}

