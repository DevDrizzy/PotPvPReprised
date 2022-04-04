package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorLeaveEvent;
import net.frozenorb.potpvp.match.event.MatchStartEvent;
import net.frozenorb.potpvp.match.event.MatchTerminateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles registering and un-registering the Objective that shows the health
 * below player's name-tags. This is also responsible for listening to health
 * changes and sending the update score packets manually, for consistency.
 */
public final class MatchHealthDisplayListener implements Listener {

    private static final String OBJECTIVE_NAME = "HealthDisplay";

    @EventHandler
    public void onMatchCountdownStart(MatchStartEvent event) {
        Match match = event.getMatch();

        if (!match.getKitType().isHealthShown()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            // send the health of the players in the match to all of the recipients
            for (Player player : getPlayers(match)) {
                sendAllTo(player, match);
            }
        }, 1L);
    }

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();

        if (!match.getKitType().isHealthShown()) {
            return;
        }

        // clear the objective for all players and spectators
        for (Player player : getRecipients(match)) {
            clearAll(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        // remove the dead player's scores
        if (match.getKitType().isHealthShown()) {
            for (Player viewer : getRecipients(match)) {
                clear(viewer, player);
            }
        }
    }

    @EventHandler
    public void onSpectatorJoin(MatchSpectatorJoinEvent event) {
        Match match = event.getMatch();

        if (!match.getKitType().isHealthShown()) {
            return;
        }

        // initialize the objective and send everyone's health to the spectator who joined
        sendAllTo(event.getSpectator(), match);
    }

    @EventHandler
    public void onSpectatorLeave(MatchSpectatorLeaveEvent event) {
        if (!event.getMatch().getKitType().isHealthShown()) {
            return;
        }

        // clear the spectator's health display objective
        clearAll(event.getSpectator());
    }

    private void sendAllTo(Player viewer, Match match) {
        for (Player target : getPlayers(match)) {
            Scoreboard scoreboard = viewer.getPlayer().getScoreboard();
            Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);

            if (objective == null) {
                objective = scoreboard.registerNewObjective("showhealth", "health");
            }

            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.RED + org.apache.commons.lang3.StringEscapeUtils.unescapeJava("\u2764"));
            objective.getScore(target.getName()).setScore((int) Math.floor(getHealth(target)) / 2);
        }
    }


    private void clearAll(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
        if (objective != null) {
            objective.unregister();
        }
        player.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
    }

    private void clear(Player viewer, Player target) {
        viewer.getScoreboard().resetScores(target.getName());
    }

    private List<Player> getRecipients(Match match) {
        List<Player> recipients = new ArrayList<>();
        recipients.addAll(getPlayers(match));
        match.getSpectators().stream().map(Bukkit::getPlayer).forEach(recipients::add);
        return recipients;
    }

    private List<Player> getPlayers(Match match) {
        List<Player> players = new ArrayList<>();

        for (MatchTeam team : match.getTeams()) {
            team.getAliveMembers().stream().map(Bukkit::getPlayer).forEach(players::add);
        }

        return players;
    }

    private int getHealth(Player player) {
        return (int) Math.ceil(player.getHealth() + ((CraftPlayer) player).getHandle().getAbsorptionHearts());
    }

}