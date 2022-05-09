package net.frozenorb.potpvp.adapter.tablist;

import com.google.common.collect.Sets;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.profile.elo.EloHandler;
import net.frozenorb.potpvp.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.tablist.adapter.TabAdapter;
import xyz.refinedev.tablist.setup.TabEntry;

import java.util.*;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/25/2022
 * Project: potpvp-reprised
 */

public class TablistAdapter implements TabAdapter {

    public String getHeader(Player player) {
        return "&cRefine Development";
    }

    public String getFooter(Player player) {
        return "&adiscord.refinedev.xyz";
    }

    public List<TabEntry> getLines(Player player) {
        List<TabEntry> entries = new ArrayList<>();
        //These are always displayed, no checks for these
        entries.add(new TabEntry(1, 0, "&c&lRefine &7┃ &fPractice"));
        entries.add(new TabEntry(1, 1, ChatColor.GRAY + "Your Connection", Math.max(((PlayerUtils.getPing(player) + 5) / 10) * 10, 1)));

        Match match = PotPvPRP.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);     
        
        // Lobby Layout
        if (match == null) {
            Party party = PotPvPRP.getInstance().getPartyHandler().getParty(player);
            EloHandler eloHandler = PotPvPRP.getInstance().getEloHandler();

            entries.add(new TabEntry(1, 3, PotPvPRP.getInstance().getDominantColor().toString() + ChatColor.BOLD + "Your Rankings"));

            int x = 0;
            int y = 4;

            for ( KitType kitType : KitType.getAllTypes()) {
                if (kitType.isHidden() || !kitType.isSupportsRanked()) {
                    continue;
                }

                entries.add(new TabEntry(x++, y, ChatColor.GRAY + kitType.getDisplayName() + " - " + eloHandler.getElo(player, kitType)));

                if (x == 3) {
                    x = 0;
                    y++;
                }
            }
            
            if (party != null) {
                entries.add(new TabEntry(1, 8, ChatColor.BLUE.toString() + ChatColor.BOLD + "Your Party"));

                int partyX = 0;
                int partyY = 9;

                for (UUID member : getOrderedMembers(player, party)) {
                    int ping = this.getPingOrDefault(member);
                    String suffix = member == party.getLeader() ? ChatColor.GRAY + "*" : "";
                    String displayName = ChatColor.BLUE + PotPvPRP.getInstance().getUuidCache().name(member) + suffix;

                    entries.add(new TabEntry(partyX++, partyY, displayName, ping));

                    if (partyX == 3 && partyY == 20) {
                        break;
                    }

                    if (partyX == 3) {
                        partyX = 0;
                        partyY++;
                    }
                }
            }
        } else {
            // Spectate Layout
            if (match.isSpectator(player.getUniqueId())) {
                MatchTeam oldTeam = match.getTeam(player.getUniqueId());
                List<MatchTeam> teams = match.getTeams();

                // if it's one team versus another
                if (teams.size() == 2) {
                    MatchTeam teamOne = teams.get(0);
                    MatchTeam teamTwo = teams.get(1);

                    boolean duel = teamOne.getAllMembers().size() == 1 && teamTwo.getAllMembers().size() == 1;

                    // first, we want to check if they were a part of the match and died, and if so, render the tab differently.
                    if (oldTeam != null) {
                        // if they were, it means it couldn't have been a duel, so we don't check for that below.
                        MatchTeam ourTeam = teamOne == oldTeam ? teamOne : teamTwo;
                        MatchTeam otherTeam = teamOne == ourTeam ? teamTwo : teamOne;

                        {
                            // Column 1
                            if (!duel) {
                                entries.add(new TabEntry(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")"));
                            } else {
                                entries.add(new TabEntry(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You"));
                            }
                            renderTeamMemberOverviewEntries(entries, ourTeam, 0, ChatColor.GREEN);
                        }

                        {
                            // Column 3
                            if (!duel) {
                                entries.add(new TabEntry(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")"));
                            } else {
                                entries.add(new TabEntry(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent"));
                            }
                            renderTeamMemberOverviewEntries(entries, otherTeam, 2, ChatColor.RED);
                        }

                    } else {

                        {
                            // Column 1
                            // we handle duels a bit differently
                            if (!duel) {
                                entries.add(new TabEntry(0, 3, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Team One (" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size() + ")"));
                            } else {
                                entries.add(new TabEntry(0, 3, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Player One"));
                            }
                            renderTeamMemberOverviewEntries(entries, teamOne, 0, ChatColor.LIGHT_PURPLE);
                        }

                        {
                            // Column 3
                            // we handle duels a bit differently
                            if (!duel) {
                                entries.add(new TabEntry(2, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Team Two (" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size() + ")"));
                            } else {
                                entries.add(new TabEntry(2, 3, ChatColor.AQUA + ChatColor.BOLD.toString() + "Player Two"));
                            }
                            renderTeamMemberOverviewEntries(entries, teamTwo, 2, ChatColor.AQUA);
                        }

                    }
                } else { // it's an FFA or something else like that
                    entries.add(new TabEntry(1, 3, ChatColor.BLUE + ChatColor.BOLD.toString() + "Party FFA"));

                    int x = 0;
                    int y = 4;

                    Map<String, Integer> otherEntries = new LinkedHashMap<>();

                    if (oldTeam != null) {
                        // if they were a part of this match, we want to render it like we would for an alive player, showing their team-mates first and in green.
                        otherEntries = renderTeamMemberOverviewLines(oldTeam, ChatColor.GREEN);

                        {
                            // this is where we'll be adding everyone else
                            Map<String, Integer> deadLines = new LinkedHashMap<>();

                            for (MatchTeam otherTeam : match.getTeams()) {
                                if (otherTeam == oldTeam) {
                                    continue;
                                }

                                // separate lists to sort alive players before dead
                                // + color differently
                                for (UUID enemy : otherTeam.getAllMembers()) {
                                    if (otherTeam.isAlive(enemy)) {
                                        otherEntries.put(ChatColor.RED + PotPvPRP.getInstance().getUuidCache().name(enemy), this.getPingOrDefault(enemy));
                                    } else {
                                        deadLines.put("&7&m" + PotPvPRP.getInstance().getUuidCache().name(enemy), this.getPingOrDefault(enemy));
                                    }
                                }
                            }

                            otherEntries.putAll(deadLines);
                        }
                    } else {
                        // if they're just a random spectator, we'll pick different colors for each team.
                        Map<String, Integer> deadLines = new LinkedHashMap<>();

                        for (MatchTeam team : match.getTeams()) {
                            for (UUID enemy : team.getAllMembers()) {
                                if (team.isAlive(enemy)) {
                                    otherEntries.put("&c" + PotPvPRP.getInstance().getUuidCache().name(enemy), this.getPingOrDefault(enemy));
                                } else {
                                    deadLines.put("&7&m" + PotPvPRP.getInstance().getUuidCache().name(enemy), this.getPingOrDefault(enemy));
                                }
                            }
                        }

                        otherEntries.putAll(deadLines);
                    }

                    List<Map.Entry<String, Integer>> result = new ArrayList<>(otherEntries.entrySet());

                    // actually display our entries
                    for (int index = 0; index < result.size(); index++) {
                        Map.Entry<String, Integer> entry = result.get(index);

                        entries.add(new TabEntry(x++, y, entry.getKey(), entry.getValue()));

                        if (x == 3 && y == 20) {
                            // if we're at the last slot, we want to see if we still have alive players to show
                            int aliveLeft = 0;

                            for (int i = index; i < result.size(); i++) {
                                String currentEntry = result.get(i).getKey();
                                boolean dead = ChatColor.getLastColors(currentEntry).equals(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString());

                                if (!dead) {
                                    aliveLeft++;
                                }
                            }

                            if (aliveLeft != 0 && aliveLeft != 1) {
                                // if there are players we weren't able to show and if it's more than one
                                // (if it's only one they'll be shown as the last entry [see 17 lines above]), display the number
                                // of alive players we weren't able to show instead.
                                entries.add(new TabEntry(x, y, ChatColor.GREEN + "+" + aliveLeft));
                            }

                            break;
                        }

                        if (x == 3) {
                            x = 0;
                            y++;
                        }
                    }
                }                
            } else { // Normal Layout
                List<MatchTeam> teams = match.getTeams();
                // if it's one team versus another
                if (teams.size() == 2) {
                    // this method won't be called if the player isn't a participant
                    MatchTeam ourTeam = match.getTeam(player.getUniqueId());
                    MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

                    assert ourTeam != null;
                    boolean duel = ourTeam.getAllMembers().size() == 1 && otherTeam.getAllMembers().size() == 1;

                    {
                        // Column 1
                        // we handle duels a bit differently
                        if (!duel) {
                            entries.add(new TabEntry(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "Team " + ChatColor.GREEN + "(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")"));
                        } else {
                            entries.add(new TabEntry(0, 3, ChatColor.GREEN + ChatColor.BOLD.toString() + "You"));
                        }
                        renderTeamMemberOverviewEntries(entries, ourTeam, 0, ChatColor.GREEN);
                    }

                    {
                        // Column 3
                        // we handle duels a bit differently
                        if (!duel) {
                            entries.add(new TabEntry(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Enemies " + ChatColor.RED + "(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")"));
                        } else {
                            entries.add(new TabEntry(2, 3, ChatColor.RED + ChatColor.BOLD.toString() + "Opponent"));
                        }
                        renderTeamMemberOverviewEntries(entries, otherTeam, 2, ChatColor.RED);
                    }
                } else { // it's an FFA or something else like that
                    entries.add(new TabEntry(1, 3, ChatColor.BLUE + ChatColor.BOLD.toString() + "Party FFA"));

                    int x = 0;
                    int y = 4;

                    Map<String, Integer> otherEntries = new LinkedHashMap<>();

                    MatchTeam ourTeam = match.getTeam(player.getUniqueId());

                    {
                        // this is where we'll be adding our team members

                        Map<String, Integer> aliveLines = new LinkedHashMap<>();
                        Map<String, Integer> deadLines = new LinkedHashMap<>();

                        // separate lists to sort alive players before dead
                        // + color differently
                        assert ourTeam != null;
                        for (UUID teamMember : ourTeam.getAllMembers()) {
                            if (ourTeam.isAlive(teamMember)) {
                                aliveLines.put(ChatColor.GREEN + PotPvPRP.getInstance().getUuidCache().name(teamMember),  this.getPingOrDefault(teamMember));
                            } else {
                                deadLines.put("&7&m" + PotPvPRP.getInstance().getUuidCache().name(teamMember), this.getPingOrDefault(teamMember));
                            }
                        }

                        otherEntries.putAll(aliveLines);
                        otherEntries.putAll(deadLines);
                    }

                    {
                        // this is where we'll be adding everyone else
                        Map<String, Integer> deadLines = new LinkedHashMap<>();

                        for (MatchTeam otherTeam : match.getTeams()) {
                            if (otherTeam == ourTeam) {
                                continue;
                            }

                            // separate lists to sort alive players before dead
                            // + color differently
                            for (UUID enemy : otherTeam.getAllMembers()) {
                                if (otherTeam.isAlive(enemy)) {
                                    otherEntries.put(ChatColor.RED + PotPvPRP.getInstance().getUuidCache().name(enemy), this.getPingOrDefault(enemy));
                                } else {
                                    deadLines.put("&7&m" + PotPvPRP.getInstance().getUuidCache().name(enemy), this.getPingOrDefault(enemy));
                                }
                            }
                        }

                        otherEntries.putAll(deadLines);
                    }

                    List<Map.Entry<String, Integer>> result = new ArrayList<>(otherEntries.entrySet());

                    // actually display our entries
                    for (int index = 0; index < result.size(); index++) {
                        Map.Entry<String, Integer> entry = result.get(index);

                        entries.add(new TabEntry(x++, y, entry.getKey(), entry.getValue()));

                        if (x == 3 && y == 20) {
                            // if we're at the last slot, we want to see if we still have alive players to show
                            int aliveLeft = 0;

                            for (int i = index; i < result.size(); i++) {
                                String currentEntry = result.get(i).getKey();
                                boolean dead = ChatColor.getLastColors(currentEntry).equals(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString());

                                if (!dead) {
                                    aliveLeft++;
                                }
                            }

                            if (aliveLeft != 0 && aliveLeft != 1) {
                                // if there are players we weren't able to show and if it's more than one
                                // (if it's only one they'll be shown as the last entry [see 17 lines above]), display the number
                                // of alive players we weren't able to show instead.
                                entries.add(new TabEntry(x, y, ChatColor.GREEN + "+" + aliveLeft));
                            }

                            break;
                        }

                        if (x == 3) {
                            x = 0;
                            y++;
                        }
                    }
                }
            }
        }

        return entries;
    }

    // player first, leader next, then all other members
    private Set<UUID> getOrderedMembers(Player viewer, Party party) {
        Set<UUID> orderedMembers = Sets.newSetFromMap(new LinkedHashMap<>());
        UUID leader = party.getLeader();

        orderedMembers.add(viewer.getUniqueId());

        // if they're the leader we don't display them twice
        if (viewer.getUniqueId() != leader) {
            orderedMembers.add(leader);
        }

        for (UUID member : party.getMembers()) {
            // don't display the leader or the viewer again
            if (member == leader || member == viewer.getUniqueId()) {
                continue;
            }

            orderedMembers.add(member);
        }

        return orderedMembers;
    }
    
    private void renderTeamMemberOverviewEntries(List<TabEntry> entries, MatchTeam team, int column, ChatColor color) {
        List<Map.Entry<String, Integer>> result = new ArrayList<>(renderTeamMemberOverviewLines(team, color).entrySet());

        // how many spots we have left
        int spotsLeft = 20 - 4;

        // we could've used the 'start' variable, but we create a new one for readability.
        int y = 4;

        for (int index = 0; index < result.size(); index++) {
            Map.Entry<String, Integer> entry = result.get(index);

            // we check if we only have 1 more spot to show
            if (spotsLeft == 1) {
                // if so, count how many alive players we have left to show
                int aliveLeft = 0;

                for (int i = index; i < result.size(); i++) {
                    String currentEntry = result.get(i).getKey();
                    boolean dead = !ChatColor.getLastColors(currentEntry).equals(color.toString());

                    if (!dead) {
                        aliveLeft++;
                    }
                }

                // if we have any
                if (aliveLeft != 0) {
                    if (aliveLeft == 1) {
                        // if it's only one, we display them as the last entry
                        entries.add(new TabEntry(column, y, entry.getKey(), entry.getValue()));
                    } else {
                        // if it's more than one, display a number of how many we couldn't display.
                        entries.add(new TabEntry(column, y, color + "+" + aliveLeft));
                    }
                }

                break;
            }

            // if not, just display the entry.
            entries.add(new TabEntry(column, y, entry.getKey(), entry.getValue()));
            y++;
            spotsLeft--;
        }
    }

    private Map<String, Integer> renderTeamMemberOverviewLines(MatchTeam team, ChatColor aliveColor) {
        Map<String, Integer> aliveLines = new LinkedHashMap<>();
        Map<String, Integer> deadLines = new LinkedHashMap<>();

        for (UUID member : team.getAllMembers()) {
            int ping = this.getPingOrDefault(member);

            if (team.isAlive(member)) {
                aliveLines.put(aliveColor + PotPvPRP.getInstance().getUuidCache().name(member), ping);
            } else {
                deadLines.put("&7&m" + PotPvPRP.getInstance().getUuidCache().name(member), ping);
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();

        result.putAll(aliveLines);
        result.putAll(deadLines);

        return result;
    }
    
    public int getPingOrDefault(UUID member){
        Player partyPlayer = Bukkit.getPlayer(member);
        return partyPlayer == null ? 0 : partyPlayer.spigot().getPing();
    }
}
