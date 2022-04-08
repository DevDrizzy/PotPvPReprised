package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class PartyFfaCommand {

    @Command(names = {"party ffa", "p ffa", "t ffa", "team ffa", "f ffa"}, permission = "")
    public static void partyFfa(Player sender) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

            if (!PotPvPValidation.canStartFfa(party, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                if (!PotPvPValidation.canStartFfa(party, sender)) {
                    return;
                }

                List<MatchTeam> teams = new ArrayList<>();

                for (UUID member : party.getMembers()) {
                    teams.add(new MatchTeam(member));
                }

                matchHandler.startMatch(teams, kitType, false, false);
            }, "Start a Party FFA...").openMenu(sender);
        }
    }

    @Command(names = {"party devffa", "p devffa", "t devffa", "team devffa", "f devffa"}, permission = "")
    public static void partyDevFfa(Player sender, @Parameter(name = "team size", defaultValue = "1") int teamSize) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

            if (!PotPvPValidation.canStartFfa(party, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                if (!PotPvPValidation.canStartFfa(party, sender)) {
                    return;
                }

                List<UUID> availableMembers = new ArrayList<>(party.getMembers());
                Collections.shuffle(availableMembers);
                
                List<MatchTeam> teams = new ArrayList<>();

                while (availableMembers.size() >= teamSize) {
                    List<UUID> teamMembers = new ArrayList<>();

                    for (int i = 0; i < teamSize; i++) {
                        teamMembers.add(availableMembers.remove(0));
                    }

                    teams.add(new MatchTeam(teamMembers));
                }

                Match match = matchHandler.startMatch(teams, kitType, false, false);

                if (match != null) {
                    for (UUID leftOut : availableMembers) {
                        match.addSpectator(Bukkit.getPlayer(leftOut), null);
                    }
                }
            }, "Start Dev Party FFA...").openMenu(sender);
        }
    }

}