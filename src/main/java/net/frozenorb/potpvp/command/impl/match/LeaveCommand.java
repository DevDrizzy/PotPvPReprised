package net.frozenorb.potpvp.command.impl.match;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class LeaveCommand {

    @Command(names = { "spawn", "leave" }, permission = "")
    public static void leave(Player sender) {
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        if (matchHandler.isPlayingMatch(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot do this while playing in a match.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Teleporting you to spawn...");

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating == null) {
            PotPvPRP.getInstance().getLobbyHandler().returnToLobby(sender);
        } else {
            spectating.removeSpectator(sender);
        }
    }

}