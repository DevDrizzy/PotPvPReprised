package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.api.annotation.Command;
import xyz.refinedev.api.annotation.Require;

public class MatchCommands implements PotPvPCommand {

    @Command(name = "list", desc = "List matches")
    @Require("potpvp.matchlist")
    public void matchList(Player sender) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        for (Match match : matchHandler.getHostedMatches()) {
            sender.sendMessage(ChatColor.RED + match.getSimpleDescription(true));
        }
    }

    @Command(name = "status", usage = "<target>", desc = "Match status")
    @Require("potpvp.matchstatus")
    public void matchStatus(CommandSender sender, Player target) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(target);

        if (match == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not playing in or spectating a match.");
            return;
        }

        for (String line : PotPvPSI.getGson().toJson(match).split("\n")) {
            sender.sendMessage("  " + ChatColor.GRAY + line);
        }
    }

    @Override
    public String getCommandName() {
        return "match";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}