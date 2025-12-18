package com.modencore.miner.config;

import com.liba.utils.MinecraftColor;
import com.liba.utils.file.FileChecker;

import java.util.Arrays;
import java.util.List;

public class MinerConfig extends FileChecker {

    public MinerConfig(String filedir) {
        super(filedir, "Imperial Roma Miner Configuration\nConfigure your miner settings below.");
    }

    @Override
    public void needle() {
        // Mining Settings
        addParam("miner-radius", 3,
                "The radius of the mining area (creates a square).",
                "A radius of 3 means a 3x3 area will be mined.");

        addParam("dig-delay-ticks", 10,
                "Delay in ticks between mining each layer.",
                "20 ticks = 1 second. Lower values = faster mining.");

        addParam("blocks-per-tick", 3,
                "Number of blocks to mine per tick within a layer.",
                "Higher values mine faster but may cause more lag.");

        // Miner Item Settings
        addParam("miner-name", "&6&lAuto Miner",
                "Display name for the miner item.");

        addParam("miner-lore", Arrays.asList(
                "&7Place this block to create",
                "&7an automatic mining machine!",
                "",
                "&eRight-click &7to open menu",
                "&eRadius: &f3x3 blocks"), "Lore lines for the miner item.");

        // Messages
        addParam("messages.miner-placed", "&aYou have placed an Auto Miner!",
                "Message sent when a miner is placed.");

        addParam("messages.miner-removed", "&cYou have removed the Auto Miner.",
                "Message sent when a miner is removed.");

        addParam("messages.miner-started", "&aMiner started! Mining down to bedrock...",
                "Message sent when mining starts.");

        addParam("messages.miner-stopped", "&eMiner stopped.",
                "Message sent when mining is manually stopped.");

        addParam("messages.miner-complete", "&6Mining complete! Reached bedrock.",
                "Message sent when the miner reaches bedrock.");

        addParam("messages.no-permission", "&cYou don't have permission to do that.",
                "Message sent when player lacks permission.");

        addParam("messages.item-given", "&aYou have received an Auto Miner!",
                "Message sent when a miner item is given.");

        addParam("messages.config-reloaded", "&aConfiguration reloaded successfully!",
                "Message sent when config is reloaded.");

        // GUI Settings
        addParam("gui.title", "&8Auto Miner Control",
                "Title of the miner control GUI.");

        addParam("gui.start-button-name", "&aStart Mining",
                "Name of the start button.");

        addParam("gui.stop-button-name", "&cStop Mining",
                "Name of the stop button.");

        addParam("gui.remove-button-name", "&4Remove Miner",
                "Name of the remove button.");

        addParam("gui.status-mining", "&aMining...",
                "Status text when miner is active.");

        addParam("gui.status-idle", "&7Idle",
                "Status text when miner is inactive.");
    }

    public int getMinerRadius() {
        return getInt("miner-radius");
    }

    public int getDigDelayTicks() {
        return getInt("dig-delay-ticks");
    }

    public int getBlocksPerTick() {
        return getInt("blocks-per-tick");
    }

    public String getMinerName() {
        return getString("miner-name");
    }

    @SuppressWarnings("unchecked")
    public List<String> getMinerLore() {
        return (List<String>) getParamList("miner-lore");
    }

    public String getGuiTitle() {
        return getString("gui.title");
    }

    public String getMessage(String key) {
        String message = getString("messages." + key);
        return MinecraftColor.format(message);
    }
}
