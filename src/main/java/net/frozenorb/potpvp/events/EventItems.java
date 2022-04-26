package net.frozenorb.potpvp.events;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.events.Game;
import net.frozenorb.potpvp.events.GameHandler;
import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;

@UtilityClass
public final class EventItems {

    public static ItemStack getEventItem() {
        List<Game> game = PotPvPRP.getInstance().getGameHandler().getCurrentGames();

        if (game.size() > 0) {
            return ItemBuilder.of(Material.EMERALD).name(LIGHT_PURPLE + "Join An Event").build();
        }

        return null;
    }

}