package net.frozenorb.potpvp.match.event;

import net.frozenorb.potpvp.match.Match;

import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a match's countdown ends (when its {@link net.frozenorb.potpvp.match.MatchState} changes
 * to {@link net.frozenorb.potpvp.match.MatchState#IN_PROGRESS})
 * @see net.frozenorb.potpvp.match.MatchState#IN_PROGRESS
 */
public final class MatchStartEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchStartEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}