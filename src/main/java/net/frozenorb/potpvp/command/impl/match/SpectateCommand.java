package net.frozenorb.potpvp.command.impl.match;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.potpvp.command.Command;
import net.frozenorb.potpvp.command.param.Parameter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class SpectateCommand {

    private static final int SPECTATE_COOLDOWN_SECONDS = 2;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    @Command(names = {"spectate", "spec"}, permission = "")
    public static void spectate(Player sender, @Parameter(name = "target") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot spectate yourself.");
            return;
        } else if (cooldowns.containsKey(sender.getUniqueId()) && cooldowns.get(sender.getUniqueId()) > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.RED + "Please wait before using this command again.");
            return;
        }

        cooldowns.put(sender.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SPECTATE_COOLDOWN_SECONDS));

        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
        SettingHandler settingHandler = PotPvPRP.getInstance().getSettingHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a match.");
            return;
        }

        //boolean bypassesSpectating = PotPvPSI.getInstance().getTournamentHandler().isInTournament(targetMatch);
        boolean bypassesSpectating = false;

        // only check the seting if the target is actually playing in the match
        if (!bypassesSpectating && (targetMatch.getTeam(target.getUniqueId()) != null && !settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS))) {
            if (sender.isOp() || sender.hasPermission("basic.staff")) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        if ((!sender.isOp() && !sender.hasPermission("basic.staff")) && targetMatch.getTeams().size() == 2 && !bypassesSpectating) {
            MatchTeam teamA = targetMatch.getTeams().get(0);
            MatchTeam teamB = targetMatch.getTeams().get(1);

            if (teamA.getAllMembers().size() == 1 && teamB.getAllMembers().size() == 1) {
                UUID teamAPlayer = teamA.getFirstMember();
                UUID teamBPlayer = teamB.getFirstMember();

                if (
                    !settingHandler.getSetting(Bukkit.getPlayer(teamAPlayer), Setting.ALLOW_SPECTATORS) ||
                    !settingHandler.getSetting(Bukkit.getPlayer(teamBPlayer), Setting.ALLOW_SPECTATORS)
                ) {
                    sender.sendMessage(ChatColor.RED + "Not all players in that 1v1 have spectators enabled.");
                    return;
                }
            }
        }

        Player teleportTo = null;

        // /spectate looks up matches being played OR watched by the target,
        // so we can only target them if they're not spectating
        if (!targetMatch.isSpectator(target.getUniqueId())) {
            teleportTo = target;
        }

        if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(sender)) {
            Match currentlySpectating = matchHandler.getMatchSpectating(sender);

            if (currentlySpectating != null) {
                if (currentlySpectating.equals(targetMatch)) {
                    sender.sendMessage(ChatColor.RED + "You're already spectating this match.");
                    return;
                }

                currentlySpectating.removeSpectator(sender);
            }

            targetMatch.addSpectator(sender, teleportTo);
        }
    }

}