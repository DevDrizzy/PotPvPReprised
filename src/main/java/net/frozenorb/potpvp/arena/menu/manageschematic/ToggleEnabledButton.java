package net.frozenorb.potpvp.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.io.IOException;
import java.util.List;

final class ToggleEnabledButton extends Button {

    private final ArenaSchematic schematic;

    ToggleEnabledButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        if (schematic.isEnabled()) {
            return ChatColor.RED + "Disable " + schematic.getName();
        } else {
            return ChatColor.GREEN + "Enable " + schematic.getName();
        }
    }

    @Override
    public List<String> getDescription(Player player) {
        if (schematic.isEnabled()) {
            return ImmutableList.of(
                "",
                ChatColor.YELLOW + "Click to disable " + schematic.getName() + ", which will prevent matches",
                ChatColor.YELLOW + "being scheduled on these arenas. Admin",
                ChatColor.YELLOW + "commands will not be impacted."
            );
        } else {
            return ImmutableList.of(
                    "",
                    ChatColor.YELLOW + "Click to enable " + schematic.getName() + ", which will allow matches",
                    ChatColor.YELLOW + "to be scheduled on these arenas."
            );
        }
    }

    @Override
    public Material getMaterial(Player player) {
        // we invert our normal logic here to show the 'potential' state (similiar to
        // video player play/pause buttons)
        return schematic.isEnabled() ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
        schematic.setEnabled(!schematic.isEnabled());

        PotPvPRP.getInstance().getArenaHandler().saveSchematics();
    }

}