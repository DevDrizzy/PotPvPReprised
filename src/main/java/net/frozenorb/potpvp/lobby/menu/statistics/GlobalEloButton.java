package net.frozenorb.potpvp.lobby.menu.statistics;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.profile.elo.EloHandler;
import net.frozenorb.potpvp.util.menu.Button;

public class GlobalEloButton extends Button {

    private static EloHandler eloHandler = PotPvPRP.getInstance().getEloHandler();

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_PURPLE + "Global" + ChatColor.GRAY.toString() + ChatColor.BOLD + " | " + ChatColor.WHITE + "Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        int counter = 1;

        for (Entry<String, Integer> entry : eloHandler.topElo(null).entrySet()) {
            String color = (counter <= 3 ? ChatColor.DARK_PURPLE : ChatColor.GRAY).toString();
            description.add(color + counter + ChatColor.GRAY.toString() + ChatColor.BOLD + " | " + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());

            counter++;
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
}
