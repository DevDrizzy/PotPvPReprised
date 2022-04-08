package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class KitWipeKitsCommands {

    @Command(names = "kit wipeKits Type", permission = "op")
    public static void kitWipeKitsType(Player sender, @Parameter(name="kit type") KitType kitType) {
        int modified = PotPvPRP.getInstance().getKitHandler().wipeKitsWithType(kitType);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + modified + " " + kitType.getDisplayName() + " kits.");
        sender.sendMessage(ChatColor.GRAY + "^ We would have a proper count here if we ran recent versions of MongoDB");
    }

    @Command(names = "kit wipeKits Player", permission = "op")
    public static void kitWipeKitsPlayer(Player sender, @Parameter(name="target") UUID target) {
        PotPvPRP.getInstance().getKitHandler().wipeKitsForPlayer(target);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + PotPvPRP.getInstance().getUuidCache().name(target) + "'s kits.");
    }

}