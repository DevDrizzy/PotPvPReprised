package net.frozenorb.potpvp.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 3/9/2022
 * Project: PotPvPRP
 */

@Data
public class HologramMeta {

    private final UUID uniqueId;
    private Hologram hologram;
    private Location location;
    private String name;
    private HologramType type;
}
