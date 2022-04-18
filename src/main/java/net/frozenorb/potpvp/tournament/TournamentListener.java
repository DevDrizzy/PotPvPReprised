package net.frozenorb.potpvp.tournament;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.util.event.HalfHourEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/4/2022
 * Project: potpvp-reprised
 */

public class TournamentListener implements Listener {

    @EventHandler
    public void onHalfHour(HalfHourEvent event) {
        if (PotPvPRP.getInstance().getTournamentHandler().getTournament() != null) return; // already a tournament in progress

        TournamentHandler.TournamentStatus status = TournamentHandler.TournamentStatus.forPlayerCount(Bukkit.getOnlinePlayers().size());
        if (status == null) return;

        int teamSize = status.getTeamSizes().get(ThreadLocalRandom.current().nextInt(status.getTeamSizes().size()));
        int teamCount = status.getTeamCounts().get(ThreadLocalRandom.current().nextInt(status.getTeamCounts().size()));
        KitType kitType = status.getKitTypes().get(ThreadLocalRandom.current().nextInt(status.getKitTypes().size()));

        Tournament tournament;
        PotPvPRP.getInstance().getTournamentHandler().setTournament(tournament = new Tournament(kitType, teamSize, teamCount));

        Bukkit.getScheduler().runTaskLater(PotPvPRP.getInstance(), () -> {
            if (tournament == PotPvPRP.getInstance().getTournamentHandler().getTournament() && PotPvPRP.getInstance().getTournamentHandler().getTournament() != null && PotPvPRP.getInstance().getTournamentHandler().getTournament().getCurrentRound() == -1) {
                PotPvPRP.getInstance().getTournamentHandler().getTournament().start();
            }
        }, 3 * 20 * 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (PotPvPRP.getInstance().getTournamentHandler().getTournament() == tournament) {
                    tournament.broadcastJoinMessage();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(PotPvPRP.getInstance(), 60 * 20, 60 * 20);
    }
    
}
