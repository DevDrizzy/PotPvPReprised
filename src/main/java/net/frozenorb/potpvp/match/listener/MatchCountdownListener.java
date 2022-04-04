package net.frozenorb.potpvp.match.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;

// the name of this listener is definitely kind of iffy (as it's really any non-IN_PROGRESS match),
// but any other ideas I had were even less descriptive
public final class MatchCountdownListener implements Listener {

    /**
     * Prevents damage in non IN_PROGRESS matches
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents throwing potions and enderpearls in in-COUNTDOWN matches
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        ItemStack item = event.getItem();
        Material type = item.getType();

        if ((type == Material.POTION && Potion.fromItemStack(item).isSplash()) || type == Material.ENDER_PEARL || type == Material.SNOW_BALL) {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
            Match match = matchHandler.getMatchPlaying(event.getPlayer());

            if (match != null && match.getState() == MatchState.COUNTDOWN) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }
        }
    }


    /**
     * Prevents bow-shooting, rods and projectiles in general from being used in non IN_PROGRESS matches.
     * @param event
     */
    @EventHandler
    public void onPlayerShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(player);

        if (match != null && match.getState() == MatchState.COUNTDOWN) {
            event.setCancelled(true);
        }
    }
}