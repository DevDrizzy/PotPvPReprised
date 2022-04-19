package net.frozenorb.potpvp.command.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.kit.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.*;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.command.annotation.Command;
import xyz.refinedev.command.annotation.OptArg;
import xyz.refinedev.command.annotation.Require;
import xyz.refinedev.command.annotation.Sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/14/2022
 * Project: potpvp-reprised
 */

public class PartyCommands implements PotPvPCommand {

    // default value for password parameter used to detect that password
    // wasn't provided. No Optional<String> :(
    private static final String NO_PASSWORD_PROVIDED = "skasjkdasdjhksahjd";

    private static final List<String> HELP_MESSAGE = ImmutableList.of(
            ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE,
            "§d§lParty Help §7- §fInformation on how to use party commands",
            ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE,
            "§9Party Commands:",
            "§e/party invite §7- Invite a player to join your party",
            "§e/party leave §7- Leave your current party",
            "§e/party accept [player] §7- Accept party invitation",
            "§e/party info [player] §7- View the roster of the party",
            "",
            "§9Leader Commands:",
            "§e/party kick <player> §7- Kick a player from your party",
            "§e/party leader <player> §7- Transfer party leadership",
            "§e/party disband §7 - Disbands party",
            "§e/party lock §7 - Lock party from others joining",
            "§e/party open §7 - Open party to others joining",
            "§e/party password <password> §7 - Sets party password",
            "",
            "§9Other Help:",
            "§eTo use §dparty chat§e, prefix your message with the §7'§d@§7' §esign.",
            ChatColor.DARK_PURPLE + PotPvPLang.LONG_LINE
    );

    @Command(name = "", aliases = "help", desc = "View party commands")
    public void help(@Sender CommandSender sender) {
        HELP_MESSAGE.forEach(sender::sendMessage);
    }

