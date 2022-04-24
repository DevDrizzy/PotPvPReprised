package net.frozenorb.potpvp.lobby.listener;

import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

public class LobbyParkourListener implements Listener {

    @Getter private static final Map<UUID, Parkour> parkourMap = Maps.newHashMap();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();

            if (block.getType() == Material.IRON_PLATE) {
                event.setCancelled(true);
                if (block.getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK) {
                    if (Parkour.getStartingCheckpoint() == null) {
                        Parkour.setStartingCheckpoint(new Parkour.Checkpoint(block.getLocation()));
                    }

                    Parkour parkour = parkourMap.get(player.getUniqueId());
                    if (parkour == null) {
                        parkour = new Parkour();

                        parkour.getCheckpoint().ring(player);

                        parkourMap.put(player.getUniqueId(), parkour);
                        player.sendMessage(ChatColor.YELLOW + "You've started the " + ChatColor.GREEN + "parkour" + ChatColor.YELLOW + " challenge!");
                    }

                } else if (block.getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_BLOCK) {
                    Parkour parkour = parkourMap.get(player.getUniqueId());
                    if (parkour != null) {
                        player.sendMessage(ChatColor.YELLOW + "You've finished the " + ChatColor.GREEN + "parkour" + ChatColor.YELLOW + " challenge!");
                        parkour.getCheckpoint().ring(player);
                        parkourMap.remove(player.getUniqueId());
                    }
                }
            } else if (block.getType() == Material.GOLD_PLATE) {
                event.setCancelled(true);
                Parkour parkour = parkourMap.get(player.getUniqueId());
                if (parkour != null) {
                    Parkour.Checkpoint checkpoint = parkour.getCheckpoint();
                    if (!checkpoint.getLocation().equals(block.getLocation())) {
                        parkour.setCheckpoint(new Parkour.Checkpoint(block.getLocation()));
                        parkour.getCheckpoint().ring(player);
                        player.sendMessage(ChatColor.YELLOW + "You've reached a new" + ChatColor.GOLD + " checkpoint" + ChatColor.YELLOW + "!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        parkourMap.remove(event.getPlayer().getUniqueId());
    }

    public static class Parkour {

        private static Checkpoint startingCheckpoint;

        @Getter private final long timeStarted;
        @Getter @Setter private Checkpoint checkpoint;

        public Parkour() {
            this.timeStarted = System.currentTimeMillis();
            if (startingCheckpoint != null) {
                checkpoint = startingCheckpoint;
            }
        }

        public static class Checkpoint {
            @Getter private final Location location;

            public Checkpoint(Location location) {
                this.location = location;
            }

            public void ring(Player player) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 0);
            }

        }

        public static Checkpoint getStartingCheckpoint() {
            return startingCheckpoint;
        }

        public static void setStartingCheckpoint(Checkpoint startingCheckpoint) {
            Parkour.startingCheckpoint = startingCheckpoint;
        }
    }
}
