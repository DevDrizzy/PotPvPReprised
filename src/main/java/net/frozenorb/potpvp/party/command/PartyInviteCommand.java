package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PartyInviteCommand {

    @Command(names = {"party invite", "p invite", "t invite", "team invite", "invite", "inv", "party inv", "p inv", "t inv", "team invite", "f invite", "f inv"}, permission = "")
    public static void partyInvite(Player sender, @Parameter(name = "player") Player target) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot invite yourself to your own party.");
            return;
        }

        if (sender.hasMetadata("ModMode")) {
            sender.sendMessage(ChatColor.RED + "You cannot do this while in silent mode!");
            return;
        }

        if (party != null) {
            if (party.isMember(target.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + target.getName() + " is already in your party.");
                return;
            }

            if (party.getInvite(target.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + target.getName() + " already has a pending party invite.");
                return;
            }
        }

        if (partyHandler.hasParty(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already in another party.");
            return;
        }

        // only create party if validations succeed
        party = partyHandler.getOrCreateParty(sender);

        if (party.getMembers().size() >= Party.MAX_SIZE && !sender.isOp()) { // I got the permission from "/party invite **" below
            sender.sendMessage(ChatColor.RED + "Your party has reached the " + Party.MAX_SIZE + " player limit.");
            return;
        }

        if (party.isLeader(sender.getUniqueId())) {
            party.invite(target);
        } else {
            askLeaderToInvite(party, sender, target);
        }
    }

    @Command(names = {"party invite **", "p invite **", "t invite **", "team invite **", "invite **", "inv **", "party inv **", "p inv **", "t inv **", "team invite **", "f invite **", "f inv **"}, permission = "op")
    public static void partyInviteAll(Player sender) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        Party party = partyHandler.getOrCreateParty(sender);

        if (sender.hasMetadata("ModMode")) {
            sender.sendMessage(ChatColor.RED + "You cannot do this while in silent mode!");
            return;
        }

        int sent = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUuid = player.getUniqueId();
            boolean isMember = party.isMember(playerUuid);
            boolean hasInvite = party.getInvite(playerUuid) != null;

            if (!isMember && !hasInvite) {
                party.invite(player);
                sent++;
            }
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.YELLOW + "No invites to send.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Sent " + sent + " invite" + (sent == 1 ? "" : "s") + ".");
        }
    }

    private static void askLeaderToInvite(Party party, Player requester, Player target) {
        requester.sendMessage(ChatColor.YELLOW + "You have requested to invite " + target.getDisplayName() + ChatColor.YELLOW + ".");

        Player leader = Bukkit.getPlayer(party.getLeader());

        // should never happen
        if (leader == null) {
            return;
        }

        leader.sendMessage(requester.getDisplayName() + ChatColor.YELLOW + " wants you to invite " + target.getDisplayName() + ChatColor.YELLOW + ".");
        leader.spigot().sendMessage(createInviteButton(target));
    }

    private static TextComponent createInviteButton(Player target) {
        BaseComponent[] hoverTooltip = { new TextComponent(ChatColor.GREEN + "Click to invite") };
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;

        TextComponent inviteButton = new TextComponent("Click here to send the invitation");

        inviteButton.setColor(ChatColor.AQUA);
        inviteButton.setHoverEvent(new HoverEvent(showText, hoverTooltip));
        inviteButton.setClickEvent(new ClickEvent(runCommand, "/invite " + target.getName()));

        return inviteButton;
    }

}