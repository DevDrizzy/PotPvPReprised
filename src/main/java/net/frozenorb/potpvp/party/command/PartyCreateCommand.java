package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyCreateCommand {

    @Command(names = {"party create", "p create", "t create", "team create", "f create"}, permission = "")
    public static void partyCreate(Player sender) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party.");
            return;
        }

        partyHandler.getOrCreateParty(sender);
        sender.sendMessage(ChatColor.YELLOW + "Created a new party.");
    }

}