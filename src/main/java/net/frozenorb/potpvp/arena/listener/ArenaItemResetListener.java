package net.frozenorb.potpvp.arena.listener;

import com.google.common.collect.Sets;
import net.frozenorb.potpvp.arena.event.ArenaReleasedEvent;
import net.frozenorb.potpvp.util.Cuboid;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

/**
 * Remove dropped items when {@link net.frozenorb.potpvp.arena.Arena}s are released.
 */
public final class ArenaItemResetListener implements Listener {

    @EventHandler
    public void onArenaReleased(ArenaReleasedEvent event) {
        Set<Chunk> chunks = Sets.newHashSet();
        Cuboid bounds = event.getArena().getBounds();
        Location minPoint = bounds.getUpperSW();
        Location maxPoint = bounds.getLowerNE();
        World world = minPoint.getWorld();

        // definitely a better way to increment than += 1 but arenas
        // are small enough this doesn't matter
        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                chunks.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }

        // force load all chunks (can't iterate entities in an unload chunk)
        // that are at all covered by this map.
        chunks.forEach(chunk -> {
            if (!chunk.isLoaded()) chunk.load();

            for (Entity entity : chunk.getEntities()) {
                // if we remove all entities we might call .remove()
                // on a player (breaks a lot of things)
                if (entity instanceof Item && bounds.contains(entity)) {
                    entity.remove();
                }
            }
        });
    }

}