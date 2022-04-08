package net.frozenorb.potpvp.util;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.lobby.LobbyUtils;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class InventoryUtils {

    public static final long RESET_DELAY_TICKS = 2L;

    public static void resetInventoryDelayed(Player player) {
        Runnable task = () -> resetInventoryNow(player);
        Bukkit.getScheduler().runTaskLater(PotPvPRP.getInstance(), task, RESET_DELAY_TICKS);
    }

    public static void resetInventoryNow(Player player) {
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(player)) {
            MatchUtils.resetInventory(player);
        } else {
            LobbyUtils.resetInventory(player);
        }
    }

}