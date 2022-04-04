package net.frozenorb.potpvp.duel;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.frozenorb.potpvp.kittype.KitType;

public final class PlayerDuelInvite extends DuelInvite<UUID> {

    public PlayerDuelInvite(Player sender, Player target, KitType kitType) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType);
    }

}