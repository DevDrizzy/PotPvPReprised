package net.frozenorb.potpvp.match.rematch;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.rematch.listener.RematchGeneralListener;
import net.frozenorb.potpvp.match.rematch.listener.RematchItemListener;
import net.frozenorb.potpvp.match.rematch.listener.RematchUnloadListener;
import net.frozenorb.potpvp.util.InventoryUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RematchHandler {

    private final static int REMATCH_TIMEOUT_SECONDS = 30;

    // maps the player the potential sender to the rematch they can send (or have sent)
    private final Map<UUID, RematchData> rematches = new ConcurrentHashMap<>();

    public RematchHandler() {
        Bukkit.getPluginManager().registerEvents(new RematchGeneralListener(), PotPvPRP.getInstance());
        Bukkit.getPluginManager().registerEvents(new RematchItemListener(this), PotPvPRP.getInstance());
        Bukkit.getPluginManager().registerEvents(new RematchUnloadListener(), PotPvPRP.getInstance());

        // remove expired entries
        Bukkit.getScheduler().runTaskTimer(PotPvPRP.getInstance(), () -> {
            Iterator<RematchData> iterator = rematches.values().iterator();

            while (iterator.hasNext()) {
                RematchData rematchData = iterator.next();

                if (rematchData.isExpired()) {
                    Player sender = Bukkit.getPlayer(rematchData.getSender());

                    if (sender != null) {
                        InventoryUtils.resetInventoryDelayed(sender);
                    }

                    iterator.remove();
                }
            }
        }, 20L, 20L);
    }

    public void registerRematches(Match match) {
        // see Match#allowRematches
        if (!match.isAllowRematches()) {
            return;
        }

        List<MatchTeam> teams = match.getTeams();

        if (teams.size() == 2) {
            MatchTeam team1 = teams.get(0);
            MatchTeam team2 = teams.get(1);

            // can only send rematches for 1v1s
            if (team1.getAllMembers().size() != 1 || team2.getAllMembers().size() != 1) {
                return;
            }

            UUID player1Uuid = team1.getFirstMember();
            UUID player2Uuid = team2.getFirstMember();
            KitType kitType = match.getKitType();

            // rematches are mutual
            rematches.put(player1Uuid, new RematchData(player1Uuid, player2Uuid, kitType, REMATCH_TIMEOUT_SECONDS, match.getArena().getSchematic()));
            rematches.put(player2Uuid, new RematchData(player2Uuid, player1Uuid, kitType, REMATCH_TIMEOUT_SECONDS, match.getArena().getSchematic()));
        }
    }

    public RematchData getRematchData(Player player) {
        return rematches.get(player.getUniqueId());
    }

    public void unloadRematchData(Player player) {
        RematchData removed = rematches.remove(player.getUniqueId());

        if (removed != null) {
            // remove opponent's rematch for them too.
            rematches.remove(removed.getTarget());

            // attempt to update the target's inventory (to remove the item)
            // we don't need to do this for us as we'll only unload
            // rematch data when we quit.
            Player targetPlayer = Bukkit.getPlayer(removed.getTarget());

            if (targetPlayer != null) {
                InventoryUtils.resetInventoryDelayed(targetPlayer);
            }
        }
    }

}