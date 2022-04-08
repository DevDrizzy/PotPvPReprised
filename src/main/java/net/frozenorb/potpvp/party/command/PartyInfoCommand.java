package net.frozenorb.potpvp.party.command;

import com.google.common.base.Joiner;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;

public final class PartyInfoCommand {

    @Command(names = {"party info", "p info", "t info", "team info", "f info", "p i", "t i", "f i", "party i", "team i"}, permission = "")
    public static void partyInfo(Player sender, @Parameter(name = "player", defaultValue = "self") Player target) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(target);

        if (party == null) {
            if (sender == target) {
                sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " isn't in a party.");
            }

            return;
        }

        String leaderName = PotPvPRP.getInstance().getUuidCache().name(party.getLeader());
        int memberCount = party.getMembers().size();
        String members = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(party.getMembers()));

        sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
        sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.GOLD + leaderName);
        sender.sendMessage(ChatColor.YELLOW + "Members " + ChatColor.GOLD + "(" + memberCount + ")" + ChatColor.YELLOW + ": " + ChatColor.GRAY + members);

        switch (party.getAccessRestriction()) {
            case PUBLIC:
                sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.GREEN + "Open");
                break;
            case INVITE_ONLY:
                sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.GOLD + "Invite-Only");
                break;
            case PASSWORD:
                // leader can see password by hovering
                if (party.isLeader(sender.getUniqueId())) {
                    HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
                    BaseComponent[] passwordComponent = { new TextComponent(party.getPassword()) };

                    // Privacy: Password Protected [Hover for password]
                    ComponentBuilder builder = new ComponentBuilder("Privacy: ").color(ChatColor.YELLOW);
                    builder.append("Password Protected ").color(ChatColor.RED);
                    builder.append("[Hover for password]").color(ChatColor.GRAY);
                    builder.event(new HoverEvent(showText, passwordComponent));

                    sender.spigot().sendMessage(builder.create());
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.RED + "Password Protected");
                }

                break;
            default:
                break;
        }

        sender.sendMessage(ChatColor.GRAY + PotPvPLang.LONG_LINE);
    }

}