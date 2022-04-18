package net.frozenorb.potpvp.match.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchFoodLevelButton extends Button {

    private final int foodLevel;

    PostMatchFoodLevelButton(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + foodLevel + "/20 Hunger";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.COOKED_BEEF;
    }

    @Override
    public int getAmount(Player player) {
        return foodLevel;
    }

}