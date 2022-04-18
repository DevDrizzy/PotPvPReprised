package net.frozenorb.potpvp.command.impl.misc;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.match.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.match.postmatchinv.PostMatchPlayer;
import net.frozenorb.potpvp.match.postmatchinv.menu.PostMatchMenu;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Sender;

import java.util.Map;
import java.util.UUID;

public class CheckPostMatchInvCommand implements PotPvPCommand {

    @Command(name = "" , usage = "<target-uuid>", desc = "View inventory menu")
    public void checkPostMatchInv(@Sender Player sender, UUID target) {
        PostMatchInvHandler postMatchInvHandler = PotPvPRP.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(sender.getUniqueId());

        if (players.containsKey(target)) {
            new PostMatchMenu(players.get(target)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Data for " + PotPvPRP.getInstance().getUuidCache().name(target) + " not found.");
        }
    }

    @Override
    public String getCommandName() {
        return "checkPostMatchInv";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"_"};
    }
}