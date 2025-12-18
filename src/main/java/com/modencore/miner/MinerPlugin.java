package com.modencore.miner;

import com.liba.Liba;
import com.modencore.miner.commands.MinerCommands;
import com.modencore.miner.config.MinerConfig;
import com.modencore.miner.listener.MinerListener;
import com.modencore.miner.miner.MinerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinerPlugin extends JavaPlugin {

    private static MinerPlugin plugin;
    private Liba liba;
    private MinerConfig minerConfig;

    @Override
    public void onEnable() {
        plugin = this;

        this.liba = new Liba(this);

        this.minerConfig = new MinerConfig(getDataFolder().getAbsolutePath() + "/config.yml");

        MinerManager.initialize();

        MinerCommands commands = new MinerCommands();
        getServer().getPluginCommand("miner").setExecutor(commands);
        getServer().getPluginCommand("miner").setTabCompleter(commands);

        getServer().getPluginManager().registerEvents(new MinerListener(), this);

        getLogger().info("Imperial Roma Miner has been enabled!");
    }

    @Override
    public void onDisable() {
        MinerManager.shutdown();

        getLogger().info("Imperial Roma Miner has been disabled!");
    }

    @Override
    public void reloadConfig() {
        this.minerConfig.reload();
    }

    public static MinerPlugin getPlugin() {
        return plugin;
    }

    public MinerConfig getMinerConfig() {
        return minerConfig;
    }

    public Liba getLiba() {
        return liba;
    }
}
