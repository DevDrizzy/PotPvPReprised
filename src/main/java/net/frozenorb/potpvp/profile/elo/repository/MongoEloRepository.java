package net.frozenorb.potpvp.profile.elo.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.profile.elo.EloHandler;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.util.MongoUtils;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;

public final class MongoEloRepository implements EloRepository {
    
    public static final String MONGO_COLLECTION_NAME = "elo";

    private static Map<String, Map<String, Integer>> cachedFormattedElo = Maps.newHashMap();
    @Getter private static MongoEloRepository instance;

    public MongoEloRepository() {
        instance = this;
        MongoUtils.getCollection(MONGO_COLLECTION_NAME).createIndex(new Document("players", 1));

        Bukkit.getScheduler().runTaskTimerAsynchronously(PotPvPRP.getInstance(), () -> {
            refreshFormattedElo();
        }, 5 * 30, 5 * 30);
    }
    
    @Override
    public Map<KitType, Integer> loadElo(Set<UUID> playerUuids) throws IOException {
        MongoCollection<Document> partyEloCollection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Set<String> playerUuidStrings = playerUuids.stream().map(u -> u.toString()).collect(Collectors.toSet());
        
        try {
            Document eloDocument = partyEloCollection.find(new Document("players", playerUuidStrings)).first();
            
            if (eloDocument == null) {
                return ImmutableMap.of();
            }

            Map<KitType, Integer> parsedElo = new HashMap<>();            
            final Document finalEloDocument = eloDocument;
            
            KitType.getAllTypes().forEach((kitType) -> {
                Integer elo = finalEloDocument.getInteger(kitType.getId());
                if (elo != null) {
                    parsedElo.put(kitType, elo);
                } else {
                    parsedElo.put(kitType, EloHandler.DEFAULT_ELO);
                }
            });
            
            return ImmutableMap.copyOf(parsedElo);
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }
    
    @Override
    public void saveElo(Set<UUID> playerUuids, Map<KitType, Integer> elo) throws IOException {
        Document document = new Document();
        elo.forEach((kit, value) -> document.put(kit.getId(), value));
        int[] wrapper = new int[2];

        KitType.getAllTypes().forEach(kitType -> {
            document.putIfAbsent(kitType.getId(), EloHandler.DEFAULT_ELO);
        });

        KitType.getAllTypes().stream().filter(kitType -> kitType.isSupportsRanked()).forEach(kitType -> {
            wrapper[0] = wrapper[0] + 1;
            wrapper[1] = wrapper[1] + elo.getOrDefault(kitType, EloHandler.DEFAULT_ELO);
        });

        document.put("GLOBAL", wrapper[1] / wrapper[0]);
        if (playerUuids.size() == 1) {
            document.put("lastUsername", PotPvPRP.getInstance().getUuidCache().name(playerUuids.iterator().next()));
        }

        try {
            MongoUtils.getCollection(MONGO_COLLECTION_NAME).updateOne(new Document("players", playerUuids.stream().map(u -> u.toString()).collect(Collectors.toSet())), new Document("$set", document), MongoUtils.UPSERT_OPTIONS // creates document if it doesn't exist
            );
        } catch (MongoException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Map<String, Integer> topElo(KitType type) throws IOException {
        return cachedFormattedElo.getOrDefault(type == null ? "GLOBAL" : type.getId(), ImmutableMap.of());
    }

    private void refreshFormattedElo() {
        KitType.getAllTypes().stream().filter(type -> type.isSupportsRanked()).forEach(type -> {
            Map<String, Integer> topElo = Maps.newLinkedHashMap();
            mapTop10(type.getId(), topElo);
            cachedFormattedElo.put(type.getId(), topElo);
        });

        Map<String, Integer> topGlobal = Maps.newLinkedHashMap();
        mapTop10("GLOBAL", topGlobal);
        cachedFormattedElo.put("GLOBAL", topGlobal);
    }

    public void mapTop10(String kitTypeName, Map<String, Integer> toInsert) {
        try {
            MongoUtils.getCollection(MONGO_COLLECTION_NAME).find().sort(Sorts.descending(kitTypeName)).limit(10).forEach(new Consumer<Document>() {
                @Override
                public void accept(Document document) {
                    Object eloNumber = document.get(kitTypeName);
                    int elo = eloNumber != null && eloNumber instanceof Number ? ((Number) eloNumber).intValue() : EloHandler.DEFAULT_ELO;
                    toInsert.put(PatchedPlayerUtils.getFormattedName(UUID.fromString((String) document.get("players", ArrayList.class).get(0))), elo);
                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}