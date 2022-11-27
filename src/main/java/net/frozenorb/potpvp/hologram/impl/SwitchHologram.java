package net.frozenorb.potpvp.hologram.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.hologram.PracticeHologram;
import net.frozenorb.potpvp.kit.kittype.KitType;
import org.bukkit.configuration.Configuration;
import xyz.refinedev.command.util.CC;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/21/2022
 * Project: potpvp-reprised
 */

@RequiredArgsConstructor
public class SwitchHologram extends PracticeHologram {


    private final PotPvPRP plugin;
    private KitType kit;

    /**
     * Spawn the hologram for all players on the server
     * at the given location in the constructor
     */
    public void spawn() {
        Preconditions.checkNotNull(this.meta, "Hologram Meta can not be null!");

        if (kit == null) {
            this.findNextKit();
        }

        Configuration config = plugin.getConfig();

        Hologram apiHologram = HologramsAPI.createHologram(plugin, meta.getLocation());
        apiHologram.clearLines();
        apiHologram.getVisibilityManager().setVisibleByDefault(true);
        if (!apiHologram.getLocation().getChunk().isLoaded()) {
            apiHologram.getLocation().getChunk().load();
        }

        for ( String line : config.getStringList("SETTINGS.KIT.LINES") ) {
            if (line.contains("<top>")) {
                int position = 1;
                for ( Map.Entry<String, Integer> entry : plugin.getEloHandler().topElo(kit).entrySet()) {

                    apiHologram.appendTextLine(config.getString("SETTINGS.KIT.FORMAT")
                            .replace("<number>", String.valueOf(position))
                            .replace("<value>", String.valueOf(entry.getValue()))
                            .replace("<name>", entry.getKey()));
                    position++;
                }
                continue;
            }

            String replace = line.replace("<kit>", kit.getDisplayName())
                    .replace("<update>", String.valueOf(updateIn));

            apiHologram.appendTextLine(CC.translate(replace));
        }

        meta.setHologram(apiHologram);
    }

    /**
     * DeSpawn the hologram for all players on the server
     * This method will only deSpawn the hologram but not delete,
     * so after a restart it will be back to its original location
     */
    public void deSpawn() {
        Hologram hologram = meta.getHologram();
        hologram.clearLines();
        hologram.delete();
    }

    /**
     * Update the hologram and its contents
     * respectively, this will change the hologram's kit
     * in the {@link SwitchHologram} otherwise it will update
     * the leaderboard being displayed
     */
    public void update() {
        this.deSpawn();
        this.findNextKit();
        this.spawn();
    }

    public void findNextKit() {
        List<KitType> kits =  KitType.getAllTypes().stream().filter(KitType::isSupportsRanked).collect(Collectors.toList());
        int index = kits.indexOf(kit);

        if (index + 2 >= kits.size()) {
            this.kit = kits.get(0);
        }

        this.kit = kits.get(index + 1);
    }
}
