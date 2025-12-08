package com.modencore.miner.miner;

import com.modencore.miner.gui.MinerMainMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Miner {

    UUID uuid;
    UUID owner;
    Location location;

    MinerMainMenu menu = new MinerMainMenu("Menu title", 27); // 27 items in menu

    public Miner(Location location, Player owner){
        uuid = UUID.randomUUID();
        this.owner = owner.getUniqueId();
        this.location = location;
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
}
