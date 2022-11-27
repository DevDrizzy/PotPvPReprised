package net.frozenorb.potpvp.match.postmatchinv;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.kit.kittype.HealingMethod;

import net.frozenorb.potpvp.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

import lombok.Getter;

@AllArgsConstructor
public final class PostMatchPlayer {

    @Getter private final UUID playerUuid;
    @Getter private final String lastUsername;
    @Getter private final ItemStack[] armor;
    @Getter private final ItemStack[] inventory;
    @Getter private final List<PotionEffect> potionEffects;
    @Getter private final int hunger;
    @Getter private final int health; // out of 10
    @Getter private final transient HealingMethod healingMethodUsed;
    @Getter private final int totalHits;
    @Getter private final int longestCombo;
    @Getter private final int missedPots;
    @Getter private final int ping;

    public PostMatchPlayer(Player player, HealingMethod healingMethodUsed, int totalHits, int longestCombo, int missedPots) {
        this.playerUuid = player.getUniqueId();
        this.lastUsername = player.getName();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = ImmutableList.copyOf(player.getActivePotionEffects());
        this.hunger = player.getFoodLevel();
        this.health = (int) player.getHealth();
        this.healingMethodUsed = healingMethodUsed;
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
        this.missedPots = missedPots;
        this.ping = PlayerUtils.getPing(player);
    }
}