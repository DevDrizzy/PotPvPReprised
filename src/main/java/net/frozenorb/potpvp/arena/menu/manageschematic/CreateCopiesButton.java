package net.frozenorb.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.List;

final class CreateCopiesButton extends Button {

    private final ArenaSchematic schematic;

    CreateCopiesButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Create copies of " + schematic.getName() + "";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to create 1 new copy",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "SHIFT-CLICK " + ChatColor.GREEN + "to create 10 new copies",
            "",
            ChatColor.AQUA + "Scale directly to a desired quantity",
            ChatColor.AQUA + "with /arena scale " + schematic.getName() + " <count>"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.EMERALD_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        int existing = arenaHandler.countArenas(schematic);
        int create = clickType.isShiftClick() ? 10 : 1;
        int desired = existing + create;

        if (arenaHandler.getGrid().isBusy()) {
            player.sendMessage(ChatColor.RED + "Grid is busy.");
            return;
        }

        try {
            player.sendMessage(ChatColor.GREEN + "Starting...");

            arenaHandler.getGrid().scaleCopies(schematic, desired, () -> {
                player.sendMessage(ChatColor.GREEN + "Scaled " + schematic.getName() + " to " + desired + ".");
            });
        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + "Failed to paste " + schematic.getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}