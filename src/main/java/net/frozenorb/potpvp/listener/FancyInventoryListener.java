package net.frozenorb.potpvp.listener;

import net.frozenorb.potpvp.util.FancyPlayerInventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

// this handles our "Fancy" player inventory used in matches to see
// inventories as spectators. All it really does is show the armor
// at the top, and move the hotbar all the way to the bottom.
public class FancyInventoryListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FancyPlayerInventory.join(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FancyPlayerInventory.quit(event.getPlayer());
    }

}
