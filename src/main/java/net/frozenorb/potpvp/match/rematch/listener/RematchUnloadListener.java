package net.frozenorb.potpvp.match.rematch.listener;

import net.frozenorb.potpvp.PotPvPRP;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RematchUnloadListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPRP.getInstance().getRematchHandler().unloadRematchData(event.getPlayer());
    }

}