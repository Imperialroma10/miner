package com.modencore.miner.gui;

import com.liba.menu.Menu;
import com.liba.menu.MenuSlot;
import com.liba.menu.buttons.BooleanButton;
import com.liba.menu.buttons.TextButton;
import com.liba.utils.ItemUtil;
import com.modencore.miner.MinerPlugin;
import com.modencore.miner.config.MinerConfig;
import com.modencore.miner.miner.Miner;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class MinerMainMenu extends Menu {
    
    private final Miner miner;
    private BooleanButton toggleButton;

    public MinerMainMenu(Miner miner) {
        super(MinerPlugin.getPlugin().getMinerConfig().getGuiTitle(), 27);
        this.miner = miner;
    }

    @Override
    public void setItems() {
        MinerConfig config = MinerPlugin.getPlugin().getMinerConfig();
        
        addSlot(4, createStatusSlot());
        
        ItemStack startItem = ItemUtil.create(
            new ItemStack(Material.LIME_WOOL),
            config.getString("gui.start-button-name"),
            Arrays.asList(
                "&7Click to start mining",
                "",
                "&aStatus: " + config.getString("gui.status-idle")
            )
        );
        
        ItemStack stopItem = ItemUtil.create(
            new ItemStack(Material.RED_WOOL),
            config.getString("gui.stop-button-name"),
            Arrays.asList(
                "&7Click to stop mining",
                "",
                "&cStatus: " + config.getString("gui.status-mining")
            )
        );
        
        toggleButton = new BooleanButton(stopItem, startItem, miner.isEnabled());
        
        addSlot(12, new MenuSlot(toggleButton, (player, event) -> {
            event.setCancelled(true);
            
            if (miner.isEnabled()) {
                miner.stop();
            } else {
                miner.start();
            }
            
            toggleButton.setVariable(miner.isEnabled());
            updateSlot(12);
            updateSlot(4);
        }));
        
        addSlot(13, createMinerInfoSlot());
        
        ItemStack removeItem = ItemUtil.create(
            new ItemStack(Material.BARRIER),
            config.getString("gui.remove-button-name"),
            Arrays.asList(
                "&7Click to remove this miner",
                "&7The miner block will drop",
                "",
                "&cWarning: &7This action cannot be undone!"
            )
        );
        
        addSlot(14, new MenuSlot(new TextButton(removeItem), (player, event) -> {
            event.setCancelled(true);
            player.closeInventory();
            
            // Remove the miner (this will drop the item)
            miner.remove();
        }));
        
        // Drop Mode Toggle Button
        addSlot(20, createDropModeSlot());
        
        // Collect Items Button 
        addSlot(22, createCollectItemsSlot());
        
        // Fill empty slots with glass panes for better appearance
        fillEmptySlots();
    }
    
    /**
     * Creates the drop mode toggle button slot.
     */
    private MenuSlot createDropModeSlot() {
        Material icon = switch (miner.getDropMode().toLowerCase()) {
            case "block" -> Material.DIRT;
            case "inventory" -> Material.ENDER_CHEST;
            default -> Material.HOPPER; // miner
        };
        
        ItemStack dropModeItem = ItemUtil.create(
            new ItemStack(icon),
            "&d&lDrop Mode",
            Arrays.asList(
                "",
                "&7Current: &f" + miner.getDropModeDisplayName(),
                "",
                "&7Options:",
                "&8 • &fAbove Miner &7- Items drop on top",
                "&8 • &fAt Block &7- Items drop where mined",
                "&8 • &fInternal Storage &7- Collect via GUI",
                "",
                "&eClick to change mode"
            )
        );
        
        return new MenuSlot(new TextButton(dropModeItem), (player, event) -> {
            event.setCancelled(true);
            
            miner.cycleDropMode();
            
            player.closeInventory();
            miner.getMenu().open(player);
        });
    }
    
    /**
     * Creates the collect items button slot.
     */
    private MenuSlot createCollectItemsSlot() {
        int itemCount = miner.getInventoryItemCount();
        
        ItemStack collectItem = ItemUtil.create(
            new ItemStack(Material.CHEST),
            "&e&lCollect Items",
            Arrays.asList(
                "",
                "&7Items in storage: &f" + itemCount,
                "",
                "&eClick to collect all items",
                "&7Items will go to your inventory"
            )
        );
        
        return new MenuSlot(new TextButton(collectItem), (player, event) -> {
            event.setCancelled(true);
            
            if (miner.getInventoryItemCount() == 0) {
                player.sendMessage("&7The miner inventory is empty.");
                return;
            }
            
            miner.collectInventory(player);
            player.sendMessage("&aCollected all items from the miner!");
            
            // Refresh the menu to update item count
            updateSlot(22);
            updateSlot(4);
        });
    }

    /**
     * Creates the status information slot showing mining progress.
     */
    private MenuSlot createStatusSlot() {
        MinerConfig config = MinerPlugin.getPlugin().getMinerConfig();
        
        String statusText = miner.isEnabled() 
            ? config.getString("gui.status-mining") 
            : config.getString("gui.status-idle");
        
        ItemStack statusItem = ItemUtil.create(
            new ItemStack(miner.isEnabled() ? Material.GLOWSTONE : Material.COAL_BLOCK),
            "&6&lMiner Status",
            Arrays.asList(
                "",
                "&7Status: " + statusText,
                "&7Current Y: &f" + miner.getCurrentY(),
                "&7Blocks Mined: &f" + miner.getBlocksMinedTotal(),
                "&7Items in Storage: &f" + miner.getInventoryItemCount(),
                "",
                "&7Mining Radius: &f" + config.getMinerRadius() + "x" + config.getMinerRadius(),
                "&7Drop Mode: &f" + miner.getDropModeDisplayName()
            )
        );
        
        return new MenuSlot(new TextButton(statusItem), (player, event) -> {
            event.setCancelled(true);
        });
    }

    /**
     * Creates the miner information slot showing location and owner info.
     */
    private MenuSlot createMinerInfoSlot() {
        ItemStack infoItem = ItemUtil.create(
            new ItemStack(Material.STONECUTTER),
            "&b&lMiner Information",
            Arrays.asList(
                "",
                "&7Location:",
                "&8  X: &f" + miner.getLocation().getBlockX(),
                "&8  Y: &f" + miner.getLocation().getBlockY(),
                "&8  Z: &f" + miner.getLocation().getBlockZ(),
                "",
                "&7World: &f" + miner.getWorldName()
            )
        );
        
        return new MenuSlot(new TextButton(infoItem), (player, event) -> {
            event.setCancelled(true);
        });
    }

    /**
     * Fills empty slots with gray glass panes for a cleaner look.
     */
    private void fillEmptySlots() {
        ItemStack filler = ItemUtil.create(
            new ItemStack(Material.GRAY_STAINED_GLASS_PANE),
            " "
        );
        
        for (int i = 0; i < 27; i++) {
            if (getMenuSlot(i) == null) {
                addSlot(i, new MenuSlot(new TextButton(filler), (player, event) -> {
                    event.setCancelled(true);
                }));
            }
        }
    }
}
