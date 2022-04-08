package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kt.util.TimeUtils;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchEndReason;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.event.MatchStartEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class MatchDurationLimitListener implements Listener {

    private static final int DURATION_LIMIT_SECONDS = (int) TimeUnit.MINUTES.toSeconds(20);
    private static final String TIME_WARNING_MESSAGE = ChatColor.RED + "The match will forcefully end in %s.";
    private static final String TIME_EXCEEDED_MESSAGE = ChatColor.RED.toString() + ChatColor.BOLD + "Match time exceeded %s. Ending match...";

    @EventHandler
    public void onMatchCountdownEnd(MatchStartEvent event) {
        Match match = event.getMatch();

        new BukkitRunnable() {

            int secondsRemaining = DURATION_LIMIT_SECONDS;

            @Override
            public void run() {
                if (match.getState() != MatchState.IN_PROGRESS) {
                    cancel();
                    return;
                }

                // Very ugly to do it here, but I don't want to put another runnable per match
                if (match.getKitType().getId().equals("SUMO")) {
                    match.getTeams().forEach(t -> t.getAllMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                        p.setHealth(20);
                        p.setFoodLevel(20);
                        p.setSaturation(20);
                    }));
                }

                switch (secondsRemaining) {
                    case 120:
                    case 60:
                    case 30:
                    case 15:
                    case 10:
                    case 5:
                        match.messageAll(String.format(TIME_WARNING_MESSAGE, TimeUtils.formatIntoDetailedString(secondsRemaining)));
                        break;
                    case 0:
                        match.messageAll(String.format(TIME_EXCEEDED_MESSAGE, TimeUtils.formatIntoDetailedString(DURATION_LIMIT_SECONDS)));
                        match.endMatch(MatchEndReason.DURATION_LIMIT_EXCEEDED);
                        break;
                    default:
                        break;
                }

                secondsRemaining--;
            }

        }.runTaskTimer(PotPvPRP.getInstance(), 20L, 20L);
    }

}