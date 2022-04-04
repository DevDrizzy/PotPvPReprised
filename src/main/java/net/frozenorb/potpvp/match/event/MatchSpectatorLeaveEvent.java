package net.frozenorb.potpvp.match.event;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.match.Match;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a spectator stops spectating a {@link Match}.
 * This event will be called for spectators who disconnect, /leave,
 * etc. This event will not be called for spectators 'leaving' a match as it ends.
 */
public final class MatchSpectatorLeaveEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final Player spectator;

    public MatchSpectatorLeaveEvent(Player spectator, Match match) {
        super(match);

        this.spectator = Preconditions.checkNotNull(spectator, "spectator");
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}