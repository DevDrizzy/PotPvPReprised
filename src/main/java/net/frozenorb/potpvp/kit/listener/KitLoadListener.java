package net.frozenorb.potpvp.kit.listener;

import net.frozenorb.potpvp.PotPvPRP;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class KitLoadListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // LOWEST runs first
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PotPvPRP.getInstance().getKitHandler().loadKits(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR) // MONITOR runs last
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPRP.getInstance().getKitHandler().unloadKits(event.getPlayer());
    }

}