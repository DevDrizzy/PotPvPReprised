package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.tournament.Tournament;
import net.frozenorb.potpvp.tournament.TournamentHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.api.annotation.Command;
import xyz.refinedev.api.annotation.Require;
import xyz.refinedev.api.annotation.Sender;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/4/2022
 * Project: potpvp-reprised
 */

public class TournamentCommands implements PotPvPCommand {

    public TournamentHandler instance = PotPvPRP.getInstance().getTournamentHandler();

    @Command(name = "start", usage= "<kitType> <teamSize> <requiredTeams>", desc = "Host a tournament")
    @Require("tournament.create")
    public void tournamentCreate(@Sender CommandSender sender, KitType type, int teamSize, int requiredTeams) {
        if (instance.getTournament() != null) {
            sender.sendMessage(ChatColor.RED + "There's already an ongoing tournament!");
            return;
        }

        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Kit type not found!");
            return;
        }

        if (teamSize < 1 || 10 < teamSize) {
            sender.sendMessage(ChatColor.RED + "Invalid team size range. Acceptable inputs: 1 -> 10");
            return;
        }

        if (requiredTeams < 4) {
            sender.sendMessage(ChatColor.RED + "Required teams must be at least 4.");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7A &c&ltournament&7 has started. Type &5/join&7 to play. (0/" + (teamSize < 3 ? teamSize * requiredTeams : requiredTeams) + ")"));
        Bukkit.broadcastMessage("");

        Tournament tournament;
        instance.setTournament(tournament = new Tournament(type, teamSize, requiredTeams));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (instance.getTournament() == tournament) {
                    tournament.broadcastJoinMessage();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(PotPvPRP.getInstance(), 60 * 20, 60 * 20);
    }

    @Command(name = "join", desc = "Join a tournament")
    public void tournamentJoin(@Sender Player sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to join.");
            return;
        }

        int tournamentTeamSize = instance.getTournament().getRequiredPartySize();

        if ((instance.getTournament().getCurrentRound() != -1 || instance.getTournament().getBeginNextRoundIn() != 31) && (instance.getTournament().getCurrentRound() != 0 || !sender.hasPermission("tournaments.joinduringcountdown"))) {
            sender.sendMessage(ChatColor.RED + "This tournament is already in progress.");
            return;
        }

        Party senderParty = PotPvPRP.getInstance().getPartyHandler().getParty(sender);
        if (senderParty == null) {
            if (tournamentTeamSize == 1) {
                senderParty = PotPvPRP.getInstance().getPartyHandler().getOrCreateParty(sender); // Will auto put them in a party
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have a team to join the tournament with!");
                return;
            }
        }

        int notInLobby = 0;
        int queued = 0;
        for ( UUID member : senderParty.getMembers()) {
            if (!PotPvPRP.getInstance().getLobbyHandler().isInLobby(Bukkit.getPlayer(member))) {
                notInLobby++;
            }

            if (PotPvPRP.getInstance().getQueueHandler().getQueueEntry(member) != null) {
                queued++;
            }
        }

        if (notInLobby != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team aren't in the lobby.");
            return;
        }

        if (queued != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team are currently queued.");
            return;
        }

        if (!senderParty.getLeader().equals(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You must be the leader of your team to join the tournament.");
            return;
        }

        if (instance.isInTournament(senderParty)) {
            sender.sendMessage(ChatColor.RED + "Your team is already in the tournament!");
            return;
        }

        if (senderParty.getMembers().size() != instance.getTournament().getRequiredPartySize()) {
            sender.sendMessage(ChatColor.RED + "You need exactly " + instance.getTournament().getRequiredPartySize() + " members in your party to join the tournament.");
            return;
        }

        if (PotPvPRP.getInstance().getQueueHandler().getQueueEntry(senderParty) != null) {
            sender.sendMessage(ChatColor.RED + "You can't join the tournament if your party is currently queued.");
            return;
        }

        senderParty.message(ChatColor.GREEN + "Joined the tournament.");
        instance.getTournament().addParty(senderParty);
    }

    @Command(name = "status", desc = "View status of the current tournament")
    public void tournamentStatus(@Sender CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no ongoing tournament to get the status of.");
            return;
        }

        sender.sendMessage(PotPvPLang.LONG_LINE);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Live &cTournament &7Fights"));
        sender.sendMessage("");
        List<Match> ongoingMatches = instance.getTournament().getMatches().stream().filter(m -> m.getState() != MatchState.TERMINATED).collect(Collectors.toList());

        for (Match match : ongoingMatches) {
            MatchTeam firstTeam = match.getTeams().get(0);
            MatchTeam secondTeam = match.getTeams().get(1);

            if (firstTeam.getAllMembers().size() == 1) {
                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.LIGHT_PURPLE + PotPvPRP.getInstance().uuidCache.name(firstTeam.getFirstMember()) + ChatColor.GRAY + " vs " + ChatColor.LIGHT_PURPLE + PotPvPRP.getInstance().uuidCache.name(secondTeam.getFirstMember()));
            } else {
                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.LIGHT_PURPLE + PotPvPRP.getInstance().uuidCache.name(firstTeam.getFirstMember()) + ChatColor.GRAY + "'s team vs " + ChatColor.LIGHT_PURPLE + PotPvPRP.getInstance().uuidCache.name(secondTeam.getFirstMember()) + ChatColor.GRAY + "'s team");
            }
        }
        sender.sendMessage(PotPvPLang.LONG_LINE);
    }

    @Command(name = "cancel", desc = "Cancel the on-going tournament")
    @Require("potpvp.tournament.cancel")
    public void tournamentCancel(@Sender CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to cancel.");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7The &c&ltournament&7 was &ccancelled."));
        Bukkit.broadcastMessage("");
        instance.setTournament(null);
    }

    @Command(name = "forcestart", desc = "Force start the on-going tournament")
    @Require("potpvp.tournament.forcestart")
    public void tournamentForceStart(@Sender CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no tournament to force start.");
            return;
        }

        if (instance.getTournament().getCurrentRound() != -1 || instance.getTournament().getBeginNextRoundIn() != 31) {
            sender.sendMessage(ChatColor.RED + "This tournament is already in progress.");
            return;
        }

        instance.getTournament().start();
        sender.sendMessage(ChatColor.GREEN + "Force started tournament.");
    }
    
    @Override
    public String getCommandName() {
        return "tournament";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"t"};
    }
}
