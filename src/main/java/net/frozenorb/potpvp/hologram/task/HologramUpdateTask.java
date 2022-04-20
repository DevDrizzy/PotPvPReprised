package net.frozenorb.potpvp.hologram.task;

import lombok.RequiredArgsConstructor;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.hologram.HologramHandler;
import net.frozenorb.potpvp.hologram.PracticeHologram;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: PotPvPRP
 */

@RequiredArgsConstructor
public class HologramUpdateTask implements Runnable {

    private final PotPvPRP plugin;

    @Override
    public void run() {
        HologramHandler handler = plugin.getHologramHandler();

        for ( PracticeHologram hologram : handler.getHolograms() ) {
            if (hologram.updateIn <= 0) {
                hologram.update();
                return;
            }

            hologram.updateIn -= 1;
        }
    }
}
