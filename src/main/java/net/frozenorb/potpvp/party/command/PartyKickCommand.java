package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyKickCommand {

    @Command(names = {"party kick", "p kick", "t kick", "team kick", "f kick"}, permission = "")
    public static void partyKick(Player sender, @Parameter(name = "player") Player target) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot kick yourself.");
        } else if (!party.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't in your party.");
        } else {
            party.kick(target);
        }
    }

}