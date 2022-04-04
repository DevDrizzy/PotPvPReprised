package net.frozenorb.potpvp.match.event;

import net.frozenorb.potpvp.match.Match;

import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a match's countdown starts (when its {@link net.frozenorb.potpvp.match.MatchState} changes
 * to {@link net.frozenorb.potpvp.match.MatchState#COUNTDOWN})
 * @see net.frozenorb.potpvp.match.MatchState#COUNTDOWN
 */
public final class MatchCountdownStartEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchCountdownStartEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}