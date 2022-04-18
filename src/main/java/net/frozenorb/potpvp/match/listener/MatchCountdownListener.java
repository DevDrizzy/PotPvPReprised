package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchStartEvent;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
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

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;

import java.util.UUID;

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

        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
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
            MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
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

        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(player);

        if (match != null && match.getState() == MatchState.COUNTDOWN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(MatchCountdownStartEvent event) {
        Match match = event.getMatch();
        if (match == null || !match.getKitType().getId().equals("SUMO") || match.getState() != MatchState.COUNTDOWN) return;

        for ( MatchTeam team : match.getTeams() ) {
            for ( UUID playerUuid : team.getAllMembers() ) {
                Player player = PotPvPRP.getInstance().getServer().getPlayer(playerUuid);
                PatchedPlayerUtils.denyMovement(player);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(MatchStartEvent event) {
        Match match = event.getMatch();
        if (match == null || !match.getKitType().getId().equals("SUMO") || match.getState() != MatchState.IN_PROGRESS) return;

        for ( MatchTeam team : match.getTeams() ) {
            for ( UUID playerUuid : team.getAllMembers() ) {
                Player player = PotPvPRP.getInstance().getServer().getPlayer(playerUuid);
                PatchedPlayerUtils.allowMovement(player);
            }
        }
    }
}