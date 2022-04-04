package net.frozenorb.potpvp.party;

import com.google.common.collect.ImmutableSet;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.party.listener.PartyChatListener;
import net.frozenorb.potpvp.party.listener.PartyItemListener;
import net.frozenorb.potpvp.party.listener.PartyLeaveListener;
import net.frozenorb.potpvp.util.InventoryUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles accessing and storage of {@link Party} data
 */
public final class PartyHandler {

    /**
     * Number of seconds it takes for a {@link PartyInvite}
     * to expire after being sent.
     */
    static final int INVITE_EXPIRATION_SECONDS = 30;

    private final Set<Party> parties = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, Party> playerPartyCache = new ConcurrentHashMap<>();

    public PartyHandler() {
        Bukkit.getPluginManager().registerEvents(new PartyChatListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new PartyItemListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new PartyLeaveListener(), PotPvPSI.getInstance());
    }

    /**
     * Finds all parties with at least one member
     * @return immutable set of all existing parties
     */
    public Set<Party> getParties() {
        return ImmutableSet.copyOf(parties);
    }

    /**
     * Checks if the player provided is in a party ({@link Party#isMember(UUID) would return true}
     * @param player the player to check
     * @return if the player provided is in a party
     */
    public boolean hasParty(Player player) {
        return playerPartyCache.containsKey(player.getUniqueId());
    }

    /**
     * Looks up a player's party (a player's party is considered a party
     * for which {@link Party#isMember(UUID)} returns true)
     * @param player the player to lookup
     * @return the player's party, or null if the player is not in a party.
     */
    public Party getParty(Player player) {
        return playerPartyCache.get(player.getUniqueId());
    }

    public Party getParty(UUID uuid) {
        return playerPartyCache.get(uuid);
    }

    /**
     * Looks up a player's party, or creates a Party (with the player as the leader)
     * if none exists.
     * @param player the player to lookup
     * @return the player's existing party, or a new Party
     *          if the player was not in a party
     */
    public Party getOrCreateParty(Player player) {
        Party party = getParty(player);

        if (party == null) {
            party = new Party(player.getUniqueId());
            parties.add(party);
            InventoryUtils.resetInventoryDelayed(player);
        }

        return party;
    }

    void unregisterParty(Party party) {
        parties.remove(party);
    }

    public void updatePartyCache(UUID playerUuid, Party party) {
        if (party != null) {
            playerPartyCache.put(playerUuid, party);
        } else {
            playerPartyCache.remove(playerUuid);
        }
    }

}