package net.frozenorb.potpvp.match.event;

import net.frozenorb.potpvp.match.Match;

import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a match is terminated (when its {@link net.frozenorb.potpvp.match.MatchState} changes
 * to {@link net.frozenorb.potpvp.match.MatchState#TERMINATED})
 * @see net.frozenorb.potpvp.match.MatchState#TERMINATED
 */
public final class MatchTerminateEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();


    public MatchTerminateEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}