package net.frozenorb.potpvp.command.impl;

import java.util.UUID;

import net.frozenorb.potpvp.command.PotPvPCommand;
import net.frozenorb.potpvp.kt.menu.menus.ConfirmMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Objects;

import net.frozenorb.potpvp.PotPvPSI;
import xyz.refinedev.api.annotation.Command;

public class StatsResetCommands implements PotPvPCommand {

    private static String REDIS_PREFIX = "PotPvP:statsResetToken:";

    @Command(name = "addtoken", usage = "<player> <amount>", desc = "Set a player's token amount")
    public void addToken(CommandSender sender, String playerName, int amount) {
        UUID uuid = PotPvPSI.getInstance().uuidCache.uuid(playerName);

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "Unable to locate '" + playerName + "'.");
            return;
        }

        addTokens(uuid, amount);
        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " token" + (amount == 1 ? "" : "s") + " to " + PotPvPSI.getInstance().getUuidCache().name(uuid) + ".");
    }

    @Command(name = "", desc = "Reset your stats")
    public void reset(Player sender) {
        int tokens = getTokens(sender.getUniqueId());
        if (tokens <= 0) {
            sender.sendMessage(ChatColor.RED + "You need at least one token to reset your stats.");
            return;
        }

        Bukkit.getScheduler().runTask(PotPvPSI.getInstance(), () -> {
            new ConfirmMenu("Stats reset", (reset) -> {
                if (!reset) {
                    sender.sendMessage(ChatColor.RED + "Stats reset aborted.");
                    return null;
                }

                Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
                    PotPvPSI.getInstance().getEloHandler().resetElo(sender.getUniqueId());
                    removeTokens(sender.getUniqueId(), 1);
                    sender.sendMessage(ChatColor.GREEN + "Reset your stats! Used one token. " + tokens + " token" + (tokens == 1 ? "" : "s") + " left.");
                });

                return null;
            }).openMenu(sender);
        });
    }

    private int getTokens(UUID player) {
        return PotPvPSI.getInstance().redis.runBackboneRedisCommand((redis) -> {
            return Integer.valueOf(Objects.firstNonNull(redis.get(REDIS_PREFIX + player.toString()), "0"));
        });
    }

    private void addTokens(UUID player, int amountBy) {
        PotPvPSI.getInstance().redis.runBackboneRedisCommand((redis) -> {
            redis.incrBy(REDIS_PREFIX + player.toString(), amountBy);
            return null;
        });
    }

    public void removeTokens(UUID player, int amountBy) {
        PotPvPSI.getInstance().redis.runBackboneRedisCommand((redis) -> {
            redis.decrBy(REDIS_PREFIX + player.toString(), amountBy);
            return null;
        });
    }

    @Override
    public String getCommandName() {
        return "statsreset";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
