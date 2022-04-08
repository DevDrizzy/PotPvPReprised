package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.command.Command;

import org.bukkit.entity.Player;

public final class PartyDisbandCommand {

    @Command(names = {"party disband", "p disband", "t disband", "team disband", "f disband"}, permission = "")
    public static void partyDisband(Player sender) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            return;
        }

        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
            return;
        }

        party.disband();
    }

}