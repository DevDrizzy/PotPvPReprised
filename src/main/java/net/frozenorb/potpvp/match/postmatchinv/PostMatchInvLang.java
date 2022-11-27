package net.frozenorb.potpvp.match.postmatchinv;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.MatchTeam;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public final class PostMatchInvLang {

    static final String LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------";
    static final String INVENTORY_HEADER = ChatColor.GOLD + "Post-Match Inventories " + ChatColor.GRAY + "(click name to view)";

    private static final String WINNER = ChatColor.GREEN + "Winner:" + ChatColor.GRAY;
    private static final String LOSER = ChatColor.RED + "Loser:" + ChatColor.GRAY;
    private static final String PARTICIPANTS = ChatColor.GREEN + "Participants:";

    private static final TextComponent COMMA_COMPONENT = new TextComponent(", ");

    static {
        COMMA_COMPONENT.setColor(ChatColor.YELLOW);
    }

    static Object[] gen1v1PlayerInvs(UUID winner, UUID loser) {
        return new Object[]{
                new TextComponent[]{
                        new TextComponent(ChatColor.GREEN + "Winner: "),
                        clickToViewLine(winner),
                        new TextComponent(ChatColor.GRAY + " - " + ChatColor.RED + "Loser: "),
                        clickToViewLine(loser)
                }
        };
    }

    // when viewing a 2 team match as a spectator
    static Object[] genSpectatorInvs(MatchTeam winner, MatchTeam loser) {
        return new Object[]{
                WINNER,
                clickToViewLine(winner.getAllMembers()),
                LOSER,
                clickToViewLine(loser.getAllMembers()),
        };
    }

    // when viewing a 2 team match as a participant
    static Object[] genTeamInvs(MatchTeam viewer, MatchTeam winner, MatchTeam loser) {
        return new Object[]{
                WINNER + (viewer == winner ? " (Your team)" : " (Enemy team)"),
                clickToViewLine(winner.getAllMembers()),
                LOSER + (viewer == loser ? " (Your team)" : " (Enemy team)"),
                clickToViewLine(loser.getAllMembers()),
        };
    }

    // when viewing a non-2 team match from any perspective
    static Object[] genGenericInvs(Collection<MatchTeam> teams) {
        Set<UUID> members = teams.stream()
                .flatMap(t -> t.getAllMembers().stream())
                .collect(Collectors.toSet());

        return new Object[]{
                PARTICIPANTS,
                clickToViewLine(members),
        };
    }

    private static TextComponent clickToViewLine(UUID member) {
        String memberName = PotPvPRP.getInstance().getUuidCache().name(member);
        TextComponent component = new TextComponent();

        component.setText(memberName);
        component.setColor(ChatColor.YELLOW);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view inventory of " + ChatColor.GOLD + memberName).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/_ " + member.toString()));

        return component;
    }

    private static TextComponent[] clickToViewLine(Set<UUID> members) {
        List<TextComponent> components = new ArrayList<>();

        for (UUID member : members) {
            components.add(clickToViewLine(member));
            components.add(COMMA_COMPONENT);
        }

        components.remove(components.size() - 1); // remove trailing comma
        return components.toArray(new TextComponent[components.size()]);
    }


}