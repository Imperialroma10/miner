package com.modencore.miner.commands;

import com.modencore.miner.miner.Miner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MinerCommands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("miner")){
            if (cs instanceof Player player){
                if (args.length == 1){
                    if (args[0].equalsIgnoreCase("getminer")){
                        player.getInventory().addItem(Miner.getMinerItemStack());
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("miner")){
            if (args.length == 0){
                return List.of("getminer");
            }
        }
        return List.of();
    }
}
