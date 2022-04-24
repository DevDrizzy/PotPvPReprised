package net.frozenorb.potpvp.match.listener;

import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.util.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public final class MatchBuildListener implements Listener {

    private static final int SEARCH_RADIUS = 3;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            // BasicPreventionListener handles this
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        if (!match.getKitType().isBuildingAllowed() || match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
        } else {
            if (!match.canBeBroken(event.getBlock())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            // BasicPreventionListener handles this
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        if (!match.getKitType().isBuildingAllowed()) {
            event.setCancelled(true);
            return;
        }

        if (match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
            return;
        }

        if (!canBePlaced(event.getBlock(), match)) {
            player.sendMessage(ChatColor.RED + "You can't build here.");
            event.setCancelled(true);
            player.teleport(player.getLocation()); // teleport them back so they can't block-glitch
            return;
        }

        // apparently this is a problem
        if (event.getPlayer().getItemInHand().getType() == Material.FLINT_AND_STEEL && event.getBlockAgainst().getType() == Material.GLASS) {
            event.setCancelled(true);
            return;
        }

        match.recordPlacedBlock(event.getBlock());
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        if (!match.getKitType().isBuildingAllowed() || match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
            return;
        }

        if (!canBePlaced(event.getBlockClicked(), match)) {
            player.sendMessage(ChatColor.RED + "You can't build here.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        MatchHandler matchHandler = PotPvPRP.getInstance().getMatchHandler();

        for (Match match : matchHandler.getHostedMatches()) {
            if (!match.getArena().getBounds().contains(event.getBlock()) || !match.getKitType().isBuildingAllowed()) {
                continue;
            }

            match.recordPlacedBlock(event.getBlock());
            break;
        }
    }

    private boolean canBePlaced(Block placedBlock, Match match) {
        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -SEARCH_RADIUS; y <= SEARCH_RADIUS; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    Block current = placedBlock.getRelative(x, y, z);

                    if (current.isEmpty()) {
                        continue;
                    }

                    if (isBlacklistedBlock(current)) {
                        continue;
                    }

                    if (isBorderGlass(current, match)) {
                        continue;
                    }

                    if (!match.canBeBroken(current)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isBlacklistedBlock(Block block) {
        return block.isLiquid() || block.getType().name().contains("LOG") || block.getType().name().contains("LEAVES");
    }

    private boolean isBorderGlass(Block block, Match match) {
        if (block.getType() != Material.GLASS) {
            return false;
        }

        Cuboid cuboid = match.getArena().getBounds();

        // the reason we do a buffer of 3 blocks here is because sometimes
        // schematics aren't perfectly copied and the glass isn't exactly on the
        // limit of the arena.
        return (getDistanceBetween(block.getX(), cuboid.getLowerX()) <= 3 || getDistanceBetween(block.getX(), cuboid.getUpperX()) <= 3) || (getDistanceBetween(block.getZ(), cuboid.getLowerZ()) <= 3 || getDistanceBetween(block.getZ(), cuboid.getUpperZ()) <= 3);
    }

    private int getDistanceBetween(int x, int z) {
        return Math.abs(x - z);
    }

}