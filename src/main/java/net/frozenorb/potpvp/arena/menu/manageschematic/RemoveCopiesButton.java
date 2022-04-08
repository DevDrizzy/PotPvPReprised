package net.frozenorb.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.List;

final class RemoveCopiesButton extends Button {

    private final ArenaSchematic schematic;

    RemoveCopiesButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED + "Remove copies of " + schematic.getName() + "";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.RED.toString() + ChatColor.BOLD + "CLICK " + ChatColor.RED + "to remove 1 copy",
            ChatColor.RED.toString() + ChatColor.BOLD + "SHIFT-CLICK " + ChatColor.RED + "to remove 10 copies",
            "",
            ChatColor.AQUA + "Scale directly to a desired quantity",
            ChatColor.AQUA + "with /arena scale " + schematic.getName() + " <count>"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        ArenaHandler arenaHandler = PotPvPRP.getInstance().getArenaHandler();
        int existing = arenaHandler.countArenas(schematic);
        int remove = clickType.isShiftClick() ? 10 : 1;
        int desired = Math.max(existing - remove, 0);

        if (arenaHandler.getGrid().isBusy()) {
            player.sendMessage(ChatColor.RED + "Grid is busy.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Starting...");

        arenaHandler.getGrid().scaleCopies(schematic, desired, () -> {
            player.sendMessage(ChatColor.GREEN + "Scaled " + schematic.getName() + " to " + desired + ".");
        });
    }

}