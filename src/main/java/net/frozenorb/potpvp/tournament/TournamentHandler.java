package net.frozenorb.potpvp.tournament;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.party.Party;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class TournamentHandler {

    @Getter @Setter private Tournament tournament = null;

    public TournamentHandler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PotPvPRP.getInstance(), () -> {
            if (tournament != null) tournament.check();
        }, 20L, 20L);

        populateTournamentStatuses();
    }

    public boolean isInTournament(Party party) {
        return tournament != null && tournament.isInTournament(party);
    }

    public boolean isInTournament(Match match) {
        return tournament != null && tournament.getMatches().contains(match);
    }

    private static List<TournamentStatus> allStatuses = Lists.newArrayList();

    private void populateTournamentStatuses() {
        List<KitType> viewableKits = KitType.getAllTypes().stream().filter(kit -> !kit.isHidden()).collect(Collectors.toList());
        if (KitType.byId("NODEBUFF") == null) return;
        allStatuses.add(new TournamentStatus(0, ImmutableList.of(1), ImmutableList.of(16, 32), viewableKits));
        allStatuses.add(new TournamentStatus(250, ImmutableList.of(1), ImmutableList.of(32), viewableKits));
        allStatuses.add(new TournamentStatus(300, ImmutableList.of(1), ImmutableList.of(48, 64), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(400, ImmutableList.of(1), ImmutableList.of(64), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(500, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(600, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(700, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
        allStatuses.add(new TournamentStatus(800, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.byId("NODEBUFF"))));
    }

    @Getter
    public static class TournamentStatus {
        private int minimumPlayerCount;
        private List<Integer> teamSizes;
        private List<Integer> teamCounts;
        private List<KitType> kitTypes;

        public TournamentStatus(int minimumPlayerCount, List<Integer> teamSizes, List<Integer> teamCounts, List<KitType> kitTypes) {
            this.minimumPlayerCount = minimumPlayerCount;
            this.teamSizes = teamSizes;
            this.teamCounts = teamCounts;
            this.kitTypes = kitTypes;
        }

        public static TournamentStatus forPlayerCount(int playerCount) {
            if (playerCount == 0) return null;
            for (int i = allStatuses.size() - 1; 0 <= i; i--) {
                if (allStatuses.get(i).minimumPlayerCount <= playerCount) return allStatuses.get(i);
            }
            throw new IllegalArgumentException("No suitable sizes found!");
        }
    }
}