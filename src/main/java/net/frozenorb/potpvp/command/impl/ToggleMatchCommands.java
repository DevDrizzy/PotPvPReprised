package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class ToggleMatchCommands implements PotPvPCommand {

    @Command(name = "toggleMatches unranked", desc = "Toggle unranked matches")
    @Require("potpvp.togglematch.admin")
    public void toggleMatchesUnranked(@Sender Player sender) {
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        boolean newState = !matchHandler.isUnrankedMatchesDisabled();
        matchHandler.setUnrankedMatchesDisabled(newState);

        sender.sendMessage(ChatColor.YELLOW + "Unranked matches are now " + ChatColor.UNDERLINE + (newState ? "disabled" : "enabled") + ChatColor.YELLOW + ".");
    }

    @Command(name = "ranked", desc = "Toggle ranked matches")
    @Require("potpvp.togglematch.admin")
    public void toggleMatchesRanked(@Sender Player sender) {
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        boolean newState = !matchHandler.isRankedMatchesDisabled();
        matchHandler.setRankedMatchesDisabled(newState);

        sender.sendMessage(ChatColor.YELLOW + "Ranked matches are now " + ChatColor.UNDERLINE + (newState ? "disabled" : "enabled") + ChatColor.YELLOW + ".");
    }

    @Override
    public String getCommandName() {
        return "toggleMatches";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}