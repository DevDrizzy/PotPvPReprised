package net.frozenorb.potpvp.command.impl.silent;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.profile.follow.FollowHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.profile.setting.Setting;
import net.frozenorb.potpvp.profile.setting.SettingHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

public class FollowCommand implements PotPvPCommand {

    @Command(name = "", usage = "<target>", desc = "Follow a target as a staff member")
    @Require("potpvp.staff.follow")
    public void follow(@Sender Player sender, Player target) {
        if (!PotPvPValidation.canFollowSomeone(sender)) {
            return;
        }

        FollowHandler followHandler = PotPvPRP.getInstance().getFollowHandler();
        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "No, you can't follow yourself.");
            return;
        } else if (!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        Game game = GameQueue.INSTANCE.getCurrentGame(target);
        if (game != null && game.getPlayers().contains(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is playing an event!");
            return;
        }

        followHandler.getFollowing(sender).ifPresent(fo -> new UnfollowCommand().unfollow(sender));

        if (matchHandler.isSpectatingMatch(sender)) {
            matchHandler.getMatchSpectating(sender).removeSpectator(sender);
        }

        followHandler.startFollowing(sender, target);
    }

    @Override
    public String getCommandName() {
        return "follow";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}