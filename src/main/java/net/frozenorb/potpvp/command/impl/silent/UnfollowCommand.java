package net.frozenorb.potpvp.command.impl.silent;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.profile.follow.FollowHandler;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class UnfollowCommand implements PotPvPCommand {

    @Command(name = "", desc = "Unfollow the target you are currently following")
    @Require("potpvp.staff.follow")
    public void unfollow(@Sender Player sender) {
        FollowHandler followHandler = PotPvPRP.getInstance().getFollowHandler();
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        if (!followHandler.getFollowing(sender).isPresent()) {
            sender.sendMessage(ChatColor.RED + "You're not following anybody.");
            return;
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating != null) {
            spectating.removeSpectator(sender);
        }

        followHandler.stopFollowing(sender);
    }

    @Override
    public String getCommandName() {
        return "unfollow";
    }

    @Override
    public String[] getAliases() {
        return new String[]{};
    }
}