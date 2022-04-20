package net.frozenorb.potpvp.profile.statistics;

import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.event.MatchTerminateEvent;
import net.frozenorb.potpvp.util.MongoUtils;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

public class StatisticsHandler implements Listener {
    
    private static MongoCollection<Document> COLLECTION;
    private Map<UUID, Map<String, Map<Statistic, Double>>> statisticsMap;
    
    public StatisticsHandler() {
        COLLECTION = MongoUtils.getCollection("playerStatistics");
        statisticsMap = Maps.newConcurrentMap();
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(PotPvPRP.getInstance(), () -> {
            statisticsMap.keySet().forEach(this::saveStatistics);
        }, 30 * 20, 30 * 20);
    }
    
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPRP.getInstance(), () -> {
            loadStatistics(event.getPlayer().getUniqueId());
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPRP.getInstance(), () -> {
            saveStatistics(event.getPlayer().getUniqueId());
            unloadStatistics(event.getPlayer().getUniqueId());
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMatchEnd(MatchTerminateEvent event) {
        Match match = event.getMatch();

        if (match.getKitType().equals(KitType.teamFight)) return;
        
        match.getWinningPlayers().forEach(uuid -> {
            incrementStat(uuid, Statistic.WINS, match.getKitType());
        });
        
        match.getLosingPlayers().forEach(uuid -> {
            incrementStat(uuid, Statistic.LOSSES, match.getKitType());
        });
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player died = event.getEntity();
        Player killer = died.getKiller();
        
        Match diedMatch = PotPvPRP.getInstance().getMatchHandler().getMatchPlayingOrSpectating(died);
        
        if (diedMatch == null) {
            return;
        }

        if (diedMatch.getKitType().equals(KitType.teamFight)) {
            return;
        }
        
        incrementStat(died.getUniqueId(), Statistic.DEATHS, diedMatch.getKitType());
        
        if (killer != null) {
            incrementStat(killer.getUniqueId(), Statistic.KILLS, diedMatch.getKitType());
        }
    }
    
    public void loadStatistics(UUID uuid) {
        Document document = COLLECTION.find(new Document("_id", uuid.toString())).first();

        if (document == null) {
            document = new Document();
        }
        
        document.put("lastUsername", PotPvPRP.getInstance().getUuidCache().name(uuid));

        final Document finalDocument = document;
        Map<String, Map<Statistic, Double>> subStatisticsMap = Maps.newHashMap();

        KitType.getAllTypes().forEach(kitType -> {
            Document subStatisticsDocument = finalDocument.containsKey(kitType.getId()) ? finalDocument.get(kitType.getId(), Document.class) : new Document();

            Map<Statistic, Double> statsMap = Maps.newHashMap();
            for (Statistic statistic : Statistic.values()) {
                Double value = Objects.firstNonNull(subStatisticsDocument.get(statistic.name(), Double.class), 0D);
                statsMap.put(statistic, value);
            }

            subStatisticsMap.put(kitType.getId(), statsMap);
        });

        if (finalDocument.containsKey("GLOBAL")) {
            Document subStatisticsDocument = finalDocument.containsKey("GLOBAL") ? finalDocument.get("GLOBAL", Document.class) : new Document();

            Map<Statistic, Double> statsMap = Maps.newHashMap();
            for (Statistic statistic : Statistic.values()) {
                Double value = Objects.firstNonNull(subStatisticsDocument.get(statistic.name(), Double.class), 0D);
                statsMap.put(statistic, value);
            }

            subStatisticsMap.put("GLOBAL", statsMap);
        } else {
            subStatisticsMap.put("GLOBAL", Maps.newHashMap());
        }

        statisticsMap.put(uuid, subStatisticsMap);
    }

    public void saveStatistics(UUID uuid) {
        Map<String, Map<Statistic, Double>> subMap = statisticsMap.get(uuid);
        if (subMap == null) {
            return;
        }

        Document toInsert = new Document();
        subMap.entrySet().forEach(entry -> {
            Document typeStats = new Document();
            entry.getValue().entrySet().forEach(subEntry -> {
                typeStats.put(subEntry.getKey().name(), subEntry.getValue());
            });

            toInsert.put(entry.getKey(), typeStats);
        });
        
        toInsert.put("lastUsername", PotPvPRP.getInstance().getUuidCache().name(uuid));

        COLLECTION.updateOne(new Document("_id", uuid.toString()), new Document("$set", toInsert), MongoUtils.UPSERT_OPTIONS);
    }

    public void unloadStatistics(UUID uuid) {
        statisticsMap.remove(uuid);
    }

    public void incrementStat(UUID uuid, Statistic statistic, KitType kitType) {
        boolean shouldUpdateWLR = statistic == Statistic.WINS || statistic == Statistic.LOSSES;
        boolean shouldUpdateKDR = statistic == Statistic.KILLS || statistic == Statistic.DEATHS;

        if (!statisticsMap.containsKey(uuid)) return; // not loaded, so prob offline so it won't save anyway

        incrementEntry(uuid, kitType.getId(), statistic);
        incrementEntry(uuid, "GLOBAL", statistic);

        if (shouldUpdateWLR) {
            recalculateWLR(uuid, kitType);
        } else if (shouldUpdateKDR) {
            recalculateKDR(uuid, kitType);
        }
    }

    private void recalculateWLR(UUID uuid, KitType kitType) {
        double totalWins = getStat(uuid, Statistic.WINS, kitType.getId());
        double totalLosses = getStat(uuid, Statistic.LOSSES, kitType.getId());

        double ratio = totalWins / Math.max(totalLosses, 1);
        statisticsMap.get(uuid).get(kitType.getId()).put(Statistic.WLR, ratio);

        totalWins = getStat(uuid, Statistic.WINS, "GLOBAL");
        totalLosses = getStat(uuid, Statistic.LOSSES, "GLOBAL");

        ratio = totalWins / Math.max(totalLosses, 1);
        statisticsMap.get(uuid).get("GLOBAL").put(Statistic.WLR, ratio);
    }

    private void recalculateKDR(UUID uuid, KitType kitType) {
        double totalKills = getStat(uuid, Statistic.KILLS, kitType.getId());
        double totalDeaths = getStat(uuid, Statistic.DEATHS, kitType.getId());

        double ratio = totalKills / Math.max(totalDeaths, 1);
        statisticsMap.get(uuid).get(kitType.getId()).put(Statistic.KDR, ratio);

        totalKills = getStat(uuid, Statistic.KILLS, "GLOBAL");
        totalDeaths = getStat(uuid, Statistic.DEATHS, "GLOBAL");

        ratio = totalKills / Math.max(totalDeaths, 1);
        statisticsMap.get(uuid).get("GLOBAL").put(Statistic.KDR, ratio);
    }

    private void incrementEntry(UUID uuid, String primaryKey, Statistic statistic) {
        Map<Statistic, Double> subMap = statisticsMap.get(uuid).get(primaryKey);
        subMap.put(statistic, subMap.getOrDefault(statistic, 0D) + 1);
    }

    public double getStat(UUID uuid, Statistic statistic, String kitType) {
        return Objects.firstNonNull(statisticsMap.getOrDefault(uuid, ImmutableMap.of()).getOrDefault(kitType, ImmutableMap.of()).get(statistic), 0D);
    }

    private static enum Statistic {
        WINS, LOSSES, WLR, KILLS, DEATHS, KDR;
    }

    
}
