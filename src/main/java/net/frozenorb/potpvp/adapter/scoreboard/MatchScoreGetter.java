package net.frozenorb.potpvp.adapter.scoreboard;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;

import net.frozenorb.potpvp.util.PlayerUtils;
import net.frozenorb.potpvp.util.TimeUtils;
import net.frozenorb.potpvp.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.potpvp.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.potpvp.util.scoreboard.construct.ScoreFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.kittype.HealingMethod;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.MatchTeam;

// the list here must be viewed as rendered javadoc to make sense. In IntelliJ, click on
// 'MatchScoreGetter' and press Control+Q
/**
 * Implements the scoreboard as defined in {@link net.frozenorb.potpvp.util.scoreboard}<br />
 * This class is divided up into multiple procedures to reduce overall complexity<br /><br />
 *
 * Although there are many possible outcomes, for a 4v4 match this code would take the
 * following path:<br /><br />
 *
 * <ul>
 *   <li>accept()</li>
 *   <ul>
 *     <li>renderParticipantLines()</li>
 *     <ul>
 *       <li>render4v4MatchLines()</li>
 *       <ul>
 *         <li>renderTeamMemberOverviewLines()</li>
 *         <li>renderTeamMemberOverviewLines()</li>
 *       </ul>
 *     </ul>
 *     <li>renderMetaLines()</li>
 *   </ul>
 * </ul>
 */
final class MatchScoreGetter implements BiConsumer<Player, List<String>> {

    // we can't count heals on an async thread so we use
    // a task to count and then report values (via this map) to
    // the scoreboard thread
    private Map<UUID, Integer> healsLeft = ImmutableMap.of();

