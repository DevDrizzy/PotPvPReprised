package net.frozenorb.potpvp.util;

import org.bukkit.Location;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LocationUtils {

    public static String locToStr(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }

}