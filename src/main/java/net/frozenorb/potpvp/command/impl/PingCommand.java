package net.frozenorb.potpvp.command.impl;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.api.annotation.Command;
import xyz.refinedev.api.annotation.OptArg;

import java.util.UUID;

public class PingCommand implements PotPvPCommand {

    @Command(name = "", desc = "In-depth ping command")
    public void ping(Player sender, @OptArg() Player target) {
        int ping = PlayerUtils.getPing(target == null ? sender : target);

        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(sender);
        if (match != null && target == null) {
            for (MatchTeam team : match.getTeams()) {
                for (UUID other : team.getAllMembers()) {
                    Player otherPlayer = Bukkit.getPlayer(other);

                    if (otherPlayer != null && !otherPlayer.equals(sender)) {
                        int otherPing = PlayerUtils.getPing(otherPlayer);
                        sender.sendMessage(otherPlayer.getDisplayName() + ChatColor.YELLOW + "'s Ping: " + ChatColor.GREEN + otherPing + "ms");
                    }
                }
            }
        } else if (target == null) {
            sender.sendMessage(sender.getDisplayName() + ChatColor.YELLOW + "'s Ping: " + ChatColor.GREEN + ping + "ms");
        } else {
            sender.sendMessage(target.getDisplayName() + ChatColor.YELLOW + "'s Ping: " + ChatColor.GREEN + ping + "ms");
        }
    }

    @Override
    public String getCommandName() {
        return "ping";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}
