package net.frozenorb.potpvp.match;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.arena.Arena;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.match.listener.GoldenHeadListener;
import net.frozenorb.potpvp.match.listener.KitSelectionListener;
import net.frozenorb.potpvp.match.listener.MatchBlockPickupListener;
import net.frozenorb.potpvp.match.listener.MatchBuildListener;
import net.frozenorb.potpvp.match.listener.MatchComboListener;
import net.frozenorb.potpvp.match.listener.MatchCountdownListener;
import net.frozenorb.potpvp.match.listener.MatchDeathMessageListener;
import net.frozenorb.potpvp.match.listener.MatchDurationLimitListener;
import net.frozenorb.potpvp.match.listener.MatchEnderPearlDamageListener;
import net.frozenorb.potpvp.match.listener.MatchFreezeListener;
import net.frozenorb.potpvp.match.listener.MatchGeneralListener;
import net.frozenorb.potpvp.match.listener.MatchHardcoreHealingListener;
import net.frozenorb.potpvp.match.listener.MatchHealthDisplayListener;
import net.frozenorb.potpvp.match.listener.MatchPartySpectateListener;
import net.frozenorb.potpvp.match.listener.MatchRodListener;
import net.frozenorb.potpvp.match.listener.MatchSoupListener;
import net.frozenorb.potpvp.match.listener.MatchStatsListener;
import net.frozenorb.potpvp.match.listener.MatchWizardListener;
import net.frozenorb.potpvp.match.listener.SpectatorItemListener;
import net.frozenorb.potpvp.match.listener.SpectatorPreventionListener;

public final class MatchHandler {

    public static final String MONGO_COLLECTION_NAME = "matches";

    private final Set<Match> hostedMatches = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter @Setter private boolean rankedMatchesDisabled;
    @Getter @Setter private boolean unrankedMatchesDisabled;

    // these two maps are one of my least favorite bits of code in the match module, but
    // they let us run O(1) lookups of player matches, so we absolutely should keep them.
    // we do, however, have to be very careful to keep them updated with the "actuaL" data.
    // if we ever have issues with these, we have /pstatus to compare the "actual" data (O(n) scan)
    // with the O(1) data here
    @Getter(AccessLevel.PACKAGE) private final Map<UUID, Match> playingMatchCache = new ConcurrentHashMap<>();
    @Getter(AccessLevel.PACKAGE) private final Map<UUID, Match> spectatingMatchCache = new ConcurrentHashMap<>();

    public MatchHandler() {
        Bukkit.getPluginManager().registerEvents(new GoldenHeadListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitSelectionListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBlockPickupListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBuildListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchComboListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchCountdownListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDeathMessageListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDurationLimitListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchEnderPearlDamageListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchFreezeListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchGeneralListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchHardcoreHealingListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchHealthDisplayListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchPartySpectateListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchRodListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchSoupListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchStatsListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchWizardListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorItemListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new SpectatorPreventionListener(), PotPvPSI.getInstance());
    }

