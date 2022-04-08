package net.frozenorb.potpvp.setting.listener;

import net.frozenorb.potpvp.PotPvPRP;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SettingLoadListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // LOWEST runs first
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PotPvPRP.getInstance().getSettingHandler().loadSettings(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR) // MONITOR runs last
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPRP.getInstance().getSettingHandler().unloadSettings(event.getPlayer());
    }

}