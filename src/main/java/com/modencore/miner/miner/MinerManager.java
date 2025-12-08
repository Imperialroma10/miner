package com.modencore.miner.miner;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Iterator;

public class MinerManager {

    static HashMap<Location, Miner> miners = new HashMap<>();

    public static void addMiner( Miner miner)
    {
        miners.put(miner.getLocation(), miner);
        Bukkit.getPlayer(miner.getUuid()).sendMessage("You place miner");
    }

    public static Miner getMiner( Location location )
    {
        return miners.get(location);
    }

    public static void removeMiner( Location location )
    {
        Miner miner = miners.get(location);
        if (miner == null) return;

        Bukkit.getPlayer(miner.getUuid()).sendMessage("You remove miner");
        miners.remove(location);


    }
    public static void removeMiner( Miner miner )
    {
        Bukkit.getPlayer(miner.getUuid()).sendMessage("You remove miner");
        miners.values().removeIf(miner1 -> miner1 == miner);
    }

}
