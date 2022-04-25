
package net.frozenorb.potpvp.adapter.scoreboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.sk89q.worldedit.math.MathUtils;
import net.frozenorb.potpvp.util.TimeUtils;
import net.frozenorb.potpvp.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.profile.elo.EloHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.MatchQueue;
import net.frozenorb.potpvp.queue.MatchQueueEntry;
import net.frozenorb.potpvp.queue.QueueHandler;
import xyz.refinedev.spigot.utils.CC;

final class LobbyScoreGetter implements BiConsumer<Player, List<String>> {

    @Override
    public void accept(Player player, List<String> scores) {
        Optional<UUID> followingOpt = PotPvPRP.getInstance().getFollowHandler().getFollowing(player);
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        EloHandler eloHandler = PotPvPRP.getInstance().getEloHandler();

        Party playerParty = partyHandler.getParty(player);
        MatchQueueEntry entry = getQueueEntry(player);
        Tournament tournament = PotPvPRP.getInstance().getTournamentHandler().getTournament();

        scores.add("&fOnline: " + ChatColor.RED + PotPvPRP.getInstance().getCache().getOnlineCount());
        scores.add("&fFighting: " + ChatColor.RED + PotPvPRP.getInstance().getCache().getFightsCount());
        scores.add("&fQueueing: " + ChatColor.RED + PotPvPRP.getInstance().getCache().getQueuesCount());
        
        // this definitely can be a .ifPresent, however creating the new lambda that often
        // was causing some performance issues, so we do this less pretty (but more efficient)
        // check (we can't define the lambda up top and reference because we reference the
        // scores variable)
        if (followingOpt.isPresent()) {
            Player following = Bukkit.getPlayer(followingOpt.get());
            scores.add("&fFollowing: &c" + following.getName());

            if (player.hasPermission("potpvp.silent")) {
                MatchQueueEntry targetEntry = getQueueEntry(following);

                if (targetEntry != null) {
                    MatchQueue queue = targetEntry.getQueue();
                    scores.add("&fTarget Queue: " + ChatColor.GRAY + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKitType().getDisplayName());
                }
            }
        } else if (entry != null) {
            String waitTimeFormatted = TimeUtils.formatIntoMMSS(entry.getWaitSeconds());
            MatchQueue queue = entry.getQueue();

            scores.add("");
            scores.add(ChatColor.GREEN + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKitType().getDisplayName());
            scores.add("&fTime: " + ChatColor.RED + waitTimeFormatted);
            if (queue.isRanked()) {
                int elo = eloHandler.getElo(entry.getMembers(), queue.getKitType());
                int window = entry.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                scores.add(ChatColor.WHITE + "ELO Range: " + ChatColor.RED + Math.max(0, (elo - window)) + " - " + (elo + window));
            }
        } else if (tournament != null) {
            scores.add("");
            scores.add("&fTournament");

            if (tournament.getStage() == Tournament.TournamentStage.WAITING_FOR_TEAMS) {
                int teamSize = tournament.getRequiredPartySize();
                scores.add("&c Kit&7: " + tournament.getType().getDisplayName());
                scores.add("&c Team Size&7: " + teamSize + "v" + teamSize);
                int multiplier = teamSize < 3 ? teamSize : 1;
                scores.add("&c " + (teamSize < 3 ? "Players"  : "Teams") + "&7: " + (tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier));
            } else if (tournament.getStage() == Tournament.TournamentStage.COUNTDOWN) {
                if (tournament.getCurrentRound() == 0) {
                    scores.add("");
                    scores.add("&7 Begins in &c" + tournament.getBeginNextRoundIn() + "&7 second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                } else {
                    scores.add("");
                    scores.add("&c&l Round " + (tournament.getCurrentRound() + 1));
                    scores.add("&7 Begins in &c" + tournament.getBeginNextRoundIn() + "&7 second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                }
            } else if (tournament.getStage() == Tournament.TournamentStage.IN_PROGRESS) {
                scores.add("&c Round&7: " + tournament.getCurrentRound());

                int teamSize = tournament.getRequiredPartySize();
                int multiplier = teamSize < 3 ? teamSize : 1;

                scores.add("&c" + (teamSize < 3 ? "Players" : "Teams") + "&7: " + tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier);
                scores.add("&f Duration: &c" + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - tournament.getRoundStartedAt()) / 1000));
            }
        } else if (playerParty != null) {
            scores.add("");
            scores.add("&cParty: ");
            scores.add("&f Leader: " + ChatColor.RED + PotPvPRP.getInstance().getUuidCache().name(playerParty.getLeader()));
            scores.add("&f Members: " + ChatColor.RED + playerParty.getMembers().size() + "/" + Party.MAX_SIZE);
        }
        scores.add("");
        scores.add("&rtest.refinedev.xyz");
        if (player.hasMetadata("Silent")) {
            scores.add(ChatColor.GRAY + ChatColor.BOLD.toString() + "Silent Mode");
        }
    }

    private MatchQueueEntry getQueueEntry(Player player) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        QueueHandler queueHandler = PotPvPRP.getInstance().getQueueHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            return queueHandler.getQueueEntry(playerParty);
        } else {
            return queueHandler.getQueueEntry(player.getUniqueId());
        }
    }

}