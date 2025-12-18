package com.modencore.miner.commands;

import com.modencore.miner.MinerPlugin;
import com.modencore.miner.item.MinerItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Commands:
 * - /miner give [player] [amount] - Gives a miner item
 * - /miner reload - Reloads the configuration
 */
public class MinerCommands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("miner")) {
            return false;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":
                handleGiveCommand(sender, args);
                break;
            case "reload":
                handleReloadCommand(sender);
                break;
            case "help":
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    /**
     * Usage: /miner give [player] [amount]
     */
    private void handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("miner.admin.give")) {
            sender.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("no-permission"));
            return;
        }

        Player target;
        int amount = 1;

        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found: " + args[1]);
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cUsage: /miner give <player> [amount]");
                return;
            }
            target = (Player) sender;
        }

        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1) {
                    amount = 1;
                } else if (amount > 64) {
                    amount = 64;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount: " + args[2]);
                return;
            }
        }

        target.getInventory().addItem(MinerItem.createMinerItem(amount));
        target.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("item-given"));

        if (sender != target) {
            sender.sendMessage("§aGave " + amount + " miner(s) to " + target.getName());
        }
    }

    /**
     * Usage: /miner reload
     */
    private void handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("miner.admin.reload")) {
            sender.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("no-permission"));
            return;
        }

        MinerPlugin.getPlugin().reloadConfig();
        sender.sendMessage(MinerPlugin.getPlugin().getMinerConfig().getMessage("config-reloaded"));
    }

    /**
     * Usage: /miner help
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== Imperial Roma Miner ===");
        sender.sendMessage("§e/miner give [player] [amount] §7- Give a miner item");
        sender.sendMessage("§e/miner reload §7- Reload configuration");
        sender.sendMessage("§e/miner help §7- Show this help message");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument - subcommands
            List<String> subCommands = Arrays.asList("give", "reload", "help");
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                .filter(s -> s.startsWith(input))
                .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // Second argument for give - player names
            String input = args[1].toLowerCase();
            completions = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            // Third argument for give - amount suggestions
            completions = Arrays.asList("1", "8", "16", "32", "64");
        }

        return completions;
    }
}
