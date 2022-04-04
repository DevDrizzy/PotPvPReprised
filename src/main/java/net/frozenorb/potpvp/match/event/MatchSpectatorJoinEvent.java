package net.frozenorb.potpvp.match.event;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.match.Match;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a spectator starts spectating a {@link Match}.
 * It should be noted that this event WILL be called for dead
 * players who immediately begin spectating a match.
 */
public final class MatchSpectatorJoinEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final Player spectator;

    public MatchSpectatorJoinEvent(Player spectator, Match match) {
        super(match);

        this.spectator = Preconditions.checkNotNull(spectator, "spectator");
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}