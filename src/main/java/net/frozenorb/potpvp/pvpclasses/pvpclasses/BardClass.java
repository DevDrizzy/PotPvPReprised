package net.frozenorb.potpvp.pvpclasses.pvpclasses;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.pvpclasses.PvPClass;
import net.frozenorb.potpvp.pvpclasses.PvPClassHandler;
import net.frozenorb.potpvp.pvpclasses.pvpclasses.bard.BardEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BardClass extends PvPClass implements Listener {

    /*
            Things commented with // CUSTOM
            are the 'unique' abilities, or things that have custom behaviour not seen by most other effects.
            An example is invis, whose passive cannot be used while its click is on cooldown.
            This is therefore commented with // CUSTOM
     */

    public final Map<Material, BardEffect> BARD_CLICK_EFFECTS = new HashMap<>();
    public final Map<Material, BardEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();

    @Getter private static Map<String, Long> lastEffectUsage = new ConcurrentHashMap<>();
    @Getter private static Map<String, Float> energy = new ConcurrentHashMap<>();
    private static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);

    public static final int BARD_RANGE = 20;
    public static final int EFFECT_COOLDOWN = 10 * 1000;
    public static final float MAX_ENERGY = 100;
    public static final float ENERGY_REGEN_PER_SECOND = 1;

    public BardClass() {
        super("Bard", 15, "GOLD_", null);

        // Click buffs
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 20));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25));
        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40));
        //BARD_CLICK_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, BardEffect.fromEnergy(60));
        BARD_CLICK_EFFECTS.put(Material.WHEAT, BardEffect.fromEnergy(25));

        // Click debuffs
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 35));

        // Passive buffs
        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));
        //BARD_PASSIVE_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 0)));

        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (player == null) return;
                    if (!PvPClassHandler.hasKitOn(player, BardClass.this)) {
                        continue;
                    }

                    if (energy.containsKey(player.getName())) {
                        if (energy.get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }

                        energy.put(player.getName(), Math.min(MAX_ENERGY, energy.get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        energy.put(player.getName(), 0F);
                    }

                    int manaInt = energy.get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + "Bard Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }

        }.runTaskTimer(PotPvPRP.getInstance(), 15L, 20L);
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(Player player) {
        if (!(this.qualifies(player.getInventory()))) {
            super.tick(player);
            return;
        }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        }

        if (player.getItemInHand() != null && BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType())) {
            // CUSTOM
            if (player.getItemInHand().getType() == Material.FERMENTED_SPIDER_EYE && getLastEffectUsage().containsKey(player.getName()) && getLastEffectUsage().get(player.getName()) > System.currentTimeMillis()) {
                return;
            }

            giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
        }
        super.tick(player);
    }


    @Override
    public void remove(Player player) {
        energy.remove(player.getName());

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this) || !energy.containsKey(event.getPlayer().getName())) {
            return;
        }

        if (getLastEffectUsage().containsKey(event.getPlayer().getName()) && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        BardEffect bardEffect = BARD_CLICK_EFFECTS.get(event.getItem().getType());

        if (bardEffect.getEnergy() > energy.get(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough energy for this! You need " + bardEffect.getEnergy() + " energy, but you only have " + energy.get(event.getPlayer().getName()).intValue());
            return;
        }

        energy.put(event.getPlayer().getName(), energy.get(event.getPlayer().getName()) - bardEffect.getEnergy());

        boolean negative = bardEffect.getPotionEffect() != null && DEBUFFS.contains(bardEffect.getPotionEffect().getType());

        getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        giveBardEffect(event.getPlayer(), bardEffect, !negative, true);

        if (event.getPlayer().getItemInHand().getAmount() == 1) {
            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            event.getPlayer().updateInventory();
        } else {
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
        }
    }

    public void giveBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean persistOldValues) {
        for (Player player : getNearbyPlayers(source, friendly)) {

            // CUSTOM
            // Bards can't get Strength.
            // Yes, that does need to use .equals. PotionEffectType is NOT an enum.
            if (PvPClassHandler.hasKitOn(player, this) && bardEffect.getPotionEffect() != null && bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                continue;
            }

            if (bardEffect.getPotionEffect() != null) {
                smartAddPotion(player, bardEffect.getPotionEffect(), persistOldValues, this);
            } else {
                Material material = source.getItemInHand().getType();
                giveCustomBardEffect(player, material);
            }
        }
    }

    public void giveCustomBardEffect(Player player, Material material) {
        switch (material) {
            case WHEAT:
                for (Player nearbyPlayer : getNearbyPlayers(player, true)) {
                    nearbyPlayer.setFoodLevel(20);
                    nearbyPlayer.setSaturation(10F);
                }

                break;
            case FERMENTED_SPIDER_EYE:


                break;
            default:
                PotPvPRP.getInstance().getLogger().warning("No custom Bard effect defined for " + material + ".");
        }
    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Match match = PotPvPRP.getInstance().getMatchHandler().getMatchPlaying(player);
        MatchTeam sourceTeam = match.getTeam(player.getUniqueId());

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(BARD_RANGE, BARD_RANGE / 2, BARD_RANGE)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                boolean isFriendly = sourceTeam.getAliveMembers().contains(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

}