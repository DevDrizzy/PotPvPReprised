package net.frozenorb.potpvp.tournament;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.profile.setting.Setting;
import net.frozenorb.potpvp.profile.setting.SettingHandler;
import net.frozenorb.potpvp.util.Clickable;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Tournament {

    // We do this because players can leave a party or the server during the tournament
    // We will need to ensure that at the end of the tournament we clear this
    // (or make sure the Tournament object is unreachable)
    private final Map<UUID, Party> partyMap = Maps.newHashMap();

    private int currentRound = -1;
    private int beginNextRoundIn = 31;
    private long roundStartedAt;
    private final int requiredPartiesToStart;

    private final List<Match> matches = Lists.newArrayList();
    private final List<Party> activeParties = Lists.newArrayList();
    private final List<Party> lost = Lists.newArrayList();

    private final int requiredPartySize;
    private final KitType type;

    private TournamentStage stage = TournamentStage.WAITING_FOR_TEAMS;

    public Tournament(KitType type, int partySize, int requiredPartiesToStart) {
        this.type = type;
        this.requiredPartySize = partySize;
        this.requiredPartiesToStart = requiredPartiesToStart;
    }

    public void addParty(Party party) {
        activeParties.add(party);

        this.checkActiveParties();
        this.joinedTournament(party);
        this.checkStart();
    }

    public boolean isInTournament(Party party) {
        return activeParties.contains(party);
    }

    public void check() {
        this.checkActiveParties();
        this.populatePartyMap();
        this.checkMatches();

        if (matches.stream().anyMatch(s -> s != null && s.getState() != MatchState.TERMINATED)) return; // We don't want to advance to the next round if any matches are ongoing
        matches.clear();

        if (currentRound == -1) return;

        if (activeParties.isEmpty()) {
            if (lost.isEmpty()) {
                stage = TournamentStage.FINISHED;
                PotPvPRP.getInstance().getTournamentHandler().setTournament(null);
                return;
            }

            // shouldn't happen, meant that the two last parties disconnected at the last second
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cThe tournament's last two teams forfeited. Winner by default: " + PatchedPlayerUtils.getFormattedName((lost.get(lost.size() - 1)).getLeader()) + "'s team!"));
            PotPvPRP.getInstance().getTournamentHandler().setTournament(null); // Removes references to this tournament, will get cleaned up by GC
            stage = TournamentStage.FINISHED;
            return;
        }

        if (activeParties.size() == 1) {
            Party party = activeParties.get(0);
            if (party.getMembers().size() == 1) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + " &fwon the tournament!"));
            } else if (party.getMembers().size() == 2) {
                Iterator<UUID> membersIterator = party.getMembers().iterator();
                UUID[] members = new UUID[] { membersIterator.next(), membersIterator.next() };
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + PatchedPlayerUtils.getFormattedName(members[0]) + " &7and &c&l" + PatchedPlayerUtils.getFormattedName(members[1]) + " &7won the tournament!"));
            } else {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + PatchedPlayerUtils.getFormattedName(party.getLeader()) + "&7's team won the tournament!"));
            }

            activeParties.clear();
            PotPvPRP.getInstance().getTournamentHandler().setTournament(null);
            stage = TournamentStage.FINISHED;
            return;
        }

        if (--beginNextRoundIn >= 1) {
            switch (beginNextRoundIn) {
            case 30:
            case 15:
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                if (currentRound == 0) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7The &c&ltournament &7will begin in &c" + beginNextRoundIn + " &7second" + (beginNextRoundIn == 1 ? "" : "s") + "."));
                } else {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lRound " + (currentRound + 1) + " &7will begin in &c" + beginNextRoundIn + " &7second" + (beginNextRoundIn == 1 ? "" : "s") + "."));
                }
            }

            if (beginNextRoundIn == 30 && currentRound == 0) {
                Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Only donors can join the tournament beyond this point!");
            }

            stage = TournamentStage.COUNTDOWN;
            return;
        }

        startRound();
    }

    private void checkActiveParties() {
        Set<UUID> realParties = PotPvPRP.getInstance().getPartyHandler().getParties().stream().map(Party::getPartyId).collect(Collectors.toSet());
        Iterator<Party> activePartyIterator = activeParties.iterator();
        while (activePartyIterator.hasNext()) {
            Party activeParty = activePartyIterator.next();
            if (!realParties.contains(activeParty.getPartyId())) {
                activePartyIterator.remove();

                if (!lost.contains(activeParty)) {
                    lost.add(activeParty);
                }
            }
        }
    }

    public void checkStart() {
        if (activeParties.size() == requiredPartiesToStart) {
            start();
        }
    }

    public void start() {
        if (currentRound == -1) {
            currentRound = 0;
        }
    }

    private void joinedTournament(Party party) {
        broadcastJoinMessage(party);
    }

    private void populatePartyMap() {
        activeParties.forEach(p -> p.getMembers().forEach(u -> partyMap.put(u, p)));
    }

    private void startRound() {
        beginNextRoundIn = 31;
        // Next round has begun...

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lRound " + ++currentRound + " &7has begun. Good luck!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7Use &c/t status &7to see who is fighting."));

        List<Party> oldPartyList = Lists.newArrayList(activeParties);
        // Collections.shuffle(oldPartyList);
        // Doing it this way will ensure that the tournament runs BUT if one party
        // disconnects every round, the bottom party could get to the final round without
        // winning a single duel. Could shuffle? But would remove the predictability & pseudo-bracket system
        while (1 < oldPartyList.size()) {
            Party firstParty = oldPartyList.remove(0);
            Party secondParty = oldPartyList.remove(0);

            matches.add(PotPvPRP.getInstance().getMatchHandler().startMatch(ImmutableList.of(new MatchTeam(firstParty.getMembers()), new MatchTeam(secondParty.getMembers())), type, false, false));
        }

        if (oldPartyList.size() == 1) {
            oldPartyList.get(0).message(ChatColor.RED + "There were an odd number of teams in this round - so your team has advanced to the next round.");
        }

        stage = TournamentStage.IN_PROGRESS;
        roundStartedAt = System.currentTimeMillis();
    }

    private void checkMatches() {
        Iterator<Match> matchIterator = matches.iterator();
        while (matchIterator.hasNext()) {
            Match match = matchIterator.next();
            if (match == null) {
                matchIterator.remove();
                continue;
            }

            if (match.getState() != MatchState.TERMINATED) continue;
            MatchTeam winner = match.getWinner();
            List<MatchTeam> losers = Lists.newArrayList(match.getTeams());
            losers.remove(winner);
            MatchTeam loser = losers.get(0);
            Party loserParty = partyMap.get(loser.getFirstMember());
            if (loserParty != null) {
                activeParties.remove(loserParty);
                broadcastEliminationMessage(loserParty);
                lost.add(loserParty);
                matchIterator.remove();
            }
        }
    }

    public void broadcastJoinMessage() {
        int teamSize = this.getRequiredPartySize();
        int requiredTeams = this.getRequiredPartiesToStart();

        int multiplier = teamSize < 3 ? teamSize : 1;

        if (this.getCurrentRound() != -1) return;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7A &c&ltournament&7 has started. Type &c/t join&7 to play. (" + (this.activeParties.size() * multiplier) + "/" + (requiredTeams * multiplier) + ")"));
        Bukkit.broadcastMessage("");
    }

    private void broadcastJoinMessage(Party joiningParty) {
        if (getCurrentRound() != -1) {
            // donor join
            String message;
            if (joiningParty.getMembers().size() == 1) {
                message = ChatColor.translateAlternateColorCodes('&', "&c&lDONOR ONLY &7- " + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&7 &7has &7joined &7the &5tournament&7. &7(" + activeParties.size() + "/" + requiredPartiesToStart + "&7)");
            } else if (joiningParty.getMembers().size() == 2) {
                Iterator<UUID> membersIterator = joiningParty.getMembers().iterator();
                message = ChatColor.translateAlternateColorCodes('&', "&c&lDONOR ONLY &7- " + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 &7and &c" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 have joined the &5tournament&7. &7(" + activeParties.size() * 2 + "/" + requiredPartiesToStart * 2 + "&7)");
            } else {
                message = ChatColor.translateAlternateColorCodes('&', "&c&lDONOR ONLY &7- " + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&7's team has joined the &5tournament&7. &7(" + activeParties.size() + "/" + requiredPartiesToStart + "&7)");
            }

            Clickable clickable = new Clickable(message, ChatColor.translateAlternateColorCodes('&', "&cDonors &7can join during the tournament countdown."), "");
            Bukkit.getOnlinePlayers().forEach(clickable::sendToPlayer);
            return;
        }

        String message;
        if (joiningParty.getMembers().size() == 1) {
            message = ChatColor.translateAlternateColorCodes('&', "&c" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&7 has joined the &5tournament&7. &7(" + activeParties.size() + "/" + requiredPartiesToStart + "&7)");
        } else if (joiningParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = joiningParty.getMembers().iterator();
            message = ChatColor.translateAlternateColorCodes('&', "&c" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 and &c" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 have joined the &5tournament&7. &7(" + activeParties.size() * 2 + "/" + requiredPartiesToStart * 2 + "&7)");
        } else {
            message = ChatColor.translateAlternateColorCodes('&', "&c" + PatchedPlayerUtils.getFormattedName(joiningParty.getLeader()) + "&7's team has joined the &5tournament&7. &7(" + activeParties.size() + "/" + requiredPartiesToStart + "&7)");
        }
        
        Clickable clickable = new Clickable(message, ChatColor.translateAlternateColorCodes('&', "&c&lCLICK &7to hide this message."), "/djm");

        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (joiningParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_JOIN_MESSAGE)) {
                clickable.sendToPlayer(player);
            }
        }
    }

    private void broadcastEliminationMessage(Party loserParty) {
        String message;
        int multiplier = requiredPartySize < 3 ? requiredPartySize : 1;
        if (loserParty.getMembers().size() == 1) {
            message = ChatColor.translateAlternateColorCodes('&', "&c" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&7 has been eliminated. &7(" + activeParties.size() * multiplier + "/" + requiredPartiesToStart * multiplier + "&7)");
        } else if (loserParty.getMembers().size() == 2) {
            Iterator<UUID> membersIterator = loserParty.getMembers().iterator();
            message = ChatColor.translateAlternateColorCodes('&', "&c" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + "&7 and &c" + PatchedPlayerUtils.getFormattedName(membersIterator.next()) + " &7were eliminated. &7(" + activeParties.size() * multiplier + "/" + requiredPartiesToStart * multiplier + "&7)");
        } else {
            message = ChatColor.translateAlternateColorCodes('&', "&c" + PatchedPlayerUtils.getFormattedName(loserParty.getLeader()) + "&7's team has been eliminated. &7(" + activeParties.size() * multiplier + "/" + requiredPartiesToStart * multiplier + "&7)");
        }

        Clickable clickable = new Clickable(message, ChatColor.translateAlternateColorCodes('&', "&c&lCLICK &7to hide this message."), "/dem");
        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (loserParty.isMember(player.getUniqueId()) || settingHandler.getSetting(player, Setting.SEE_TOURNAMENT_ELIMINATION_MESSAGES)) {
                clickable.sendToPlayer(player);
            }
        }
    }


    public enum TournamentStage {
        WAITING_FOR_TEAMS,
        COUNTDOWN,
        IN_PROGRESS,
        FINISHED
    }
}
