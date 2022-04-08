package net.frozenorb.potpvp.duel;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.duel.listener.DuelListener;
import net.frozenorb.potpvp.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DuelHandler {

    public static final int DUEL_INVITE_TIMEOUT_SECONDS = 30;

    // this does mean lookups are O(n), but unlike matches or parties
    // there are isn't enough volume + frequency to become an issue
    private Set<DuelInvite> activeInvites = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DuelHandler() {
        Bukkit.getPluginManager().registerEvents(new DuelListener(), PotPvPRP.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(PotPvPRP.getInstance(), () -> activeInvites.removeIf(DuelInvite::isExpired), 20, 20);
    }

    public void insertInvite(DuelInvite invite) {
        activeInvites.add(invite);
    }

    public void removeInvite(DuelInvite invite) {
        activeInvites.remove(invite);
    }

    public void removeInvitesTo(Player player) {
        activeInvites.removeIf(i ->
            i instanceof PlayerDuelInvite &&
            ((PlayerDuelInvite) i).getTarget().equals(player.getUniqueId())
        );
    }

    public void removeInvitesFrom(Player player) {
        activeInvites.removeIf(i ->
            i instanceof PlayerDuelInvite &&
            ((PlayerDuelInvite) i).getSender().equals(player.getUniqueId())
        );
    }

    public void removeInvitesTo(Party party) {
        activeInvites.removeIf(i ->
            i instanceof PartyDuelInvite &&
            ((PartyDuelInvite) i).getTarget() == party
        );
    }

    public void removeInvitesFrom(Party party) {
        activeInvites.removeIf(i ->
            i instanceof PartyDuelInvite &&
            ((PartyDuelInvite) i).getSender() == party
        );
    }

    public PartyDuelInvite findInvite(Party sender, Party target) {
        for (DuelInvite invite : activeInvites) {
            if (invite instanceof PartyDuelInvite) {
                PartyDuelInvite partyInvite = (PartyDuelInvite) invite;

                if (partyInvite.getSender() == sender && partyInvite.getTarget() == target) {
                    return partyInvite;
                }
            }
        }

        return null;
    }

    public PlayerDuelInvite findInvite(Player sender, Player target) {
        for (DuelInvite invite : activeInvites) {
            if (invite instanceof PlayerDuelInvite) {
                PlayerDuelInvite playerInvite = (PlayerDuelInvite) invite;

                if (playerInvite.getSender().equals(sender.getUniqueId()) && playerInvite.getTarget().equals(target.getUniqueId())) {
                    return playerInvite;
                }
            }
        }

        return null;
    }

}