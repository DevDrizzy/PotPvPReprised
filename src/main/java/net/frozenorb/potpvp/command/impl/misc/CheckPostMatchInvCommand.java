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
    public void checkPostMatchInv(@Sender Player sender, String target) {

        UUID uuid;
        try {
            uuid = UUID.fromString(target);
        } catch (Exception e) {
            UUID cached = PotPvPRP.getInstance().getUuidCache().uuid(target);
            if (cached == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUUID does not exist!"));
                return;
            }
            uuid = cached;
        }

        PostMatchInvHandler postMatchInvHandler = PotPvPRP.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(sender.getUniqueId());

        if (players.containsKey(uuid)) {
            new PostMatchMenu(players.get(uuid)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Data for " + PotPvPRP.getInstance().getUuidCache().name(uuid) + " not found.");
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