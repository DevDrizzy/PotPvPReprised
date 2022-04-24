package net.frozenorb.potpvp.kit.menu.editkit;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.util.menu.Button;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class UnselectableItemButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.RED + "You can only reorganize your inventory.";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.RED + "No items can be changed in this kit."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.GRAY.getWoolData();
    }

}