    @Command(name = "create", desc = "Create a new party")
    public void partyCreate(@Sender Player sender) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party.");
            return;
        }

        partyHandler.getOrCreateParty(sender);
        sender.sendMessage(ChatColor.YELLOW + "Created a new party.");
    }

    @Command(name = "disband", desc = "Disband your party")
    public void partyDisband(@Sender Player sender) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            return;
        }

        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
            return;
        }

        party.disband();
    }

    @Command(name = "ffa", desc = "Start party ffa for your party")
    public void partyFFA(@Sender Player sender) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

            if (!PotPvPValidation.canStartFfa(party, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                if (!PotPvPValidation.canStartFfa(party, sender)) {
                    return;
                }

                List<MatchTeam> teams = new ArrayList<>();

                for ( UUID member : party.getMembers()) {
                    teams.add(new MatchTeam(member));
                }

                matchHandler.startMatch(teams, kitType, false, false);
            }, "Start a Party FFA...").openMenu(sender);
        }
    }

    @Command(name = "devffa", usage = "<teamSize>", desc = "Start a party developer ffa for your party")
    @Require("potpvp.party.devffa")
    public void partyDevFFA(@Sender Player sender, int teamSize) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

            if (!PotPvPValidation.canStartFfa(party, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                if (!PotPvPValidation.canStartFfa(party, sender)) {
                    return;
                }

                List<UUID> availableMembers = new ArrayList<>(party.getMembers());
                Collections.shuffle(availableMembers);

                List<MatchTeam> teams = new ArrayList<>();

                while (availableMembers.size() >= teamSize) {
                    List<UUID> teamMembers = new ArrayList<>();

                    for (int i = 0; i < teamSize; i++) {
                        teamMembers.add(availableMembers.remove(0));
                    }

                    teams.add(new MatchTeam(teamMembers));
                }

                Match match = matchHandler.startMatch(teams, kitType, false, false);

                if (match != null) {
                    for (UUID leftOut : availableMembers) {
                        match.addSpectator(Bukkit.getPlayer(leftOut), null);
                    }
                }
            }, "Start Dev Party FFA...").openMenu(sender);
        }
    }

    @Command(name = "info", usage = "[target]", desc = "View party information of yourself or a target")
    public void partyInfo(@Sender Player sender, @OptArg() Player target) {
        if (target == null) target = sender;
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
                    ComponentBuilder builder = new ComponentBuilder("Privacy: ").color(net.md_5.bungee.api.ChatColor.YELLOW);
                    builder.append("Password Protected ").color(net.md_5.bungee.api.ChatColor.RED);
                    builder.append("[Hover for password]").color(net.md_5.bungee.api.ChatColor.GRAY);
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
    
    @Command(name = "invite", usage = "<target>", desc = "Invite a target to your party")
    public void partyInvite(@Sender Player sender, Player target) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
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
            PartyUtils.askLeaderToInvite(party, sender, target);
        }
    }
    
    @Command(name = "inviteall", aliases = {"invite **", "invite all", ""}, desc = "Invite all players online to your party")
    @Require("potpvp.party.inviteall")
    public void partyInviteALL(@Sender Player sender) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
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

    @Command(name = "join", usage = "<target> [password]", desc = "Join a specified party")
    public void partyJoin(@Sender Player sender, Player target, @OptArg(NO_PASSWORD_PROVIDED) String providedPassword) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        Party targetParty = partyHandler.getParty(target);

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party. You must leave your current party first.");
            return;
        }

        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a party.");
            return;
        }

        PartyInvite invite = targetParty.getInvite(sender.getUniqueId());

        switch (targetParty.getAccessRestriction()) {
            case PUBLIC:
                targetParty.join(sender);
                break;
            case INVITE_ONLY:
                if (invite != null) {
                    targetParty.join(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have an invitation to this party.");
                }

                break;
            case PASSWORD:
                if (providedPassword.equals(NO_PASSWORD_PROVIDED) && invite == null) {
                    sender.sendMessage(ChatColor.RED + "You need the password or an invitation to join this party.");
                    sender.sendMessage(ChatColor.GRAY + "To join with a password, use " + ChatColor.YELLOW + "/party join " + target.getName() + " <password>");
                    return;
                }

                String correctPassword = targetParty.getPassword();

                if (invite == null && !correctPassword.equals(providedPassword)) {
                    sender.sendMessage(ChatColor.RED + "Invalid password.");
                } else {
                    targetParty.join(sender);
                }

                break;
            default:
                break;
        }
    }

    @Command(name = "kick", usage = "<target>", desc = "Kick a specified target from your party")
    public void kick(@Sender Player sender, Player target) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot kick yourself.");
        } else if (!party.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't in your party.");
        } else {
            party.kick(target);
        }
    }

    @Command(name = "leader", aliases = "promote", usage = "<target>", desc = "Promote a specified target from your party")
    public void leader(@Sender Player sender, Player target) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (!party.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't in your party.");
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot promote yourself to the leader of your own party.");
        } else {
            party.setLeader(target);
        }
    }

    @Command(name = "leave", desc = "Leave your current party")
    public void leave(@Sender Player sender) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else {
            party.leave(sender);
        }
    }

    @Command(name = "lock", aliases = "close", desc = "Privatise your party to the public")
    public void lock(@Sender Player sender) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.INVITE_ONLY) {
            sender.sendMessage(ChatColor.RED + "Your party is already locked.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.INVITE_ONLY);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.RED + "locked" + ChatColor.YELLOW + ".");
        }
    }

    @Command(name = "unlock", aliases = "open", desc = "Publicize your party to the public")
    public void unlock(@Sender Player sender) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.PUBLIC) {
            sender.sendMessage(ChatColor.RED + "Your party is already open.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PUBLIC);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.GREEN + "open" + ChatColor.YELLOW + ".");
        }
    }

    @Command(name = "password", aliases = "pass", desc = "Set a password for your private party")
    public void pass(@Sender Player sender, String password) {
        Party party = PotPvPRP.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PASSWORD);
            party.setPassword(password);

            sender.sendMessage(ChatColor.YELLOW + "Your party's password is now " + ChatColor.AQUA + password + ChatColor.YELLOW + ".");
        }
    }

    @Command(name = "teamSplit", aliases = "split", desc = "Start a team split match with your party")
    public void teamSplit(@Sender Player sender) {
        PartyHandler partyHandler = PotPvPRP.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else {
            PartyUtils.startTeamSplit(party, sender);
        }
    }

    @Override
    public String getCommandName() {
        return "party";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"p", "t", "team"};
    }
}
