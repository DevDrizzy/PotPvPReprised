package net.frozenorb.potpvp.hologram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.hologram.impl.GlobalHologram;
import net.frozenorb.potpvp.hologram.impl.KitHologram;
import net.frozenorb.potpvp.hologram.task.HologramUpdateTask;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.util.LocationUtils;
import net.frozenorb.potpvp.util.config.impl.BasicConfigurationFile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: PotPvPRP
 */

@Getter
@RequiredArgsConstructor
public class HologramHandler {

    private final PotPvPRP plugin;
    private final BasicConfigurationFile config;
    private final List<PracticeHologram> holograms = new ArrayList<>();

    /**
     * Load and initiate holograms
     */
    public final void init() {
        ConfigurationSection section = config.getConfigurationSection("HOLOGRAMS");
        if (section == null || section.getKeys(false).isEmpty()) return;

        for ( String key : section.getKeys(false) ) {
            HologramType type;

            try {
                type = HologramType.valueOf(section.getString(key + ".TYPE"));
            } catch (Exception e) {
                plugin.consoleLog("&cInvalid Type in hologram " + section.getString(key) + ", skipping!");
                continue;
            }

            PracticeHologram hologram;
            ConfigurationSection hologramSection = section.getConfigurationSection(key);

            if (type == HologramType.KIT) {
                KitType kit = KitType.byId(section.getString(key + ".KIT"));
                hologram = new KitHologram(plugin, kit);
            } else {
                hologram = new GlobalHologram(plugin);
            }

            this.load(hologram, hologramSection, type);
            this.holograms.add(hologram);
        }
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new HologramUpdateTask(plugin), 20L, 20L);
    }

    /**
     * Load a Hologram's meta from config
     *
     * @param hologram {@link PracticeHologram} hologram
     * @param section  {@link ConfigurationSection} the config section of hologram
     * @param type     {@link HologramType} type of hologram
     */
    public final void load(PracticeHologram hologram, ConfigurationSection section, HologramType type) {
        HologramMeta meta = new HologramMeta();

        meta.setLocation(LocationUtils.deserialize(section.getString("LOCATION")));
        meta.setName(section.getName());
        meta.setWorld(meta.getLocation().getWorld());
        meta.setType(type);

        hologram.setMeta(meta);
        hologram.spawn();
    }

    /**
     * Save a hologram to the config
     *
     * @param hologram {@link PracticeHologram} hologram
     */
    public final void save(PracticeHologram hologram) {
        HologramMeta meta = hologram.getMeta();
        String path = "HOLOGRAMS." + meta.getName() + ".";

        config.set(path + "LOCATION", LocationUtils.serialize(meta.getLocation()));
        config.set(path + "TYPE", meta.getType().name());

        config.save();
    }

}
