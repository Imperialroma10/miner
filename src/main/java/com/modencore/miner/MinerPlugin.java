package com.modencore.miner;

import com.liba.Liba;
import com.modencore.miner.commands.MinerCommands;
import com.modencore.miner.listener.MinerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinerPlugin extends JavaPlugin {

    static MinerPlugin plugin;
    Liba liba;
    @Override
    public void onEnable() {
        plugin = this;
        this.liba = new Liba(this);   // my library  - For this to work, you must compile the liba project with the clean install command.

        getServer().getPluginCommand("miner").setExecutor(new MinerCommands());
        getServer().getPluginManager().registerEvents(new MinerListener(), this);
    }

    @Override
    public void onDisable() {
    }

    public static MinerPlugin getPlugin() {
        return plugin;
    }
}
