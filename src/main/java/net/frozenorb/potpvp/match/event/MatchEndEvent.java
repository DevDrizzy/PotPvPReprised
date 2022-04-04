package net.frozenorb.potpvp.match.event;

import net.frozenorb.potpvp.match.Match;

import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a match is ended (when its {@link net.frozenorb.potpvp.match.MatchState} changes
 * to {@link net.frozenorb.potpvp.match.MatchState#ENDING})
 * @see net.frozenorb.potpvp.match.MatchState#ENDING
 */
public final class MatchEndEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchEndEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}