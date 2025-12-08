package com.modencore.miner;

import com.liba.Liba;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinerPlugin extends JavaPlugin {

    static MinerPlugin plugin;
    Liba liba;
    @Override
    public void onEnable() {
         liba = new Liba(this);   // my library  - For this to work, you must compile the liba project with the clean install command.


    }

    @Override
    public void onDisable() {
    }

    public static MinerPlugin getPlugin() {
        return plugin;
    }
}
