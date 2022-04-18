package net.frozenorb.potpvp.profile.elo.repository;

import net.frozenorb.potpvp.kit.kittype.KitType;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface EloRepository {

    Map<KitType, Integer> loadElo(Set<UUID> playerUuids) throws IOException;
    void saveElo(Set<UUID> playerUuids, Map<KitType, Integer> elo) throws IOException;

   Map<String, Integer> topElo(KitType kitType) throws IOException;
}