    public Match startMatch(List<MatchTeam> teams, KitType kitType, boolean ranked, boolean allowRematches) {
        boolean anyOps = false;

        for (MatchTeam team : teams) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                if (!anyOps && memberPlayer.isOp()) {
                    anyOps = true;
                }

                if (isPlayingOrSpectatingMatch(memberPlayer)) {
                    throw new IllegalArgumentException(PotPvPSI.getInstance().getUuidCache().name(member) + " is already in a match!");
                }
            }
        }

        if (!anyOps) {
            if (ranked && rankedMatchesDisabled) {
                throw new IllegalArgumentException("Ranked match creation is disabled!");
            } else if (unrankedMatchesDisabled) {
                throw new IllegalArgumentException("Unranked match creation is disabled!");
            }
        }

        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        long matchSize = teams.stream()
                .mapToInt(t -> t.getAllMembers().size())
                .sum();

        // the archer only logic here was often a source of confusion while
        // this code was being written. below is a table of the desired
        // results / if a match can run in a given arena
        //
        //              Arena is archer only    Arena is not archer only
        //  Is Archer           Yes                         Yes
        // Not Archer           No                          Yes
        //
        // the left side of the or statement covers the top row, and the
        // right side covers the right side
        Optional<Arena> openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                schematic.isEnabled() &&
                        !schematic.isTeamFightsOnly() &&
                        canUseSchematic(kitType, schematic) &&
                        matchSize <= schematic.getMaxPlayerCount() &&
                        matchSize >= schematic.getMinPlayerCount() &&
                        (!ranked || schematic.isSupportsRanked()) &&
                        (kitType.getId().equals("ARCHER") || !schematic.isArcherOnly())
        );

        if (kitType.equals(KitType.teamFight)) {
            openArenaOpt = arenaHandler.allocateUnusedArena(schematic ->
                    schematic.isEnabled() &&
                            canUseSchematic(kitType, schematic) &&
                            matchSize <= schematic.getMaxPlayerCount() &&
                            matchSize >= schematic.getMinPlayerCount() &&
                            schematic.isTeamFightsOnly()
            );
        }

        if (!openArenaOpt.isPresent()) {
            PotPvPSI.getInstance().getLogger().warning("Failed to start match: No open arenas found");
            return null;
        }

        Match match = new Match(kitType, openArenaOpt.get(), teams, ranked, allowRematches);

        hostedMatches.add(match);
        match.startCountdown();

        return match;
    }

    public static boolean canUseSchematic(KitType kitType, ArenaSchematic schematic) {
        String kitId = kitType.getId();

        if (kitId.equals("ARCHER")) return schematic.isArcherOnly();
        if (kitId.equals("BUILDUHC")) return schematic.isBuildUHCOnly();
        if (kitId.equals("SPLEEF")) return schematic.isSpleefOnly();
        if (kitId.equals("SUMO")) return schematic.isSumoOnly();
        if (kitId.equals("HCF")) return schematic.isHCFOnly();
        if (kitType.equals(KitType.teamFight)) return schematic.isTeamFightsOnly();

        if (schematic.isArcherOnly()) return kitId.equals("ARCHER");
        if (schematic.isBuildUHCOnly()) return kitId.equals("BUILDUHC");
        if (schematic.isSpleefOnly()) return kitId.equals("SPLEEF");
        if (schematic.isSumoOnly()) return kitId.equals("SUMO");
        if (schematic.isHCFOnly()) return kitId.equals("HCF");

        return true;
    }

    void removeMatch(Match match) {
        hostedMatches.remove(match);
    }

    /**
     * Gets a read-only set containing all matches currently being hosted.
     * This includes matches that are pre-start and ending.
     * @return a read-only representation of all hosted matches
     */
    public Set<Match> getHostedMatches() {
        return ImmutableSet.copyOf(hostedMatches);
    }

    /**
     * Returns a sum of all players who are playing in an IN_PROGRESS match
     * @return number of players playing in IN_PROGRESS matches
     */
    public int countPlayersPlayingInProgressMatches() {
        return countPlayersPlayingMatches(m -> m.getState() == MatchState.COUNTDOWN || m.getState() == MatchState.IN_PROGRESS);
    }

    /**
     * Returns a sum of all players who are playing in a {@link Match}
     * that passes the {@link Predicate} provided.
     * @return number of players playing in matches that
     *          pass the {@link Predicate} provided
     */
    public int countPlayersPlayingMatches(Predicate<Match> inclusionPredicate) {
        int result = 0;

        for (Match match : hostedMatches) {
            if (inclusionPredicate.test(match)) {
                for (MatchTeam team : match.getTeams()) {
                    result += team.getAliveMembers().size();
                }
            }
        }

        return result;
    }

    /**
     * Gets the match currently being played by the player provided.
     * In this context, played means a player who alive and fighting on a team
     *
     * @param player player to look up match for
     * @return the match being played by the provided player
     */
    public Match getMatchPlaying(Player player) {
        return playingMatchCache.get(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated by the player provided.
     * In this context, spectated includes both players who died while
     * fighting who have not left and players who joined via /spectate.
     *
     * @param player player to look up match for
     * @return the match being spectated by the provided player
     */
    public Match getMatchSpectating(Player player) {
        return spectatingMatchCache.get(player.getUniqueId());
    }

    /**
     * Gets the match currently being spectated or played by the player provided.
     * This method acts as a combination of {@link MatchHandler#getMatchPlaying(Player)}
     * and {@link MatchHandler#getMatchSpectating(Player)}.
     *
     * @param player player to look up match for
     * @return the match being played or spectated by the provided player
     */
    public Match getMatchPlayingOrSpectating(Player player) {
        Match playing = playingMatchCache.get(player.getUniqueId());

        if (playing != null) {
            return playing;
        } else {
            return spectatingMatchCache.get(player.getUniqueId());
        }
    }

    /**
     * Checks if the player specified is playing a match.
     * See {@link MatchHandler#getMatchPlaying(Player)} for a definition
     * of the term playing.
     * @param player player to look up match for
     * @return if a match is being played by the provided player
     */
    public boolean isPlayingMatch(Player player) {
        return playingMatchCache.containsKey(player.getUniqueId());
    }

    /**
     * Checks if the player specified is spectating a match.
     * See {@link MatchHandler#getMatchSpectating(Player)} (UUID)} for a
     * definition of the term spectating.
     * @param player player to look up match for
     * @return if a match is being spectated by the provided player
     */
    public boolean isSpectatingMatch(Player player) {
        return spectatingMatchCache.containsKey(player.getUniqueId());
    }

    /**
     * Checks if the player specified is playing or spectating a match.
     * See {@link MatchHandler#getMatchPlayingOrSpectating(Player)} for a definition
     * of the term playing or spectating.
     * @param player player to look up match for
     * @return if a match is being played or spectated by the provided player
     */
    public boolean isPlayingOrSpectatingMatch(Player player) {
        if (player == null) return false;
        return playingMatchCache.containsKey(player.getUniqueId()) || spectatingMatchCache.containsKey(player.getUniqueId());
    }

}