package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.util.FireworkEffectPlayer;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public final class MatchWizardListener implements Listener {

    private final FireworkEffectPlayer fireworkEffectPlayer = new FireworkEffectPlayer();

    @EventHandler(priority = EventPriority.MONITOR)
    // no ignoreCancelled = true because right click on air
    // events are by default cancelled (wtf Bukkit)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.DIAMOND_HOE || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null || !match.getKitType().getId().contains("WIZARD")) {
            return;
        }

        FireworkEffect effect = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build();

        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(snowball.getVelocity().multiply(2));

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= 100) {
                    cancel();
                    return;
                }

                if (snowball.isDead() || snowball.isOnGround()) {
                    for (Entity entity : snowball.getNearbyEntities(4, 4, 4)) {
                        MatchTeam entityTeam = match.getTeam(entity.getUniqueId());

                        if (entityTeam != null && !entityTeam.getAllMembers().contains(player.getUniqueId())) {
                            entity.setVelocity(entity.getLocation().toVector().subtract(snowball.getLocation().toVector()).normalize().add(new Vector(0, 0.7, 0.0)));
                        }
                    }

                    snowball.remove();
                    cancel();
                } else {
                    try {
                        fireworkEffectPlayer.playFirework(snowball.getWorld(), snowball.getLocation(), effect);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }.runTaskTimer(PotPvPRP.getInstance(), 1L, 1L);
    }

}