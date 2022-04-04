package net.frozenorb.potpvp.postmatchinv;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.postmatchinv.listener.PostMatchInvGeneralListener;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PostMatchInvHandler {

    // uuid -> their "view" of their last match
    // this varies per player, so we must store them all individually
    private final Map<UUID, Map<UUID, PostMatchPlayer>> playerData = new ConcurrentHashMap<>();

    public PostMatchInvHandler() {
        Bukkit.getPluginManager().registerEvents(new PostMatchInvGeneralListener(), PotPvPSI.getInstance());
    }

    public void recordMatch(Match match) {
        saveInventories(match);
        messagePlayers(match);
    }

    private void saveInventories(Match match) {
        Map<UUID, PostMatchPlayer> matchPlayers = match.getPostMatchPlayers();

        for (MatchTeam team : match.getTeams()) {
            for (UUID member : team.getAliveMembers()) {
                playerData.put(member, matchPlayers);
            }
        }

        for (UUID spectator : match.getSpectators()) {
            playerData.put(spectator, matchPlayers);
        }
    }

    private void messagePlayers(Match match) {
        Map<UUID, Object[]> invMessages = new HashMap<>();

        BaseComponent[] spectatorLine;
        List<UUID> spectatorUuids = new ArrayList<>(match.getSpectators());

        // don't count actual players and players in silent mode.
        spectatorUuids.removeIf(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            return player.hasMetadata("ModMode") || match.getPreviousTeam(uuid) != null;
        });

        if (!spectatorUuids.isEmpty()) {
            List<String> spectatorNames = PatchedPlayerUtils.mapToNames(spectatorUuids);
            spectatorNames.sort(String::compareToIgnoreCase);

            String firstFourNames = Joiner.on(", ").join(
                spectatorNames.subList(
                    0,
                    Math.min(spectatorNames.size(), 4)
                )
            );

            if (spectatorNames.size() > 4) {
                firstFourNames += " (+" + (spectatorNames.size() - 4) + " more)";
            }

            HoverEvent hover = new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                spectatorNames.stream()
                    .map(n -> new TextComponent(ChatColor.AQUA + n + '\n'))
                    .toArray(BaseComponent[]::new)
            );

            spectatorLine = new ComponentBuilder("Spectators (" + spectatorNames.size() + "): ").color(ChatColor.AQUA)
                .append(firstFourNames).color(ChatColor.GRAY).event(hover)
                .create();
        } else {
            // this is dumb but it lets us make the variable effectively final
            // (and avoid a working variable)
            spectatorLine = null;
        }

        createInvMessages(match, invMessages);

        invMessages.forEach((uuid, lines) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                return;
            }

            player.sendMessage(PostMatchInvLang.LINE);
            player.sendMessage(PotPvPSI.getInstance().getDominantColor() + "Post-Match Inventories " + ChatColor.GRAY + "(click name to view)");

            for (Object line : lines) {
                if (line instanceof TextComponent[]) {
                    player.spigot().sendMessage((TextComponent[]) line);
                } else if (line instanceof TextComponent) {
                    player.spigot().sendMessage((TextComponent) line);
                } else if (line instanceof String) {
                    player.sendMessage((String) line);
                }
            }

            if (spectatorLine != null) {
                player.spigot().sendMessage(spectatorLine);
            }

            player.sendMessage(PostMatchInvLang.LINE);
        });
    }

    private void createInvMessages(Match match, Map<UUID, Object[]> invMessages) {
        List<MatchTeam> teams = match.getTeams();

        if (teams.size() != 2) {
            // matches without 2 teams just get big 'participants' sections
            Object[] generic = PostMatchInvLang.genGenericInvs(teams);

            writeSpecInvMessages(match, invMessages, generic);
            writeTeamInvMessages(teams, invMessages, generic);
            return;
        }

        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        if (winnerTeam.getAllMembers().size() == 1 && loserTeam.getAllMembers().size() == 1) {
            // 1v1 messages
            Object[] generic = PostMatchInvLang.gen1v1PlayerInvs(winnerTeam.getFirstMember(), loserTeam.getFirstMember());

            writeSpecInvMessages(match, invMessages, generic);
            writeTeamInvMessages(teams, invMessages, generic);
        } else {
            // normal 2 team messages
            writeSpecInvMessages(match, invMessages, PostMatchInvLang.genSpectatorInvs(winnerTeam, loserTeam));
            writeTeamInvMessages(winnerTeam, invMessages, PostMatchInvLang.genTeamInvs(winnerTeam, winnerTeam, loserTeam));
            writeTeamInvMessages(loserTeam, invMessages, PostMatchInvLang.genTeamInvs(loserTeam, winnerTeam, loserTeam));
        }
    }

    private void writeTeamInvMessages(Iterable<MatchTeam> teams, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (MatchTeam team : teams) {
            writeTeamInvMessages(team, messageMap, messages);
        }
    }

    private void writeTeamInvMessages(MatchTeam team, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (UUID member : team.getAllMembers()) {
            // on this containsKey check:
            // we only want to send messages to players who are alive or were on this team in the match
            // we always add messages from the least specific (ex generic for spectators)
            // to most specific (ex per team messages), so checking if they're already added is a good
            // way to check if they're going to get a message.
            // we can't just use getAllMembers because players could've left and started a new fight
            if (messageMap.containsKey(member) || team.isAlive(member)) {
                messageMap.put(member, messages);
            }
        }
    }

    private void writeSpecInvMessages(Match match, Map<UUID, Object[]> messageMap, Object[] messages) {
        for (UUID spectator : match.getSpectators()) {
            messageMap.put(spectator, messages);
        }
    }

    public Map<UUID, PostMatchPlayer> getPostMatchData(UUID forWhom) {
        return playerData.getOrDefault(forWhom, ImmutableMap.of());
    }

    public void removePostMatchData(UUID forWhom) {
        playerData.remove(forWhom);
    }

}