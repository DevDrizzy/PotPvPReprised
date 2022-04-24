package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class GoldenHeadListener implements Listener {

    private static final int HEALING_POINTS = 8; // half hearts, so 4 hearts
    private static final ItemStack GOLDEN_HEAD = ItemBuilder.of(Material.GOLDEN_APPLE)
            .name("&6&lGolden Head")
            .build();

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        if (matches(item)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, HEALING_POINTS * 25, 1), true);
        }
    }

    private boolean matches(ItemStack item) {
        return GOLDEN_HEAD.isSimilar(item);
    }

}