package net.frozenorb.potpvp.hologram;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.hologram.task.HologramUpdateTask;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
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
public final class HologramHandler {

    private static final String HOLOGRAMS_FILE_NAME = "holograms.json";
    private final List<PracticeHologram> holograms = new ArrayList<>();

    /**
     * Load and initiate holograms
     */
    public HologramHandler() {
        File folder = PotPvPRP.getInstance().getDataFolder();
        File hologramsFile = new File(folder, HOLOGRAMS_FILE_NAME);

        try {
            // parsed as a List<PracticeHologram>
            if (hologramsFile.exists()) {
                try (Reader hologramReader = Files.newReader(hologramsFile, Charsets.UTF_8)) {
                    Type hologramListType = new TypeToken<List<PracticeHologram>>(){}.getType();
                    List<PracticeHologram> hologramList = PotPvPRP.getGson().fromJson(hologramReader, hologramListType);
                    hologramList.forEach(PracticeHologram::spawn);
                    this.holograms.addAll(hologramList);
                }
            }
        } catch (Exception e) {
            // Can't recover from this lol
            throw new RuntimeException(e);
        }

        Bukkit.getScheduler().runTaskTimer(PotPvPRP.getInstance(), new HologramUpdateTask(), 20L, 20L);
    }

    /**
     * Save all holograms to the config
     */
    public void save() throws IOException {
        Files.write(
                PotPvPRP.getGson().toJson(holograms),
                new File(PotPvPRP.getInstance().getDataFolder(), HOLOGRAMS_FILE_NAME),
                Charsets.UTF_8
        );
    }

    public void delete(PracticeHologram hologram) {
        hologram.deSpawn();
        this.holograms.remove(hologram);

        try {
            this.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final PracticeHologram getByName(String name) {
        for ( PracticeHologram hologram : this.holograms ) {
            HologramMeta meta = hologram.getMeta();
            if (meta.getName().equals(name)) return hologram;
        }
        return null;
    }

}
