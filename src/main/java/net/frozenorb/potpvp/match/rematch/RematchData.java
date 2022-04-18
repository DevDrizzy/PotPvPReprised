package net.frozenorb.potpvp.match.rematch;

import com.google.common.base.Preconditions;

import net.frozenorb.potpvp.kit.kittype.KitType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@ToString
public final class RematchData {

    @Getter private final UUID sender;
    @Getter private final UUID target;
    @Getter private final KitType kitType;
    @Getter private final Instant expiresAt;
    @Getter private final String arenaName;

    RematchData(UUID sender, UUID target, KitType kitType, int durationSeconds, String arenaName) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.expiresAt = Instant.now().plusSeconds(durationSeconds);
        this.arenaName = Preconditions.checkNotNull(arenaName, "arenaName");
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public int getSecondsUntilExpiration() {
        return (int) ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
    }

}