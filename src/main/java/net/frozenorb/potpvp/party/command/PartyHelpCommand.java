package net.frozenorb.potpvp.party.command;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public final class PartyHelpCommand {

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

    @Command(names = {"party", "p", "t", "team", "f", "party help", "p help", "t help", "team help", "f help"}, permission = "")
    public static void party(Player sender) {
        HELP_MESSAGE.forEach(sender::sendMessage);
    }

}