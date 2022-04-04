package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchHealthButton extends Button {

    private final double health;

    PostMatchHealthButton(double health) {
        this.health = health;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + health + "/10  ❤";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SPECKLED_MELON;
    }

    @Override
    public int getAmount(Player player) {
        return (int) Math.ceil(health);
    }

}