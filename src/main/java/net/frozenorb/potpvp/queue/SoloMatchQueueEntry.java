package net.frozenorb.potpvp.queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents a single player waiting
 * in a {@link MatchQueue}
 */
public final class SoloMatchQueueEntry extends MatchQueueEntry {

    @Getter private final UUID player;

    SoloMatchQueueEntry(MatchQueue queue, UUID player) {
        super(queue);

        this.player = Preconditions.checkNotNull(player, "player");
    }

    @Override
    public Set<UUID> getMembers() {
        return ImmutableSet.of(player);
    }

}