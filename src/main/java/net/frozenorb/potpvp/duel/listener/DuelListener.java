package net.frozenorb.potpvp.duel.listener;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.event.PartyDisbandEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public final class DuelListener implements Listener {

    @EventHandler
    public void onMatchSpectatorJoin(MatchSpectatorJoinEvent event) {
        DuelHandler duelHandler = PotPvPRP.getInstance().getDuelHandler();
        Player player = event.getSpectator();

        duelHandler.removeInvitesTo(player);
        duelHandler.removeInvitesFrom(player);
    }

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        DuelHandler duelHandler = PotPvPRP.getInstance().getDuelHandler();
        Party party = event.getParty();

        duelHandler.removeInvitesTo(party);
        duelHandler.removeInvitesFrom(party);
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        DuelHandler duelHandler = PotPvPRP.getInstance().getDuelHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                duelHandler.removeInvitesTo(memberPlayer);
                duelHandler.removeInvitesFrom(memberPlayer);
            }
        }
    }

}