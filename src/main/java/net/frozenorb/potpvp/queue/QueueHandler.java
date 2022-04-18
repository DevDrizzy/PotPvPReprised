package net.frozenorb.potpvp.queue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.listener.QueueGeneralListener;
import net.frozenorb.potpvp.queue.listener.QueueItemListener;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.validation.PotPvPValidation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

public final class QueueHandler {

    public static final int RANKED_WINDOW_GROWTH_PER_SECOND = 5;

    private static final String JOIN_SOLO_MESSAGE = ChatColor.GREEN + "You are now queued for %s %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_SOLO_MESSAGE = ChatColor.GREEN + "You are no longer queued for %s %s" + ChatColor.GREEN + ".";
    private static final String JOIN_PARTY_MESSAGE = ChatColor.GREEN + "Your party is now queued for %s %s" + ChatColor.GREEN + ".";
    private static final String LEAVE_PARTY_MESSAGE = ChatColor.GREEN + "Your party is no longer queued for %s %s" + ChatColor.GREEN + ".";

    // we never call .put outside of the constructor so no concurrency is needed
    // (KitType type, boolean ranked) -> MatchQueue
    private final Table<KitType, Boolean, MatchQueue> soloQueues = HashBasedTable.create();
    private final Table<KitType, Boolean, MatchQueue> partyQueues = HashBasedTable.create();

    // maps players (and parties) to their entry for fast O(1) lookup
    private final Map<UUID, SoloMatchQueueEntry> soloQueueCache = new ConcurrentHashMap<>();
    private final Map<Party, PartyMatchQueueEntry> partyQueueCache = new ConcurrentHashMap<>();

    // because this is called very often (it's on the lobby scoreboard)
    // we cache every second (per KitType counts aren't cached, however)
    @Getter private int queuedCount = 0;

    public QueueHandler() {
        Bukkit.getPluginManager().registerEvents(new QueueGeneralListener(this), PotPvPRP.getInstance());
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(this), PotPvPRP.getInstance());

        for (KitType kitType : KitType.getAllTypes()) {
            soloQueues.put(kitType, true, new MatchQueue(kitType, true));
            soloQueues.put(kitType, false, new MatchQueue(kitType, false));

            partyQueues.put(kitType, true, new MatchQueue(kitType, true));
            partyQueues.put(kitType, false, new MatchQueue(kitType, false));
        }

        Bukkit.getScheduler().runTaskTimer(PotPvPRP.getInstance(), () -> {
            soloQueues.values().forEach(MatchQueue::tick);
            partyQueues.values().forEach(MatchQueue::tick);

            int i = 0;

            for (MatchQueue queue : soloQueues.values()) {
                i += queue.countPlayersQueued();
            }

            for (MatchQueue queue : partyQueues.values()) {
                i += queue.countPlayersQueued();
            }

            queuedCount = i;
        }, 20L, 20L);
    }

    public void addQueues(KitType kitType) {
        soloQueues.put(kitType, true, new MatchQueue(kitType, true));
        soloQueues.put(kitType, false, new MatchQueue(kitType, false));

        partyQueues.put(kitType, true, new MatchQueue(kitType, true));
        partyQueues.put(kitType, false, new MatchQueue(kitType, false));
    }

    public void removeQueues(KitType kitType) {
        soloQueues.remove(kitType, true);
        soloQueues.remove(kitType, false);

        partyQueues.remove(kitType, true);
        partyQueues.remove(kitType, false);
    }

    public int countPlayersQueued(KitType kitType, boolean ranked) {
        return soloQueues.get(kitType, ranked).countPlayersQueued() +
               partyQueues.get(kitType, ranked).countPlayersQueued();
    }

    public boolean joinQueue(Player player, KitType kitType, boolean ranked) {
        if (!PotPvPValidation.canJoinQueue(player)) {
            return false;
        }

        MatchQueue queue = soloQueues.get(kitType, ranked);
        SoloMatchQueueEntry entry = new SoloMatchQueueEntry(queue, player.getUniqueId());

        queue.addToQueue(entry);
        soloQueueCache.put(player.getUniqueId(), entry);

        player.sendMessage(String.format(JOIN_SOLO_MESSAGE, ranked ? "Ranked" : "Unranked", kitType.getColoredDisplayName()));
        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean leaveQueue(Player player, boolean silent) {
        MatchQueueEntry entry = getQueueEntry(player.getUniqueId());

        if (entry == null) {
            return false;
        }

        MatchQueue queue = entry.getQueue();

        queue.removeFromQueue(entry);
        soloQueueCache.remove(player.getUniqueId());

        if (!silent) {
            player.sendMessage(String.format(LEAVE_SOLO_MESSAGE, queue.isRanked() ? "Ranked" : "Unranked", queue.getKitType().getColoredDisplayName()));
        }

        InventoryUtils.resetInventoryDelayed(player);
        return true;
    }

    public boolean joinQueue(Party party, KitType kitType, boolean ranked) {
        if (!PotPvPValidation.canJoinQueue(party)) {
            return false;
        }

        MatchQueue queue = partyQueues.get(kitType, ranked);
        PartyMatchQueueEntry entry = new PartyMatchQueueEntry(queue, party);

        queue.addToQueue(entry);
        partyQueueCache.put(party, entry);

        party.message(String.format(JOIN_PARTY_MESSAGE, ranked ? "Ranked" : "Unranked", kitType.getColoredDisplayName()));
        party.resetInventoriesDelayed();
        return true;
    }

    public boolean leaveQueue(Party party, boolean silent) {
        MatchQueueEntry entry = getQueueEntry(party);

        if (entry == null) {
            return false;
        }

        MatchQueue queue = entry.getQueue();

        queue.removeFromQueue(entry);
        partyQueueCache.remove(party);

        if (!silent) {
            party.message(String.format(LEAVE_PARTY_MESSAGE, queue.isRanked() ? "Ranked" : "Unranked", queue.getKitType().getColoredDisplayName()));
        }

        party.resetInventoriesDelayed();
        return true;
    }

    public boolean isQueued(UUID player) {
        return soloQueueCache.containsKey(player);
    }

    public boolean isQueuedRanked(UUID player) {
        SoloMatchQueueEntry entry = getQueueEntry(player);
        return entry != null && entry.getQueue().isRanked();
    }

    public boolean isQueuedUnranked(UUID player) {
        SoloMatchQueueEntry entry = getQueueEntry(player);
        return entry != null && !entry.getQueue().isRanked();
    }

    public SoloMatchQueueEntry getQueueEntry(UUID player) {
        return soloQueueCache.get(player);
    }

    public boolean isQueued(Party party) {
        return partyQueueCache.containsKey(party);
    }

    public boolean isQueuedRanked(Party party) {
        PartyMatchQueueEntry entry = getQueueEntry(party);
        return entry != null && entry.getQueue().isRanked();
    }

    public boolean isQueuedUnranked(Party party) {
        PartyMatchQueueEntry entry = getQueueEntry(party);
        return entry != null && !entry.getQueue().isRanked();
    }


    public PartyMatchQueueEntry getQueueEntry(Party party) {
        return partyQueueCache.get(party);
    }

    void removeFromQueueCache(MatchQueueEntry entry) {
        if (entry instanceof SoloMatchQueueEntry) {
            soloQueueCache.remove(((SoloMatchQueueEntry) entry).getPlayer());
        } else if (entry instanceof PartyMatchQueueEntry) {
            partyQueueCache.remove(((PartyMatchQueueEntry) entry).getParty());
        }
    }

}