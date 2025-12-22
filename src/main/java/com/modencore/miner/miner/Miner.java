package com.modencore.miner.miner;

import com.modencore.miner.MinerPlugin;
import com.modencore.miner.gui.MinerMainMenu;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.awt.font.TextHitInfo;
import java.util.UUID;

public class Miner {

    UUID uuid;
    UUID owner;
    Location location;

    MinerMainMenu menu = new MinerMainMenu("Menu title", 27, this); // 27 items in menu
    BukkitTask scheduler;
    boolean enable;
    int startY;
    MinerHead minerHead;
    FuelTank fueltank = new FuelTank(this);

    public Miner(Location location, Player owner){
        uuid = UUID.randomUUID();
        this.owner = owner.getUniqueId();
        this.location = location;
        this.startY = location.clone().getBlockY()-1;
    }

    public void start(){
        if (isEnable()) return;
        enable = true;
        scheduler = Bukkit.getScheduler().runTaskTimer(MinerPlugin.getPlugin(), this::Task, 0, 20);
    }
    public void stop(){
        if (!isEnable()) return;
        enable = false;
        scheduler.cancel();
    }

    public void Task(){
        if (this.minerHead == null) this.minerHead = new MinerHead(this);

        this.minerHead.nextBlock();

    }

    public boolean isEnable() {
        return enable;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }

    public MinerMainMenu getMenu() {
        return menu;
    }

    public UUID getUuid() {
        return uuid;
    }

    public FuelTank getFueltank() {
        return fueltank;
    }



    public MinerHead getMinerHead() {
        return minerHead;
    }
}
