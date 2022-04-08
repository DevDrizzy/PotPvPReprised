package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.command.Command;

import org.bukkit.entity.Player;

public final class PartyLeaveCommand {

    @Command(names = {"party leave", "p leave", "t leave", "team leave", "leave", "f leave"}, permission = "")
    public static void partyLeave(Player sender) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else {
            party.leave(sender);
        }
    }

}