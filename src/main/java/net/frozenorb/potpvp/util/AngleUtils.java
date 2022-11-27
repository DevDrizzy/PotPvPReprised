package net.frozenorb.potpvp.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.EnumMap;
import java.util.Map;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public final class AngleUtils {

    private static final Map<BlockFace, Integer> NOTCHES = new EnumMap<>(BlockFace.class);

    static {
        BlockFace[] radials = {
                BlockFace.WEST,
                BlockFace.NORTH_WEST,
                BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST
        };

        for (int i = 0; i < radials.length; i++) {
            NOTCHES.put(radials[i], i);
        }
    }

    public static int faceToYaw(BlockFace face) {
        return wrapAngle(45 * NOTCHES.getOrDefault(face, 0));
    }

    private static int wrapAngle(int angle) {
        int wrappedAngle = angle;

        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }

        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

    public double yawDiff(double a, double b) {
        double mi = Math.min(a, b);
        double mx = Math.max(a, b);
        return Math.min(mx - mi, mi + 360 - mx);
    }

    public boolean faceTo(Location a, Location b) {
        double dx = b.getX() - a.getX();
        double dz = b.getZ() - a.getZ();
        double ang = Math.toDegrees(Math.acos(dz / Math.sqrt(dx * dx + dz * dz)));
        if (dx > 0) {
            ang = -ang;
        }
        return yawDiff(a.getYaw(), ang) <= 90;
    }

    public boolean isInRange(Player player, Player target, double range) {
        return player.getEyeLocation().distance(target.getLocation()) <= range || player.getLocation().distance(target.getLocation()) <= range;
    }

}