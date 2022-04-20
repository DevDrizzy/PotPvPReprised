package net.frozenorb.potpvp.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class LocationUtils {

    public static String locToStr(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }

    public String serialize(@Nullable Location location) {
        if (location == null) return "empty";
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() +
                ":" + location.getYaw() + ":" + location.getPitch();
    }

    public Location deserialize(String source) {
        if (source == null) {
            return null;
        }

        String[] split = source.split(":");
        World world = Bukkit.getServer().getWorld(split[0]);

        if (world == null) {
            return null;
        }

        return new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

}