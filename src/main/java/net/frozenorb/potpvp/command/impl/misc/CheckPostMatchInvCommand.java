package net.frozenorb.potpvp.command.impl.misc;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.match.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.match.postmatchinv.PostMatchPlayer;
import net.frozenorb.potpvp.match.postmatchinv.menu.PostMatchMenu;

import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.Sender;

import java.util.Map;
import java.util.UUID;

public class CheckPostMatchInvCommand implements PotPvPCommand {

    @Command(name = "" , usage = "<target>", desc = "View inventory menu")
    public void checkPostMatchInv(@Sender Player sender, UUID target) {
        PostMatchInvHandler postMatchInvHandler = PotPvPRP.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(sender.getUniqueId());
        PostMatchPlayer inv = players.get(target);

        if (inv == null) {
            String name = PotPvPRP.getInstance().getUuidCache().name(target);
            sender.sendMessage(ChatColor.RED + "Data for " + name + " not found.");
        }

        Menu menu = new PostMatchMenu(players.get(target));
        menu.openMenu(sender);

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