    MatchScoreGetter() {
        Bukkit.getScheduler().runTaskTimer(PotPvPRP.getInstance(), () -> {
            MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
            Map<UUID, Integer> newHealsLeft = new HashMap<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Match playing = matchHandler.getMatchPlaying(player);

                if (playing == null) {
                    continue;
                }

                HealingMethod healingMethod = playing.getKitType().getHealingMethod();

                if (healingMethod == null) {
                    continue;
                }

                int count = healingMethod.count(player.getInventory().getContents());
                newHealsLeft.put(player.getUniqueId(), count);
            }

            this.healsLeft = newHealsLeft;
        }, 10L, 10L);
    }

    @Override
    public void accept(Player player, List<String> scores) {
        Optional<UUID> followingOpt = PotPvPRP.getInstance().getFollowHandler().getFollowing(player);
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);

        // this method shouldn't have even been called if
        // they're not in a match
        if (match == null) {
            followingOpt.ifPresent(uuid -> scores.add("&fFollowing: &c" + PotPvPRP.getInstance().getUuidCache().name(uuid)));

            if (player.hasMetadata("ModMode")) {
                scores.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "In Silent Mode");
            }
            return;
        }

        if (match.getState() == MatchState.ENDING) {
            scores.add(ChatColor.WHITE + "Match ended");
        }

        boolean participant = match.getTeam(player.getUniqueId()) != null;
        boolean renderPing = false;

        renderMetaLines(scores, match, participant);

        if (participant) {
            renderPing = renderParticipantLines(scores, match, player);
        } else {
            MatchTeam previousTeam = match.getPreviousTeam(player.getUniqueId());
            renderSpectatorLines(scores, match, previousTeam);
        }

        if (renderPing) {
            renderPingLines(scores, match, player);
        }

        // this definitely can be a .ifPresent, however creating the new lambda that often
        // was causing some performance issues, so we do this less pretty (but more efficient)
        // check (we can't define the lambda up top and reference because we reference the
        // scores variable)

        followingOpt.ifPresent(uuid -> scores.add("&fFollowing: &c" + PotPvPRP.getInstance().getUuidCache().name(uuid)));
    }

    private boolean renderParticipantLines(List<String> scores, Match match, Player player) {
        List<MatchTeam> teams = match.getTeams();

        // only render scoreboard if we have two teams
        if (teams.size() != 2) {
            return false;
        }

        // this method won't be called if the player isn't a participant
        MatchTeam ourTeam = match.getTeam(player.getUniqueId());
        MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

        // we use getAllMembers instead of getAliveMembers to avoid
        // mid-match scoreboard changes as players die / disconnect
        int ourTeamSize = ourTeam.getAllMembers().size();
        int otherTeamSize = otherTeam.getAllMembers().size();

        boolean ping = false;

        if (ourTeamSize == 1 && otherTeamSize == 1) {
            render1v1MatchLines(scores, otherTeam);
            ping = true;
        } else if (ourTeamSize <= 2 && otherTeamSize <= 2) {
            render2v2MatchLines(scores, ourTeam, otherTeam, player, match.getKitType().getHealingMethod());
        } else if (ourTeamSize <= 4 && otherTeamSize <= 4) {
            render4v4MatchLines(scores, ourTeam, otherTeam);
        } else if (ourTeam.getAllMembers().size() <= 9) {
            renderLargeMatchLines(scores, ourTeam, otherTeam);
        } else {
            renderJumboMatchLines(scores, ourTeam, otherTeam);
        }

        String archerMarkScore = getArcherMarkScore(player);
        String bardEffectScore = getBardEffectScore(player);
        String bardEnergyScore = getBardEnergyScore(player);

        if (archerMarkScore != null) {
            scores.add(" &fArcher Mark&7: &c" + archerMarkScore);
        }

        if (bardEffectScore != null) {
            scores.add(" &fBard Effect&7: &c" + bardEffectScore);
        }

        if (bardEnergyScore != null) {
            scores.add(" &fBard Energy&7: &c" + bardEnergyScore);
        }

        return ping;
    }

    private void render1v1MatchLines(List<String> scores, MatchTeam otherTeam) {
        scores.add(" &fOpponent: &c" + PotPvPRP.getInstance().getUuidCache().name(otherTeam.getFirstMember()));

    }

    private void render2v2MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam, Player player, HealingMethod healingMethod) {
        // 2v2, but potentially 1v2 / 1v1 if players have died
        UUID partnerUuid = null;

        for (UUID teamMember : ourTeam.getAllMembers()) {
            if (teamMember != player.getUniqueId()) {
                partnerUuid = teamMember;
                break;
            }
        }

        if (partnerUuid != null) {
            String healthStr;
            String healsStr;
            String namePrefix;

            if (ourTeam.isAlive(partnerUuid)) {
                Player partnerPlayer = Bukkit.getPlayer(partnerUuid); // will never be null (or isAlive would've returned false)
                double health = Math.round(partnerPlayer.getHealth()) / 2D;
                int heals = healsLeft.getOrDefault(partnerUuid, 0);

                ChatColor healthColor;
                ChatColor healsColor;

                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = ChatColor.GOLD;
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                if (heals > 20) {
                    healsColor = ChatColor.GREEN;
                } else if (heals > 12) {
                    healsColor = ChatColor.YELLOW;
                } else if (heals > 8) {
                    healsColor = ChatColor.GOLD;
                } else if (heals > 3) {
                    healsColor = ChatColor.RED;
                } else {
                    healsColor = ChatColor.DARK_RED;
                }

                namePrefix = "&a";
                healthStr = healthColor.toString() + health + " *❤*" + ChatColor.GRAY;

                if (healingMethod != null) {
                    healsStr = " &l⏐ " + healsColor.toString() + heals + " " + (heals == 1 ? healingMethod.getShortSingular() : healingMethod.getShortPlural());
                } else {
                    healsStr = "";
                }
            } else {
                namePrefix = "&7&m";
                healthStr = "&4RIP";
                healsStr = "";
            }

            scores.add(namePrefix + PotPvPRP.getInstance().getUuidCache().name(partnerUuid));
            scores.add(healthStr + healsStr);
            scores.add("");
        }

        scores.add(" &c&lOpponents");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam));

        // Removes the space
        if (PotPvPRP.getInstance().getMatchHandler().getMatchPlaying(player).getState() == MatchState.IN_PROGRESS) {
            scores.add("&c");
        }
    }

    private void render4v4MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // Above a 2v2, but up to a 4v4.
        scores.add(" &fTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add(" ");
        scores.add(" &fOpponents &c(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam));
        if (PotPvPRP.getInstance().getMatchHandler().getMatchPlaying(Bukkit.getPlayer(ourTeam.getFirstAliveMember())).getState() == MatchState.IN_PROGRESS) {
            scores.add("&c");
        }
    }

    private void renderLargeMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // We just display THEIR team's names, and the other team is a number.
        scores.add(" &fTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add(" ");
        scores.add(" &fOpponents: &c" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
    }

    private void renderJumboMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // We just display numbers.
        scores.add(" &fTeam: &c" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size());
        scores.add(" &fOpponents: &c" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
    }

    private void renderSpectatorLines(List<String> scores, Match match, MatchTeam oldTeam) {
        String rankedStr = match.isRanked() ? " (R)" : "";
        scores.add(" &fKit: &c" + match.getKitType().getColoredDisplayName() + rankedStr);

        List<MatchTeam> teams = match.getTeams();

        // only render team overview if we have two teams
        if (teams.size() == 2) {
            MatchTeam teamOne = teams.get(0);
            MatchTeam teamTwo = teams.get(1);

            if (teamOne.getAllMembers().size() != 1 && teamTwo.getAllMembers().size() != 1) {
                // spectators who were on a team see teams as they relate
                // to them, not just one/two.
                if (oldTeam == null) {
                    scores.add(" &fTeam A: &c" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size());
                    scores.add(" &fTeam B: &c" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size());
                } else {
                    MatchTeam otherTeam = oldTeam == teamOne ? teamTwo : teamOne;

                    scores.add(" &fTeam: &c" + oldTeam.getAliveMembers().size() + "/" + oldTeam.getAllMembers().size());
                    scores.add(" &fOpponents: &c" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
                }
            }
        }
    }

    private void renderMetaLines(List<String> scores, Match match, boolean participant) {
        Date startedAt = match.getStartedAt();
        Date endedAt = match.getEndedAt();
        String formattedDuration;

        // short circuit for matches which are still counting down
        // or which ended before they started (if a player disconnects
        // during countdown)
        if (startedAt == null) {
            return;
        } else {
            // we go from when it started to either now (if it's in progress)
            // or the timestamp at which the match actually ended
            formattedDuration = TimeUtils.formatLongIntoHHMMSS(ChronoUnit.SECONDS.between(
                    startedAt.toInstant(),
                    endedAt == null ? Instant.now() : endedAt.toInstant()
            ));
        }

        // spectators don't have any bold entries on their scoreboard
        scores.add("&c&lMatch");
        scores.add(" &fDuration: &c" + formattedDuration);
    }

    private void renderPingLines(List<String> scores, Match match, Player ourPlayer) {
        if (Boolean.TRUE) return;
        List<MatchTeam> teams = match.getTeams();
        if (teams.size() == 2) {
            MatchTeam firstTeam = teams.get(0);
            MatchTeam secondTeam = teams.get(1);

            Set<UUID> firstTeamPlayers = firstTeam.getAllMembers();
            Set<UUID> secondTeamPlayers = secondTeam.getAllMembers();

            if (firstTeamPlayers.size() == 1 && secondTeamPlayers.size() == 1) {
                scores.add(" ");
                scores.add(" &fYour Ping: &c" + PlayerUtils.getPing(ourPlayer));
                Player otherPlayer = Bukkit.getPlayer(match.getTeam(ourPlayer.getUniqueId()) == firstTeam ? secondTeam.getFirstMember() : firstTeam.getFirstMember());
                if (otherPlayer == null) return;
                scores.add(" &fTheir Ping: &c" + PlayerUtils.getPing(otherPlayer));
            }
        }
    }

    /* Returns the names of all alive players, colored + indented, followed
       by the names of all dead players, colored + indented. */

    private List<String> renderTeamMemberOverviewLinesWithHearts(MatchTeam team) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // seperate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + PotPvPRP.getInstance().getUuidCache().name(teamMember) + " " + getHeartString(team, teamMember));
            } else {
                deadLines.add(" &7&m" + PotPvPRP.getInstance().getUuidCache().name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        return result;
    }

    private List<String> renderTeamMemberOverviewLines(MatchTeam team) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // seperate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + PotPvPRP.getInstance().getUuidCache().name(teamMember));
            } else {
                deadLines.add(" &7&m" + PotPvPRP.getInstance().getUuidCache().name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        return result;
    }

    private String getHeartString(MatchTeam ourTeam, UUID partnerUuid) {
        if (partnerUuid != null) {
            String healthStr;

            if (ourTeam.isAlive(partnerUuid)) {
                Player partnerPlayer = Bukkit.getPlayer(partnerUuid); // will never be null (or isAlive would've returned false)
                double health = Math.round(partnerPlayer.getHealth()) / 2D;

                ChatColor healthColor;

                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = ChatColor.GOLD;
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                healthStr = healthColor + "(" + health + " ❤)";
            } else {
                healthStr = "&4(RIP)";
            }

            return healthStr;
        } else {
            return "&4(RIP)";
        }
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEffectScore(Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEnergyScore(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }

        return (null);
    }
}
