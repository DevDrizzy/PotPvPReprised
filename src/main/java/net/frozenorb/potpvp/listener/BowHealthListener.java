package net.frozenorb.potpvp.listener;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.match.MatchHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class BowHealthListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER || !(event.getDamager() instanceof Arrow)) {
            return;
        }

        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
        Player hit = (Player) event.getEntity();
        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        if (damager != null) {
            Bukkit.getScheduler().runTaskLater(PotPvPRP.getInstance(), () -> {
                // in case the player died because of this hit
                if (!matchHandler.isPlayingMatch(hit)) {
                    return;
                }

                int outOf20 = (int) Math.ceil(hit.getHealth());
                // we specifically divide by 2.0 (not 2) so that we do floating point math
                // as integer math will just round away the .5
                damager.sendMessage(ChatColor.GOLD + hit.getName() + "'s health: " + ChatColor.RED.toString() + (outOf20 / 2.0) + ChatColor.DARK_RED + "❤");
            }, 1L);
        }
    }

}